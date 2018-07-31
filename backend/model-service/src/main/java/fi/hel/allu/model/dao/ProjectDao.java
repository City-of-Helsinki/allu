package fi.hel.allu.model.dao;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.QBean;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.sql.SQLExpressions;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Project;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QApplication.application;
import static fi.hel.allu.QProject.project;
import fi.hel.allu.model.querydsl.ExcludingMapper;
import static fi.hel.allu.model.querydsl.ExcludingMapper.NullHandling.WITH_NULL_BINDINGS;

import java.util.Arrays;


@Repository
public class ProjectDao {

  /** Fields that won't be updated in regular updates */
  public static final List<Path<?>> UPDATE_READ_ONLY_FIELDS = Arrays.asList(project.creatorId);

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Project> projectBean = bean(Project.class, project.all());

  private static final BooleanExpression NOT_DELETED = project.deleted.isFalse();

  @Transactional(readOnly = true)
  public Optional<Project> findById(int id) {
    Project proj = queryFactory.select(projectBean).from(project).where(project.id.eq(id), NOT_DELETED).fetchOne();
    return Optional.ofNullable(proj);
  }

  @Transactional(readOnly = true)
  public List<Project> findByIds(List<Integer> ids) {
    return queryFactory.select(projectBean).from(project).where(project.id.in(ids), NOT_DELETED).fetch();
  }

  @Transactional(readOnly = true)
  public List<Project> findProjectChildren(int id) {
    return queryFactory.select(projectBean).from(project).where(project.parentId.eq(id), NOT_DELETED).fetch();
  }

  /**
   * Find all projects, with paging
   *
   * @param pageRequest page request
   * @return a page of projects
   */
  @Transactional(readOnly = true)
  public Page<Project> findAll(Pageable pageRequest) {
    int offset = (pageRequest == null) ? 0 : pageRequest.getOffset();
    int count = (pageRequest == null) ? 100 : pageRequest.getPageSize();
    QueryResults<Project> queryResults = queryFactory.select(projectBean).from(project).where(NOT_DELETED).orderBy(project.id.asc())
        .offset(offset).limit(count).fetchResults();
    return new PageImpl<>(queryResults.getResults(), pageRequest, queryResults.getTotal());
  }

  @Transactional
  public Project insert(Project p) {
    Integer id = queryFactory.insert(project).populate(p).executeWithKey(project.id);
    return findById(id).get();
  }

  @Transactional
  public Project update(int id, Project p) {
    p.setId(id);
    long changed = queryFactory.update(project).populate(p, new ExcludingMapper(WITH_NULL_BINDINGS, UPDATE_READ_ONLY_FIELDS))
        .where(project.id.eq(id)).execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }

  @Transactional
  public int getNextProjectNumber() {
    return queryFactory.select(SQLExpressions.nextval(Integer.class, "allu.project_identifier_prefix_seq")).fetchOne();
  }

  @Transactional
  public void delete(int id) {
    // Delete given project and all it's child projects
    deleteChildProjects(id);
    deleteProjects(Collections.singletonList(id));
  }

  private void deleteProjects(List<Integer> projectIds) {
    queryFactory.update(project).set(project.deleted, true).where(project.id.in(projectIds)).execute();
    // Remove project link from applications
    queryFactory.update(application).setNull(application.projectId).where(application.projectId.in(projectIds)).execute();
  }

  /**
   * Recursively delete all child projects of given project
   */
  private void deleteChildProjects(int parentId) {
    List<Integer> childProjectIds = queryFactory.select(project.id).from(project).where(project.parentId.eq(parentId)).fetch();
    childProjectIds.forEach(c -> deleteChildProjects(c));
    deleteProjects(childProjectIds);
  }
}
