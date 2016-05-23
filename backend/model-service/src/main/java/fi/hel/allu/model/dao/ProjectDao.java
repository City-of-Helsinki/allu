package fi.hel.allu.model.dao;

import java.util.Optional;

import fi.hel.allu.model.domain.Project;

public interface ProjectDao {

    public Optional<Project> findById(int id);

    public Project insert(Project project);

    public Project update(int id, Project project);
}
