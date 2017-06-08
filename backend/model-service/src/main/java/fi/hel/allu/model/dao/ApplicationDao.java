package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.CustomerRoleType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static com.querydsl.sql.SQLExpressions.selectDistinct;
import static com.querydsl.sql.SQLExpressions.union;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApplicationAttachment.applicationAttachment;
import static fi.hel.allu.QApplicationCustomer.applicationCustomer;
import static fi.hel.allu.QApplicationCustomerContact.applicationCustomerContact;
import static fi.hel.allu.QApplicationReminder.applicationReminder;
import static fi.hel.allu.QApplicationTag.applicationTag;
import static fi.hel.allu.QAttachment.attachment;
import static fi.hel.allu.QContact.contact;
import static fi.hel.allu.QDefaultAttachment.defaultAttachment;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QLocationGeometry.locationGeometry;
import static fi.hel.allu.QPostalAddress.postalAddress;
import static fi.hel.allu.QRecurringPeriod.recurringPeriod;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class ApplicationDao {

  /** Fields that won't be updated in regular updates */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS =
      Arrays.asList(application.status, application.decisionMaker, application.decisionTime, application.creationTime,
          application.metadataVersion, application.handler);

  private SQLQueryFactory queryFactory;
  private ApplicationSequenceDao applicationSequenceDao;
  private StructureMetaDao structureMetaDao;
  private DistributionEntryDao distributionEntryDao;
  private CustomerDao customerDao;

  final QBean<Application> applicationBean = bean(Application.class, application.all());
  final QBean<ApplicationTag> applicationTagBean = bean(ApplicationTag.class, applicationTag.all());

  @Autowired
  public ApplicationDao(
      SQLQueryFactory queryFactory,
      ApplicationSequenceDao applicationSequenceDao,
      DistributionEntryDao distributionEntryDao,
      StructureMetaDao structureMetaDao,
      CustomerDao customerDao) {
    this.queryFactory = queryFactory;
    this.applicationSequenceDao = applicationSequenceDao;
    this.distributionEntryDao = distributionEntryDao;
    this.structureMetaDao = structureMetaDao;
    this.customerDao = customerDao;
  }

  @Transactional(readOnly = true)
  public List<Application> findByIds(List<Integer> ids) {
    List<Application> appl = queryFactory.select(applicationBean).from(application).where(application.id.in(ids)).fetch();
    return populateDependencies(appl);
  }

  @Transactional(readOnly = true)
  public List<Application> findByProject(int projectId) {
    List<Application> applications =
        queryFactory.select(applicationBean).from(application).where(application.projectId.eq(projectId)).fetch();
    return populateDependencies(applications);
  }

  @Transactional(readOnly = true)
  public List<Application> findByLocation(LocationSearchCriteria lsc) {
    ZonedDateTime recurringStartTime = RecurringApplication.BEGINNING_1972_DATE;
    ZonedDateTime recurringEndTime = RecurringApplication.MAX_END_TIME;

    BooleanExpression strictStartEndTimeCondition = null;
    if (lsc.getAfter() != null) {
      strictStartEndTimeCondition = andExpression(strictStartEndTimeCondition, application.endTime.after(lsc.getAfter()));
      recurringStartTime = lsc.getAfter();
    }
    if (lsc.getBefore() != null) {
      strictStartEndTimeCondition = andExpression(strictStartEndTimeCondition, application.startTime.before(lsc.getBefore()));
      recurringEndTime = lsc.getBefore();
    }

    RecurringApplication recurringApplication =
        new RecurringApplication(recurringStartTime, recurringEndTime, recurringEndTime);
    BooleanExpression recurringCondition =
        // application start and recurring end time has to intersect search period
        application.startTime.before(TimeUtil.millisToZonedDateTime(recurringApplication.getEndTime()))
            .and(application.recurringEndTime.after(TimeUtil.millisToZonedDateTime(recurringApplication.getStartTime())))
            // recurring period checks
            .and(overlapsPeriod(recurringApplication.getPeriod1Start(), recurringApplication.getPeriod1End())
                .or(overlapsPeriod(recurringApplication.getPeriod2Start(), recurringApplication.getPeriod2End()))
            );
    BooleanExpression geometryExpression = locationGeometry.geometry.intersects(lsc.getIntersects());

    if (strictStartEndTimeCondition != null) {
      // Time based where conditions are used only if at least start or end time is defined in search query.
      // Both recurring and start/end time conditions are checked, because only recurring applications have recurring_period rows in database
      recurringCondition = recurringCondition.or(strictStartEndTimeCondition);
      geometryExpression = geometryExpression.and(recurringCondition);
    }

    BooleanExpression condition =
        application.id.in(selectDistinct(location.applicationId)
            .from(locationGeometry)
            .join(location).on(locationGeometry.locationId.eq(location.id))
            .join(application).on(location.applicationId.eq(application.id))
            .leftJoin(recurringPeriod).on(recurringPeriod.applicationId.eq(application.id))
            .where(geometryExpression));

    List<Application> applications =
        queryFactory.select(applicationBean).from(application).where(condition).fetch();

    return populateDependencies(applications);
  }

  /**
   * Finds applications related to the given customer with the role customer has within the application.
   *
   * @param id    Id of the customer whose applications are fetched.
   * @return List of related application ids and roles of the customer within the application. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public Map<Integer, List<CustomerRoleType>> findByCustomer(int id) {
    return queryFactory
        .select(applicationCustomer.applicationId, applicationCustomer.customerRoleType)
        .from(applicationCustomer)
        .where(applicationCustomer.customerId.eq(id))
        .transform(groupBy(applicationCustomer.applicationId).as(list(applicationCustomer.customerRoleType)));
  }


  /**
   * Find applications that are about to end
   * <p>
   * This method returns IDs of all the applications that are about to end
   * within the given number of days. It can also filter these applications and
   * only return applications that have some of the given specifiers set.
   *
   * @param endsAfter         Applications must end after this time
   * @param endsBefore        Applications must end before this time
   * @param applicationTypes  Selector for application types. If this list is given
   *                          and not empty, the applications must also be of a type
   *                          that's included in the given specifiers.
   * @param statusTypes       Selector for application status. If this list is given
   *                          and not empty, the results are also filtered by application
   *                          status: applications must be in a status that's included in
   *                          the list.
   * @return List of application identifiers.
   */
  @Transactional(readOnly = true)
  public List<Integer> findByEndTime(ZonedDateTime endsAfter, ZonedDateTime endsBefore,
      List<ApplicationType> applicationTypes, List<StatusType> statusTypes) {
    BooleanExpression whereCondition = application.endTime.between(endsAfter, endsBefore);
    if (applicationTypes != null && ! applicationTypes.isEmpty()) {
      whereCondition = whereCondition.and(application.type.in(applicationTypes));
    }
    if (statusTypes != null && ! statusTypes.isEmpty()) {
      whereCondition = whereCondition.and(application.status.in(statusTypes));
    }
    List<Integer> applications = queryFactory.select(application.id).from(application).where(whereCondition).fetch();
    return applications;
  }

  /**
   * Given a list of application ids, filter out those for which a reminder has
   * already been sent
   *
   * @param  applications   list of application IDs to check
   * @return those application IDs that don't have a reminder sent
   */
  @Transactional(readOnly = true)
  public List<Integer> excludeSentReminders(List<Integer> applications) {
    final Set<Integer> sentReminders = new HashSet<>(
        queryFactory.select(applicationReminder.applicationId).from(applicationReminder).join(application)
            .on(applicationReminder.applicationId.eq(application.id)).where(applicationReminder.applicationId
                .in(applications).and(applicationReminder.reminderTrigger.eq(application.endTime)))
            .fetch());
    return applications.stream().filter(i -> !sentReminders.contains(i)).collect(Collectors.toList());
  }

  /**
   * Mark the given applications to have a reminder set. For every given
   * application ID, an entry with application's ID and current end date is
   * added to applicationReminder table. Existing reminder entries for these
   * applications are removed.
   *
   * @param  applications   list of application IDs
   * @return number of inserted applicationReminder entries
   */
  @Transactional
  public long markReminderSent(List<Integer> applications) {
    queryFactory.delete(applicationReminder).where(applicationReminder.applicationId.in(applications)).execute();
    long inserted = queryFactory.insert(applicationReminder)
        .columns(applicationReminder.applicationId, applicationReminder.reminderTrigger)
        .select(SQLExpressions
            .select(application.id, application.endTime).from(application).where(application.id.in(applications)))
        .execute();
    return inserted;
  }

  /**
   * Find all contacts of applications having given contact.
   *
   * @param ids Contact ids to be searched.
   * @return  all contacts of applications having given contacts.
   */
  @Transactional(readOnly = true)
  public List<ApplicationWithContacts> findRelatedApplicationsWithContacts(List<Integer> ids) {
    // find all application_customer rows, which have link to search contacts through application_customer_contact
    SubQueryExpression<Integer> sq =
        select(applicationCustomer.id)
            .from(applicationCustomer)
            .join(applicationCustomerContact).on(applicationCustomer.id.eq(applicationCustomerContact.applicationCustomerId))
            .where(applicationCustomerContact.contactId.in(ids));

    List<Expression<?>> mappedExpressions = new ArrayList<>(Arrays.asList(contact.all()));
    mappedExpressions.add(bean(PostalAddress.class, postalAddress.all()).as("postalAddress"));
    // find all application_customer rows with all their contacts (there's normally more distinct contacts in this set than what were
    // searched originally in the sub query's in condition)
    Map<List<?>, List<Contact>> appIdRoleToContacts = queryFactory
        .select(applicationCustomer.applicationId, applicationCustomer.customerRoleType, bean(Contact.class, contact.all()))
        .from(applicationCustomerContact)
        .join(applicationCustomer).on(applicationCustomerContact.applicationCustomerId.eq(applicationCustomer.id))
        .join(contact).on(applicationCustomerContact.contactId.eq(contact.id))
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(applicationCustomer.id.in(sq))
        .transform(groupBy(applicationCustomer.applicationId, applicationCustomer.customerRoleType)
            .as(list(bean(Contact.class, mappedExpressions.toArray(new Expression[0])))));

    return appIdRoleToContacts.entrySet().stream()
        .map(entry -> new ApplicationWithContacts((Integer) entry.getKey().get(0), (CustomerRoleType) entry.getKey().get(1), entry.getValue()))
        .collect(Collectors.toList());
  }


  @Transactional
  public Application insert(Application appl) {
    appl.setApplicationId(createApplicationId(appl.getType()));
    appl.setStatus(StatusType.PENDING);
    appl.setCreationTime(ZonedDateTime.now());
    appl.setMetadataVersion(structureMetaDao.getLatestMetadataVersion());
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    insertDistributionEntries(id, appl.getDecisionDistributionList());
    replaceCustomersWithContacts(id, appl.getCustomersWithContacts());
    Application application = findByIds(Collections.singletonList(id)).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    replaceRecurringPeriods(application);
    return populateTags(application);
  }

  /**
   * Delete note-type application and its related data
   *
   * @param id application's database ID.
   */
  @SuppressWarnings("unchecked")
  @Transactional
  public void deleteNote(int id) {
    ApplicationType applicationType = queryFactory.select(application.type).from(application)
        .where(application.id.eq(id)).fetchOne();
    if (ApplicationType.NOTE != applicationType) {
      throw new IllegalArgumentException("Trying to delete non-note application");
    }
    queryFactory.delete(application).where(application.id.eq(id)).execute();
    /*
     * After removing the application, some attachments might be unreferenced,
     * so we need a cleanup action for them (attachment doesn't reference
     * application directly, so ON DELETE CASCADE can't be used to automatically
     * clean it up):
     */
    queryFactory.delete(attachment)
        .where(attachment.id.notIn(
            union(select(applicationAttachment.attachmentId).from(applicationAttachment),
                select(defaultAttachment.attachmentId).from(defaultAttachment))))
        .execute();
  }

  /**
   * Updates handler of given applications.
   *
   * @param handler New handler set to the applications.
   * @param applications Applications whose handler is updated.
   * @return Number of updated applications.
   */
  @Transactional
  public int updateHandler(int handler, List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.handler, handler)
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  @Transactional
  public int removeHandler(List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.handler, Expressions.nullExpression())
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  /**
   * Update application's decision.
   *
   * @param applicationId   Id of the application to be updated.
   * @param status          New status.
   * @return  Updated application.
   */
  @Transactional
  public Application updateDecision(int applicationId, StatusType status, int userId) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.decisionMaker, userId)
        .set(application.status, status)
        .set(application.decisionTime, ZonedDateTime.now())
        .where(application.id.eq(applicationId))
        .execute();
    if (updated != 1) {
      throw new NoSuchEntityException("Attempted to update decision status of non-existent application", Integer.toString(applicationId));
    }
    return findByIds(Collections.singletonList(applicationId)).get(0);
  }

  /**
   * Update application status.
   *
   * @param applicationId   Application to be updated
   * @param status          New status (cannot change status to either decision state).
   * @return  Updated application.
   */
  public Application updateStatus(int applicationId, StatusType status) {
    if (StatusType.DECISION.equals(status) || StatusType.REJECTED.equals(status)) {
      throw new IllegalArgumentException("Cannot set status to any decision state. Use updateDecision()");
    }
    int updated = (int) queryFactory
        .update(application)
        .set(application.status, status)
        .where(application.id.eq(applicationId))
        .execute();
    if (updated != 1) {
      throw new NoSuchEntityException("Attempted to update status of non-existent application", Integer.toString(applicationId));
    }
    return findByIds(Collections.singletonList(applicationId)).get(0);
  }

  /**
   * Updates project of the given applications.
   *
   * @param   projectId     New project set to the applications. May be <code>null</code>.
   * @param   applications  Applications whose project is updated.
   *
   * @return  Number of updated applications.
   */
  @Transactional
  public int updateProject(Integer projectId, List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.projectId, projectId)
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  /**
   * Update application. All other fields are taken from appl, but {@link #UPDATE_READ_ONLY_FIELDS}
   */
  @Transactional
  public Application update(int id, Application appl) {
    appl.setId(id);
    long changed = queryFactory.update(application)
        .populate(appl,
            new ExcludingMapper(WITH_NULL_BINDINGS,
                UPDATE_READ_ONLY_FIELDS))
        .where(application.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    distributionEntryDao.deleteByApplication(id);
    insertDistributionEntries(id, appl.getDecisionDistributionList());
    replaceCustomersWithContacts(id, appl.getCustomersWithContacts());
    Application application = findByIds(Collections.singletonList(id)).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    replaceRecurringPeriods(application);
    return populateTags(application);
  }

  String createApplicationId(ApplicationType applicationType) {
    long seqValue = applicationSequenceDao
        .getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType));
    return ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType).name() + seqValue;
  }

  private void insertDistributionEntries(Integer id, List<DistributionEntry> decisionDistributionList) {
    if (decisionDistributionList != null) {
      // make sure id is not set and nothing is updated in wrong application
      decisionDistributionList.forEach(dEntry -> { dEntry.setId(null); dEntry.setApplicationId(id); });
      distributionEntryDao.insert(decisionDistributionList);
    }
  }

  private List<Application> populateDependencies(List<Application> applications) {
    applications.forEach(a -> a.setDecisionDistributionList(distributionEntryDao.findByApplicationId(a.getId())));
    applications.forEach(a -> populateTags(a));
    applications.forEach(a -> a.setCustomersWithContacts(customerDao.findByApplicationWithContacts(a.getId())));
    return applications;
  }

  private Application populateTags(Application application) {
    application.setApplicationTags(findTagsByApplicationId(application.getId()));
    return application;
  }

  private List<ApplicationTag> findTagsByApplicationId(Integer applicationId) {
    List<ApplicationTag> applicationTags =
        queryFactory.select(applicationTagBean).from(applicationTag).where(applicationTag.applicationId.eq(applicationId)).fetch();
    return applicationTags;
  }

  private void replaceApplicationTags(Integer applicationId, List<ApplicationTag> tags) {
    queryFactory.delete(applicationTag).where(applicationTag.applicationId.eq(applicationId)).execute();
    if (tags != null && !tags.isEmpty()) {
      tags.forEach(tag -> tag.setApplicationId(applicationId));
      tags.forEach(tag -> queryFactory.insert(applicationTag).populate(tag).execute());
    }
  }

  private void replaceRecurringPeriods(Application application) {
    queryFactory.delete(recurringPeriod).where(recurringPeriod.applicationId.eq(application.getId())).execute();
    if (hasValidRecurringEndTime(application)) {
      RecurringApplication recurringApplication =
          new RecurringApplication(application.getStartTime(), application.getEndTime(), application.getRecurringEndTime());
      RecurringPeriod period1 = new RecurringPeriod(
          application.getId(),
          TimeUtil.millisToZonedDateTime(recurringApplication.getPeriod1Start()),
          TimeUtil.millisToZonedDateTime(recurringApplication.getPeriod1End()));
      queryFactory.insert(recurringPeriod).populate(period1).execute();
      if (recurringApplication.getPeriod2End() != 0) {
        RecurringPeriod period2 = new RecurringPeriod(
            application.getId(),
            TimeUtil.millisToZonedDateTime(recurringApplication.getPeriod2Start()),
            TimeUtil.millisToZonedDateTime(recurringApplication.getPeriod2End()));
        queryFactory.insert(recurringPeriod).populate(period2).execute();
      }
    }
  }

  private List<CustomerWithContacts> replaceCustomersWithContacts(Integer applicationId, List<CustomerWithContacts> customerWithContacts) {
    List<Integer> applicationCustomers =
        queryFactory.select(applicationCustomer.id).from(applicationCustomer).where(applicationCustomer.applicationId.eq(applicationId)).fetch();
    queryFactory.delete(applicationCustomerContact).where(applicationCustomerContact.applicationCustomerId.in(applicationCustomers)).execute();
    queryFactory.delete(applicationCustomer).where(applicationCustomer.id.in(applicationCustomers)).execute();
    customerWithContacts.stream().forEach(cwc -> insertCustomerWithContact(applicationId, cwc));
    return customerDao.findByApplicationWithContacts(applicationId);
  }

  private void insertCustomerWithContact(int applicationId, CustomerWithContacts cwc) {
    Integer applicationCustomerId = queryFactory.insert(applicationCustomer)
        .set(applicationCustomer.applicationId, applicationId)
        .set(applicationCustomer.customerId, cwc.getCustomer().getId())
        .set(applicationCustomer.customerRoleType, cwc.getRoleType())
        .executeWithKey(applicationCustomer.id);

    SQLInsertClause inserts = queryFactory.insert(applicationCustomerContact);
    List<Integer> contactIds = new ArrayList<>();
    if (!cwc.getContacts().isEmpty()) {
      cwc.getContacts().forEach(
          c -> inserts.populate(bean(applicationCustomerContact, applicationCustomerContact.all()))
              .set(applicationCustomerContact.contactId, c.getId())
              .set(applicationCustomerContact.applicationCustomerId, applicationCustomerId)
              .addBatch());
      contactIds = inserts.executeWithKeys(contact.id);
    }
  }

  private boolean hasValidRecurringEndTime(Application application) {
    return application.getEndTime() != null &&
        application.getRecurringEndTime() != null &&
        application.getEndTime().isBefore(application.getRecurringEndTime());
  }

  private BooleanExpression andExpression(BooleanExpression existingExpression, BooleanExpression addedExpression) {
    if (existingExpression == null) {
      return addedExpression;
    } else {
      return existingExpression.and(addedExpression);
    }
  }

  /**
   * Creates expression for checking does given period overlap with the recurring period in database.
   */
  private BooleanExpression overlapsPeriod(long periodStart, long periodEnd) {
    return recurringPeriod.periodStartTime.before(TimeUtil.millisToZonedDateTime(periodEnd))
        .and(recurringPeriod.periodEndTime.after(TimeUtil.millisToZonedDateTime(periodStart)));
  }

}
