package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QProject.project;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.Project;

public class ProjectDaoImpl implements ProjectDao {

    @Inject
    private SQLQueryFactory queryFactory;

    final QBean<Project> projectBean = bean(Project.class, project.all());

    @Override
    @Transactional(readOnly = true)
    public Optional<Project> findById(int id) {
        Project proj = queryFactory.select(projectBean).from(project).where(project.projectId.eq(id)).fetchOne();
        return Optional.ofNullable(proj);
    }

    @Override
    @Transactional
    public Project insert(Project p) {
        Integer id = queryFactory.insert(project).populate(p).executeWithKey(project.projectId);
        if (id == null) {
            throw new RuntimeException("Failed to insert record");
        }
        return findById(id).get();
    }

    @Override
    @Transactional
    public Project update(int id, Project p) {
        p.setProjectId(id);
        long changed = queryFactory.update(project).populate(p).where(project.projectId.eq(id)).execute();
        if (changed != 1) {
            throw new RuntimeException("Failed to update the record");
        }
        return findById(id).get();
    }

}
