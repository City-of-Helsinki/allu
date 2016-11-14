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
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QGeometry.geometry1;

@Repository
public class ApplicationDao {

  private SQLQueryFactory queryFactory;
  private ApplicationSequenceDao applicationSequenceDao;

  @Autowired
  public ApplicationDao(SQLQueryFactory queryFactory, ApplicationSequenceDao applicationSequenceDao) {
    this.queryFactory = queryFactory;
    this.applicationSequenceDao = applicationSequenceDao;
  }

  final QBean<Application> applicationBean = bean(Application.class, application.all());

  @Transactional(readOnly = true)
  public List<Application> findByIds(List<Integer> ids) {
    List<Application> appl = queryFactory.select(applicationBean).from(application).where(application.id.in(ids)).fetch();
    return appl;
  }

  @Transactional(readOnly = true)
  public List<Application> findByProject(int projectId) {
    return queryFactory.select(applicationBean).from(application).where(application.projectId.eq(projectId)).fetch();
  }

  @Transactional(readOnly = true)
  public List<Application> findByLocation(LocationSearchCriteria lsc) {
    BooleanExpression condition = application.locationId.in(SQLExpressions.select(geometry1.locationId).from(geometry1)
        .where(geometry1.geometry.intersects(lsc.getIntersects())));
    if (lsc.getAfter() != null) {
      condition = condition.and(application.endTime.after(lsc.getAfter()));
    }
    if (lsc.getBefore() != null) {
      condition = condition.and(application.startTime.before(lsc.getBefore()));
    }
    return queryFactory.select(applicationBean).from(application)
        .where(condition).fetch();
  }

  @Transactional
  public Application insert(Application appl) {
    appl.setApplicationId(createApplicationId(appl.getType()));
    appl.setStatus(StatusType.PENDING);
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findByIds(Collections.singletonList(id)).get(0);
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
    Event event = appl.getEvent();
    long changed = queryFactory.update(application).populate(appl, DefaultMapper.WITH_NULL_BINDINGS).where(application.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findByIds(Collections.singletonList(id)).get(0);
  }

  String createApplicationId(ApplicationType applicationType) {
    long seqValue = applicationSequenceDao.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType));
    return ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.of(applicationType).name() + seqValue;
  }
}
