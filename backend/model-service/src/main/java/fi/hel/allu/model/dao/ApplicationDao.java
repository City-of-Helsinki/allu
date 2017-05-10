package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLQueryFactory;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.Projections.bean;
import static com.querydsl.sql.SQLExpressions.select;
import static com.querydsl.sql.SQLExpressions.selectDistinct;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApplicationContact.applicationContact;
import static fi.hel.allu.QApplicationTag.applicationTag;
import static fi.hel.allu.QContact.contact;
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
  DistributionEntryDao distributionEntryDao;

  final QBean<Application> applicationBean = bean(Application.class, application.all());
  final QBean<ApplicationTag> applicationTagBean = bean(ApplicationTag.class, applicationTag.all());

  @Autowired
  public ApplicationDao(
      SQLQueryFactory queryFactory,
      ApplicationSequenceDao applicationSequenceDao,
      DistributionEntryDao distributionEntryDao,
      StructureMetaDao structureMetaDao) {
    this.queryFactory = queryFactory;
    this.applicationSequenceDao = applicationSequenceDao;
    this.distributionEntryDao = distributionEntryDao;
    this.structureMetaDao = structureMetaDao;
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
   * Finds applications related to the given applicant.
   *
   * @param id    Id of the applicant whose applications are fetched.
   * @return List of related application ids. Never <code>null</code>.
   */
  @Transactional(readOnly = true)
  public List<Integer> findByApplicant(int id) {
    return queryFactory
        .select(application.id)
        .from(application)
        .where(application.applicantId.eq(id)).fetch();
  }

  /**
   * Find all contacts of applications having given contact.
   *
   * @param ids Contact ids to be searched.
   * @return  all contacts of applications having given contact. The id of application is the map key and value contains all contacts
   *          of the application.
   */
  @Transactional(readOnly = true)
  public Map<Integer, List<Contact>> findRelatedApplicationsWithContacts(List<Integer> ids) {
    SubQueryExpression<Integer> sq = select(applicationContact.applicationId).from(applicationContact).where(applicationContact.contactId.in(ids));
    // create expression list to allow mapping of all contact fields and postal address joined from another table
    List<Expression> mappedExpressions = new ArrayList<>(Arrays.asList(contact.all()));
    mappedExpressions.add(bean(PostalAddress.class, postalAddress.all()).as("postalAddress"));
    Map<Integer, List<Contact>> applicationIdToContacts = queryFactory
        .select(bean(applicationContact.applicationId), bean(Contact.class, contact.all()))
        .from(applicationContact)
        .join(contact).on(applicationContact.contactId.eq(contact.id))
        .leftJoin(postalAddress).on(contact.postalAddressId.eq(postalAddress.id))
        .where(applicationContact.applicationId.in(sq))
        .transform(groupBy(applicationContact.applicationId).as(list(bean(Contact.class, mappedExpressions.toArray(new Expression[0])))));

    return applicationIdToContacts;
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
    Application application = findByIds(Collections.singletonList(id)).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    insertOrUpdateRecurringPeriods(application);
    return populateTags(application);
  }

  /**
   * Updates handler of given applications.
   *
   * @param   handler       New handler set to the applications.
   * @param   applications  Applications whose handler is updated.
   *
   * @return  Number of updated applications.
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

  /*
   * Update application. All other fields are taken from appl, but creationTime is not updated.
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
    Application application = findByIds(Collections.singletonList(id)).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    insertOrUpdateRecurringPeriods(application);
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

  private void insertOrUpdateRecurringPeriods(Application application) {
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

  private boolean hasValidRecurringEndTime(Application application) {
    return application.getEndTime() != null &&
        application.getRecurringEndTime() != null &&
        application.getEndTime().isBefore(application.getRecurringEndTime());
  }

  public static void main(String ...argv) {
    System.out.println(ZoneId.systemDefault());
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
