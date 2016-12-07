package fi.hel.allu.model.dao;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.dml.DefaultMapper;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.querydsl.core.types.Projections.bean;
import static fi.hel.allu.QProject.project;

@Repository
public class ProjectDao {

  @Autowired
  private SQLQueryFactory queryFactory;

  final QBean<Project> projectBean = bean(Project.class, project.all());

  @Transactional(readOnly = true)
  public Optional<Project> findById(int id) {
    Project proj = queryFactory.select(projectBean).from(project).where(project.id.eq(id)).fetchOne();
    return Optional.ofNullable(proj);
  }

  @Transactional(readOnly = true)
  public List<Project> findByIds(List<Integer> ids) {
    return queryFactory.select(projectBean).from(project).where(project.id.in(ids)).fetch();
  }

  @Transactional(readOnly = true)
  public List<Project> findProjectChildren(int id) {
    return queryFactory.select(projectBean).from(project).where(project.parentId.eq(id)).fetch();
  }

  @Transactional
  public Project insert(Project p) {
    Integer id = queryFactory.insert(project).populate(p).executeWithKey(project.id);
    return findById(id).get();
  }

  @Transactional
  public Project update(int id, Project p) {
    p.setId(id);
    long changed = queryFactory.update(project).populate(p, DefaultMapper.WITH_NULL_BINDINGS).where(project.id.eq(id))
        .execute();
    if (changed == 0) {
      throw new NoSuchEntityException("Failed to update the record", Integer.toString(id));
    }
    return findById(id).get();
  }
}
