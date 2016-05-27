package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QApplication.application;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;

public class ApplicationDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Application> applicationBean = bean(Application.class, application.all());

  public Optional<Application> findById(int id) {
    Application appl = queryFactory.select(applicationBean).from(application).where(application.applicationId.eq(id))
        .fetchOne();
    return Optional.ofNullable(appl);
  }

  public List<Application> findByHandler(String handler) {
    return queryFactory.select(applicationBean).from(application).where(application.handler.eq(handler)).fetch();
  }

  public List<Application> findByProject(int projectId) {
    return queryFactory.select(applicationBean).from(application).where(application.projectId.eq(projectId)).fetch();
  }

  public Application insert(Application appl) {
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.applicationId);
    if (id == null) {
      throw new RuntimeException("Failed to insert record");
    }
    return findById(id).get();
  }

  public Application update(int id, Application appl) {
    appl.setApplicationId(id);
    long changed = queryFactory.update(application).populate(appl).where(application.applicationId.eq(id)).execute();
    if (changed != 1) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }

  public void deleteAll() {
    queryFactory.delete(application).execute();
  }
}
