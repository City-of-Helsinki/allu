package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QGeometry.geometry1;

import java.util.List;
import java.util.Optional;

import org.geolatte.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryException;
import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;

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
  public List<Application> findIntersecting(Geometry geometry) {
    return queryFactory.select(applicationBean).from(application)
        .where(application.locationId.in(
            SQLExpressions.select(geometry1.locationId).from(geometry1).where(geometry1.geometry.intersects(geometry))))
        .fetch();
  }

  @Transactional
  public Application insert(Application appl) {
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.id);
    if (id == null) {
      throw new QueryException("Failed to insert record");
    }
    return findById(id).get();
  }

  @Transactional
  public Application update(int id, Application appl) {
    appl.setId(id);
    long changed = queryFactory.update(application).populate(appl, DefaultMapper.WITH_NULL_BINDINGS)
        .where(application.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }
}
