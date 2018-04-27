package fi.hel.allu.model.dao;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static com.querydsl.sql.SQLExpressions.selectDistinct;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApplicationCustomer.applicationCustomer;
import static fi.hel.allu.QApplicationCustomerContact.applicationCustomerContact;
import static fi.hel.allu.QApplicationKind.applicationKind;
import static fi.hel.allu.QApplicationReminder.applicationReminder;
import static fi.hel.allu.QApplicationTag.applicationTag;
import static fi.hel.allu.QContact.contact;
import static fi.hel.allu.QKindSpecifier.kindSpecifier;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QLocationGeometry.locationGeometry;
import static fi.hel.allu.QPostalAddress.postalAddress;
import static fi.hel.allu.QRecurringPeriod.recurringPeriod;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.SQLInsertClause;

import fi.hel.allu.QApplication;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationSpecifier;
import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationIdentifier;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.ApplicationWithContacts;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.model.domain.PostalAddress;
import fi.hel.allu.model.domain.RecurringPeriod;
import fi.hel.allu.model.domain.util.CustomerAnonymizer;
import fi.hel.allu.model.querydsl.ExcludingMapper;

@Repository
public class ApplicationDao {

  /** Fields that won't be updated in regular updates */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS =
      Arrays.asList(application.status, application.decisionMaker, application.decisionTime, application.creationTime,
          application.metadataVersion, application.owner, application.replacedByApplicationId, application.replacesApplicationId,
          application.invoiced, application.clientApplicationData, application.applicationId);

  private static final BooleanExpression APPLICATION_NOT_REPLACED = application.status.ne(StatusType.REPLACED);

  private final SQLQueryFactory queryFactory;
  private final ApplicationSequenceDao applicationSequenceDao;
  private final StructureMetaDao structureMetaDao;
  private final DistributionEntryDao distributionEntryDao;
  private final CustomerDao customerDao;
  private final AttachmentDao attachmentDao;

  final QBean<Application> applicationBean = bean(Application.class, application.all());
  final QBean<ApplicationTag> applicationTagBean = bean(ApplicationTag.class, applicationTag.all());
  final QBean<ApplicationIdentifier> applicationIdentifierBean = bean(ApplicationIdentifier.class,
      application.id, application.applicationId, application.identificationNumber);

  @Autowired
  public ApplicationDao(
      SQLQueryFactory queryFactory,
      ApplicationSequenceDao applicationSequenceDao,
      DistributionEntryDao distributionEntryDao,
      StructureMetaDao structureMetaDao,
      CustomerDao customerDao,
      AttachmentDao attachmentDao) {
    this.queryFactory = queryFactory;
    this.applicationSequenceDao = applicationSequenceDao;
    this.distributionEntryDao = distributionEntryDao;
    this.structureMetaDao = structureMetaDao;
    this.customerDao = customerDao;
    this.attachmentDao = attachmentDao;
  }

  /**
   * Returns also replaced application.
   */
  @Transactional
  public Application findById(int id) {
    Application app = queryFactory.select(applicationBean).from(application).where(application.id.eq(id)).fetchOne();
    Optional.ofNullable(app).ifPresent(a -> populateDependencies(Collections.singletonList(a), false));
    return app;
  }

  @Transactional(readOnly = true)
  public List<Application> findByIds(List<Integer> ids, boolean anonymizePersons) {
    List<Application> appl = queryFactory.select(applicationBean).from(application)
        .where(application.id.in(ids).and(APPLICATION_NOT_REPLACED)).fetch();
    return populateDependencies(appl, anonymizePersons);
  }

  @Transactional(readOnly = true)
  public List<Application> findByProject(int projectId) {
    List<Application> applications =
        queryFactory.select(applicationBean).from(application).where(application.projectId.eq(projectId)).fetch();
    return populateDependencies(applications, false);
  }

  @Transactional(readOnly = true)
  public List<Application> findActiveByLocation(LocationSearchCriteria lsc) {
    BooleanExpression strictStartEndTimeCondition = strictStartEndTimeCondition(lsc);
    BooleanExpression recurringCondition = recurringCondition(lsc);
    BooleanExpression statusExpression = statusCondition(lsc);
    BooleanExpression geometryExpression = statusExpression.and(locationGeometry.geometry.intersects(lsc.getIntersects()));
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
            .where(geometryExpression.and(APPLICATION_NOT_REPLACED)));

    List<Application> applications =
        queryFactory.select(applicationBean).from(application).where(condition).fetch();

    return populateDependencies(applications, false);
  }

  private BooleanExpression strictStartEndTimeCondition(LocationSearchCriteria lsc) {
    BooleanExpression strictStartEndTimeCondition = null;
    if (lsc.getAfter() != null) {
      BooleanExpression beforeOrUnfinished = application.endTime.after(lsc.getAfter())
          .or(application.endTime.before(ZonedDateTime.now())
              .and(application.status.notIn(StatusType.FINISHED, StatusType.CANCELLED)));
      strictStartEndTimeCondition = andExpression(strictStartEndTimeCondition, beforeOrUnfinished);
    }
    if (lsc.getBefore() != null) {
      strictStartEndTimeCondition = andExpression(strictStartEndTimeCondition, application.startTime.before(lsc.getBefore()));
    }
    return strictStartEndTimeCondition;
  }

  private BooleanExpression recurringCondition(LocationSearchCriteria lsc) {
    ZonedDateTime recurringStartTime = Optional.ofNullable(lsc.getAfter()).orElse(RecurringApplication.BEGINNING_1972_DATE);
    ZonedDateTime recurringEndTime = Optional.ofNullable(lsc.getBefore()).orElse(RecurringApplication.MAX_END_TIME);
    RecurringApplication recurringApplication = new RecurringApplication(recurringStartTime, recurringEndTime, recurringEndTime);
        // application start and recurring end time has to intersect search period
    return application.startTime.before(TimeUtil.millisToZonedDateTime(recurringApplication.getEndTime()))
            .and(application.recurringEndTime.after(TimeUtil.millisToZonedDateTime(recurringApplication.getStartTime())))
            // recurring period checks
            .and(overlapsPeriod(recurringApplication.getPeriod1Start(), recurringApplication.getPeriod1End())
                .or(overlapsPeriod(recurringApplication.getPeriod2Start(), recurringApplication.getPeriod2End())));
  }

  private BooleanExpression statusCondition(LocationSearchCriteria lsc) {
    return Optional.ofNullable(lsc.getStatusTypes())
        .filter(types -> !types.isEmpty())
        .map(statusTypes -> application.status.in(statusTypes))
        .orElse(Expressions.TRUE);
  }

  /**
   * Find all applications, with paging
   *
   * @param pageRequest page request
   * @return a page of applications
   */
  @Transactional(readOnly = true)
  public Page<Application> findAll(Pageable pageRequest) {
    int offset = (pageRequest == null) ? 0 : pageRequest.getOffset();
    int count = (pageRequest == null) ? 100 : pageRequest.getPageSize();
    QueryResults<Application> queryResults = queryFactory.select(applicationBean).from(application)
        .orderBy(application.id.asc()).offset(offset).limit(count).fetchResults();
    return new PageImpl<>(populateDependencies(queryResults.getResults(), false), pageRequest, queryResults.getTotal());
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
    List<Integer> applications = queryFactory.select(application.id).from(application).where(whereCondition.and(APPLICATION_NOT_REPLACED)).fetch();
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
    // Use application id from application if present, otherwise generate id.
    appl.setApplicationId(Optional.ofNullable(appl.getApplicationId()).orElse(createApplicationId(appl.getType())));
    appl.setCreationTime(ZonedDateTime.now());
    // Do not overwrite given status
    if (appl.getStatus() == null) {
      appl.setStatus(StatusType.PENDING);
    }
    appl.setMetadataVersion(structureMetaDao.getLatestMetadataVersion());
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    insertDistributionEntries(id, appl.getDecisionDistributionList());
    replaceCustomersWithContacts(id, appl.getCustomersWithContacts());
    replaceKindsWithSpecifiers(id, appl.getKindsWithSpecifiers());
    Application application = findByIds(Collections.singletonList(id), false).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    replaceRecurringPeriods(application);
    return populateTags(application);
  }

  /**
   * Delete note-type application and its related data
   *
   * @param id application's database ID.
   */
  @Transactional
  public void deleteNote(int id) {
    ApplicationType applicationType = queryFactory.select(application.type).from(application)
        .where(application.id.eq(id)).fetchOne();
    if (ApplicationType.NOTE != applicationType) {
      throw new IllegalArgumentException("Trying to delete non-note application");
    }
    deleteApplication(id);
  }

  /**
   * Delete draft application and its related data
   *
   * @param id application's database ID.
   */
  @Transactional
  public void deleteDraft(int id) {
    StatusType status = queryFactory.select(application.status).from(application)
        .where(application.id.eq(id)).fetchOne();
    if (StatusType.PRE_RESERVED != status) {
      throw new IllegalArgumentException("Trying to delete application which is not a draft");
    }
    deleteApplication(id);
  }

  private void deleteApplication(int id) {
    queryFactory.delete(application).where(application.id.eq(id)).execute();
    /*
     * After removing the application, some attachments might be unreferenced,
     * so we need a cleanup action for them (attachment doesn't reference
     * application directly, so ON DELETE CASCADE can't be used to automatically
     * clean it up):
     */
    attachmentDao.deleteUnreferencedAttachments();
  }


  /**
   * Updates owner of given applications.
   *
   * @param owner New owner set to the applications.
   * @param applications Applications whose owner is updated.
   * @return Number of updated applications.
   */
  @Transactional
  public int updateOwner(int owner, List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.owner, owner)
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  @Transactional
  public int removeOwner(List<Integer> applications) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.owner, Expressions.nullExpression())
        .where(application.id.in(applications))
        .execute();
    return updated;
  }

  @Transactional
  public Application startDecisionMaking(int applicationId, StatusType status, int userId) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.handler, userId)
        .set(application.status, status)
        .where(application.id.eq(applicationId))
        .execute();
    if (updated != 1) {
      throw new NoSuchEntityException("Attempted to update decision status of non-existent application", Integer.toString(applicationId));
    }
    return findByIds(Collections.singletonList(applicationId), false).get(0);
  }

  /**
   * Update application's decision.
   *
   * @param applicationId   Id of the application to be updated.
   * @param status          New status.
   * @return  Updated application.
   */
  @Transactional
  public Application updateDecision(int applicationId, StatusType status, int decisionMakerId, Integer ownerId) {
    int updated = (int) queryFactory
        .update(application)
        .set(application.owner, ownerId)
        .set(application.decisionMaker, decisionMakerId)
        .set(application.status, status)
        .set(application.decisionTime, ZonedDateTime.now())
        .where(application.id.eq(applicationId))
        .execute();
    if (updated != 1) {
      throw new NoSuchEntityException("Attempted to update decision status of non-existent application", Integer.toString(applicationId));
    }
    return findByIds(Collections.singletonList(applicationId), false).get(0);
  }

  /**
   * Update application status.
   *
   * @param applicationId   Application to be updated
   * @param status          New status (cannot change status to either decision state).
   * @return  Updated application.
   */
  @Transactional
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
    return findById(applicationId);
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
    replaceKindsWithSpecifiers(id, appl.getKindsWithSpecifiers());
    Application application = findByIds(Collections.singletonList(id), false).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    replaceRecurringPeriods(application);
    return populateTags(application);
  }

  /**
   * Replaces applications tags with given new tags.
   * Returns updated tags.
   */
  @Transactional
  public List<ApplicationTag> updateTags(int id, List<ApplicationTag> tags) {
    replaceApplicationTags(id, tags);
    return findTagsByApplicationId(id);
  }

  /**
   * Add single tag to application
   *
   * @param applicationId Application's database ID
   * @param tag Tag to add
   * @return existing tag if there is one, otherwise added tag
   */
  @Transactional
  public ApplicationTag addTag(int applicationId, ApplicationTag tag) {
    ApplicationTag existing = queryFactory.select(applicationTagBean)
        .from(applicationTag)
        .where(applicationTag.applicationId.eq(applicationId).and(applicationTag.type.eq(tag.getType())))
        .fetchFirst();

    return Optional.ofNullable(existing)
        .orElseGet(() -> insertSingleTag(applicationId, tag));
  }

  /**
   * Fetches tags for specified application
   */
  @Transactional(readOnly = true)
  public List<ApplicationTag> findTagsByApplicationId(Integer applicationId) {
    return queryFactory.select(applicationTagBean)
        .from(applicationTag)
        .where(applicationTag.applicationId.eq(applicationId))
        .fetch();
  }

  /**
   * Removes all tags of given type from application
   * @param applicationId Application's database ID
   * @param tagType type of tag(s) to remove
   */
  @Transactional
  public void removeTagByType(int applicationId, ApplicationTagType tagType) {
    queryFactory.delete(applicationTag)
        .where(applicationTag.applicationId.eq(applicationId).and(applicationTag.type.eq(tagType)))
        .execute();
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

  private List<Application> populateDependencies(List<Application> applications, boolean anonymizePersons) {
    applications.forEach(a -> a.setDecisionDistributionList(distributionEntryDao.findByApplicationId(a.getId())));
    applications.forEach(a -> populateTags(a));
    if (anonymizePersons) {
      applications.forEach(a -> a.setCustomersWithContacts(
          CustomerAnonymizer.anonymize(customerDao.findByApplicationWithContacts(a.getId()))));
    } else {
      applications.forEach(a -> a.setCustomersWithContacts(customerDao.findByApplicationWithContacts(a.getId())));
    }
    applications.forEach(a -> a.setKindsWithSpecifiers(findKindsAndSpecifiers(a.getId())));
    return applications;
  }

  private Application populateTags(Application application) {
    application.setApplicationTags(findTagsByApplicationId(application.getId()));
    return application;
  }

  private Map<ApplicationKind, List<ApplicationSpecifier>> findKindsAndSpecifiers(Integer applicationId) {
    Map<ApplicationKind, List<ApplicationSpecifier>> m = queryFactory
        .select(applicationKind.kind, kindSpecifier.specifier).from(applicationKind)
        .leftJoin(kindSpecifier)
        .on(applicationKind.id.eq(kindSpecifier.kindId)).where(applicationKind.applicationId.eq(applicationId))
        .transform(groupBy(applicationKind.kind).as(list(kindSpecifier.specifier)));
    return m;
  }

  private void replaceApplicationTags(Integer applicationId, List<ApplicationTag> tags) {
    queryFactory.delete(applicationTag).where(applicationTag.applicationId.eq(applicationId)).execute();
    if (tags != null && !tags.isEmpty()) {
      tags.forEach(tag -> insertSingleTag(applicationId, tag));
    }
  }

  private ApplicationTag insertSingleTag(int applicationId, ApplicationTag tag) {
    // Let the database assign ID
    tag.setId(null);
    tag.setApplicationId(applicationId);
    Integer id = queryFactory.insert(applicationTag).populate(tag).executeWithKey(applicationTag.id);
    return queryFactory.select(applicationTagBean).from(applicationTag).where(applicationTag.id.eq(id)).fetchOne();
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

  /* Set new kinds and specifiers for the given application id */
  private void replaceKindsWithSpecifiers(Integer applicationId,
      Map<ApplicationKind, List<ApplicationSpecifier>> kindsWithSpecifiers) {
    queryFactory.delete(applicationKind).where(applicationKind.applicationId.eq(applicationId)).execute();
    kindsWithSpecifiers.forEach((kind, specifiers) -> {
      final int kindId = queryFactory.insert(applicationKind).set(applicationKind.applicationId, applicationId)
          .set(applicationKind.kind, kind).executeWithKey(applicationKind.id);
      specifiers.forEach(specifier -> queryFactory.insert(kindSpecifier).set(kindSpecifier.kindId, kindId)
          .set(kindSpecifier.specifier, specifier).execute());
    });
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

  public List<Integer> findByInvoiceRecipient(int invoiceRecipientId) {
    QApplication application = QApplication.application;
    List<Integer> applicationIds = queryFactory
        .select(application.id)
        .from(application)
        .where(application.invoiceRecipientId.eq(invoiceRecipientId)).
        fetch();
    return applicationIds;
  }

  public List<ApplicationIdentifier> findByApplicationIdStartingWith(String idStart) {
    return queryFactory
        .select(applicationIdentifierBean)
        .from(application)
        .where(application.applicationId.startsWith(idStart))
        .fetch();
  }

  public void copyApplicationAttachments(Integer copyFromApplicationId, Integer copyToApplicationId) {
    attachmentDao.copyApplicationAttachments(copyFromApplicationId, copyToApplicationId);
  }

  public void setApplicationReplaced(int replacedApplicationId, int replacingApplicationId) {
    queryFactory
        .update(application)
        .set(application.replacedByApplicationId, replacingApplicationId)
        .where(application.id.eq(replacedApplicationId))
        .execute();
  }

  /**
   * Get invoicing date for application with given application ID
   * @param applicationId
   * @return
   */
  public ZonedDateTime getInvoicingDate(int applicationId) {
    return queryFactory
      .select(application.invoicingDate)
      .from(application)
      .where(application.id.eq(applicationId))
      .fetchOne();
  }

  /**
   * Mark applications (completely) invoiced
   */
  @Transactional
  public void markInvoiced(List<Integer> applicationIds) {
    queryFactory.update(application).set(application.invoiced, true).where(application.id.in(applicationIds)).execute();
  }

  /**
   * Fetches status of specified application
   */
  @Transactional(readOnly = true)
  public StatusType getStatus(int applicationId) {
    return queryFactory
        .select(application.status)
        .from(application)
        .where(application.id.eq(applicationId))
        .fetchOne();
  }

}
