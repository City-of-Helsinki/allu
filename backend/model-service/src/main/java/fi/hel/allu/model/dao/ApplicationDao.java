package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QGeometry.geometry1;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.domain.LocationSearchCriteria;

@Repository
public class ApplicationDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Application> applicationBean = bean(Application.class, application.all());

  @Transactional(readOnly = true)
  public Optional<Application> findById(int id) {
    Application appl = queryFactory.select(applicationBean).from(application).where(application.id.eq(id)).fetchOne();
    return Optional.ofNullable(appl);
  }

  @Transactional(readOnly = true)
  public List<Application> findByHandler(String handler) {
    return queryFactory.select(applicationBean).from(application).where(application.handler.eq(handler)).fetch();
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
    Event event = appl.getEvent();
    Integer id = queryFactory.insert(application).populate(appl)
        .set(application.startTime, event.getStartTime()).set(application.endTime, event.getEndTime())
        .executeWithKey(application.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Application update(int id, Application appl) {
    appl.setId(id);
    Event event = appl.getEvent();
    long changed = queryFactory.update(application).populate(appl, DefaultMapper.WITH_NULL_BINDINGS)
        .set(application.startTime, event.getStartTime()).set(application.endTime, event.getEndTime())
        .where(application.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }
}
