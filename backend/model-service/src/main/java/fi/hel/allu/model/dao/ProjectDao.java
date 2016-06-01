package fi.hel.allu.model.dao;

import static com.querydsl.core.types.Projections.bean;
import static fi.vincit.allu.QProject.project;

import java.util.Optional;

import javax.inject.Inject;

import com.querydsl.core.types.QBean;
import com.querydsl.sql.SQLQueryFactory;

import fi.hel.allu.model.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class ProjectDao {

    @Autowired
    private SQLQueryFactory queryFactory;

    final QBean<Project> projectBean = bean(Project.class, project.all());

    @Transactional(readOnly = true)
    public Optional<Project> findById(int id) {
        Project proj = queryFactory.select(projectBean).from(project).where(project.id.eq(id)).fetchOne();
        return Optional.ofNullable(proj);
    }

    @Transactional
    public Project insert(Project p) {
        Integer id = queryFactory.insert(project).populate(p).executeWithKey(project.id);
        if (id == null) {
            throw new RuntimeException("Failed to insert record");
        }
        return findById(id).get();
    }

    @Transactional
    public Project update(int id, Project p) {
        p.setId(id);
        long changed = queryFactory.update(project).populate(p).where(project.id.eq(id)).execute();
        if (changed != 1) {
            throw new RuntimeException("Failed to update the record");
        }
        return findById(id).get();
    }

}
