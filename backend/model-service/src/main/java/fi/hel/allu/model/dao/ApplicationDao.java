package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QApplication.application;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;

public class ApplicationDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Application> applicationBean = bean(Application.class, application.all());

  @Transactional(readOnly = true)
  public Optional<Application> findById(int id) {
    Application appl = queryFactory.select(applicationBean).from(application).where(application.id.eq(id))
        .fetchOne();
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

  @Transactional
  public Application insert(Application appl) {
    Integer id = queryFactory.insert(application).populate(appl).executeWithKey(application.id);
    return findById(id).get();
  }

  @Transactional
  public Application update(int id, Application appl) {
    appl.setId(id);
    long changed = queryFactory.update(application).populate(appl).where(application.id.eq(id)).execute();
    if (changed != 1) {
      throw new QueryException("Failed to update the record");
    }
    return findById(id).get();
  }
}
