package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.querydsl.ExcludingMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApplicationTag.applicationTag;
import static fi.hel.allu.QLocation.location;
import static fi.hel.allu.QLocationGeometry.locationGeometry;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

@Repository
public class ApplicationDao {

  /** Fields that won't be updated in regular updates */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS =
      Arrays.asList(application.status, application.decisionMaker, application.decisionTime, application.creationTime, application.metadataVersion);

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
    BooleanExpression condition =
        application.id.in(SQLExpressions.select(location.applicationId)
        .from(locationGeometry).join(location).on(locationGeometry.locationId.eq(location.id))
            .where(locationGeometry.geometry.intersects(lsc.getIntersects())));
    if (lsc.getAfter() != null) {
      condition = condition.and(application.endTime.after(lsc.getAfter()));
    }
    if (lsc.getBefore() != null) {
      condition = condition.and(application.startTime.before(lsc.getBefore()));
    }
    List<Application> applications = queryFactory.select(applicationBean).from(application).where(condition).fetch();
    return populateDependencies(applications);
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

}
