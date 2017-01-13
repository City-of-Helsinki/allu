package fi.hel.allu.model.dao;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.LocationSearchCriteria;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QApplicationTag.applicationTag;
import static fi.hel.allu.QLocationGeometry.locationGeometry;

@Repository
public class ApplicationDao {

  private SQLQueryFactory queryFactory;
  private ApplicationSequenceDao applicationSequenceDao;

  final QBean<Application> applicationBean = bean(Application.class, application.all());
  final QBean<ApplicationTag> applicationTagBean = bean(ApplicationTag.class, applicationTag.all());

  @Autowired
  public ApplicationDao(SQLQueryFactory queryFactory, ApplicationSequenceDao applicationSequenceDao) {
    this.queryFactory = queryFactory;
    this.applicationSequenceDao = applicationSequenceDao;
  }

  @Transactional(readOnly = true)
  public List<Application> findByIds(List<Integer> ids) {
    List<Application> appl = queryFactory.select(applicationBean).from(application).where(application.id.in(ids)).fetch();
    return populateTags(appl);
  }

  @Transactional(readOnly = true)
  public List<Application> findByProject(int projectId) {
    List<Application> applications =
        queryFactory.select(applicationBean).from(application).where(application.projectId.eq(projectId)).fetch();
    return populateTags(applications);
  }

  @Transactional(readOnly = true)
  public List<Application> findByLocation(LocationSearchCriteria lsc) {
    BooleanExpression condition = application.locationId.in(SQLExpressions.select(locationGeometry.locationId)
        .from(locationGeometry).where(locationGeometry.geometry.intersects(lsc.getIntersects())));
    if (lsc.getAfter() != null) {
      condition = condition.and(application.endTime.after(lsc.getAfter()));
    }
    if (lsc.getBefore() != null) {
      condition = condition.and(application.startTime.before(lsc.getBefore()));
    }
    List<Application> applications = queryFactory.select(applicationBean).from(application).where(condition).fetch();
    return populateTags(applications);
  }

  @Transactional
  public Application insert(Application appl) {
    appl.setApplicationId(createApplicationId(appl.getType()));
    appl.setStatus(StatusType.PENDING);
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
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

  @Transactional
  public Application update(int id, Application appl) {
    appl.setId(id);
    long changed = queryFactory.update(application).populate(appl, DefaultMapper.WITH_NULL_BINDINGS).where(application.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    Application application = findByIds(Collections.singletonList(id)).get(0);
    replaceApplicationTags(application.getId(), appl.getApplicationTags());
    return populateTags(application);
  }

  String createApplicationId(ApplicationType applicationType) {
    long seqValue = applicationSequenceDao
        .getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType));
    return ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType).name() + seqValue;
  }

  private List<Application> populateTags(List<Application> applications) {
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
