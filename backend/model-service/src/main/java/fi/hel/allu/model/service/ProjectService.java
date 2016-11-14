package fi.hel.allu.model.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Business logic for project (hanke) related functionality.
 */
@Service
public class ProjectService {

  @Autowired
  private ProjectDao projectDao;
  @Autowired
  private ApplicationDao applicationDao;

  @Transactional(readOnly = true)
  public Project find(int id) {
    Optional<Project> project = projectDao.findById(id);
    return project.orElseThrow(() -> new NoSuchEntityException("Project not found", Integer.toString(id)));
  }

  @Transactional(readOnly = true)
  public List<Project> findProjectChildren(int id) {
    return projectDao.findProjectChildren(id);
  }

  /**
   * Insert given project to database.
   *
   * @param   project Project to be inserted.
   * @return  Inserted project.
   */
  @Transactional
  public Project insert(Project project) {
    if (project.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    Project insertedProject = projectDao.insert(project);
    return insertedProject;
  }

  /**
   * Update given project to database.
   *
   * @param   id Id of the project to be updated.
   * @param   project Project to be updated.
   * @return  Updated project.
   */
  @Transactional
  public Project update(int id, Project project) {
    project.setId(id);
    Project currentProject = projectDao.findById(project.getId())
        .orElseThrow(() -> new NoSuchEntityException("Tried to update non-existent project", Integer.toString(project.getId())));
    if (currentProject.getParentId() != project.getParentId()) {
      updateProjectParent(project.getId(), project.getParentId());
      return projectDao.update(id, project);
    } else {
      return projectDao.update(id, project);
    }
  }

  /**
   * Update applications of a given project. This method also updates the projects, which were previously linked to the given applications.
   *
   * @param id              Project whose applications will be updated.
   * @param applicationIds  Applications to be added to the project.
   */
  @Transactional
  public Project updateProjectApplications(int id, List<Integer> applicationIds) {
    List<Application> existingRelatedApplications = applicationDao.findByIds(applicationIds);
    List<Integer> existingRelatedApplicationIds = existingRelatedApplications.stream().map(a -> a.getId()).collect(Collectors.toList());
    if (!existingRelatedApplicationIds.isEmpty()) {
      applicationDao.updateProject(null, existingRelatedApplicationIds);
    }
    applicationDao.updateProject(id, applicationIds);

    // update projects according to the changes
    List<Integer> changedProjects = existingRelatedApplications.stream()
        .filter(a -> a.getProjectId() != null)
        .map(a -> a.getProjectId())
        .distinct()
        .collect(Collectors.toList());
    changedProjects.add(id);
    updateProjectInformation(changedProjects);
    return projectDao.findById(id).get();
  }

  /**
   * Updates project parent to the given parent. Updates existing parent and new parent information.
   *
   * @param id              Project whose parent is updated.
   * @param parentProject   Parent to be set. Use <code>null</code> to clear existing parent.
   */
  @Transactional
  public Project updateProjectParent(int id, Integer parentProject) {
    Project currentProject = projectDao.findById(id)
        .orElseThrow(() -> new NoSuchEntityException("Tried to update parent of non-existent project", Integer.toString(id)));

    ArrayList<Integer> changedProjects = new ArrayList<>();
    if (parentProject != null) {
      // make sure there's no circular references
      List<Integer> newParents = resolveParents(parentProject);
      if (newParents.contains(currentProject.getId())) {
        throw new IllegalArgumentException("Attempted to create a circular reference for project id " + currentProject.getId());
      }
      changedProjects.add(parentProject);
    }

    if (currentProject.getParentId() != null) {
      changedProjects.add(currentProject.getParentId());
    }
    currentProject.setParentId(parentProject);
    projectDao.update(currentProject.getId(), currentProject);
    updateProjectInformation(changedProjects);

    return projectDao.findById(id).get();
  }

  /**
   * Go through all the given projects and their related projects to possibly update at least the following information:
   * - Project duration
   * - Project districts
   *
   * @param projectIds
   */
  private void updateProjectInformation(List<Integer> projectIds) {

    List<Integer> rootParentIds = new ArrayList<>();
    HashSet<Integer> resolvedProjectIds = new HashSet<>();
    for (Integer projectId : projectIds) {
      if (resolvedProjectIds.contains(projectId)) {
        // the root of this project has been discovered previously
        continue;
      }
      List<Integer> parents = resolveParents(projectId);
      rootParentIds.add(parents.get(parents.size() - 1));
      resolvedProjectIds.addAll(parents);
    }

    rootParentIds.forEach(pId -> calculateSummaryAndUpdate(pId));
  }

  /**
   * Resolve parent and grandparents of the given project.
   *
   * @param   projectId   Project id whose parents should be resolved.
   * @return  List of project ids, which has the given project as first item and most grandparents project as last item.
   */
  private List<Integer> resolveParents(Integer projectId) {
    ArrayList<Integer> resolvedProjects = new ArrayList<>();
    Project currentProject = projectDao.findById(projectId)
        .orElseThrow(() -> new NoSuchEntityException("Project not found", projectId.toString())); // should never happen
    resolvedProjects.add(currentProject.getId());
    if (currentProject.getParentId() == null) {
      return resolvedProjects;
    } else {
      resolvedProjects.addAll(resolveParents(currentProject.getParentId()));
      return resolvedProjects;
    }
  }

  /**
   * Calculates and sets summary information of project. The summary consists of information calculated from applications linked directly
   * to the project plus the applications linked to children or grandchildren of the project.
   *
   * @param   projectId   Project whose summary information is calculated.
   * @return  Summary of the data calculated from applications in the project or its children.
   */
  private ProjectSummary calculateSummaryAndUpdate(int projectId) {
    Project project = projectDao.findById(projectId)
        .orElseThrow(() -> new NoSuchEntityException("Project not found", Integer.toString(projectId))); // should never happen
    ProjectSummary summary = new ProjectSummary();
    List<Project> children = projectDao.findProjectChildren(project.getId());

    for (Project child : children) {
      ProjectSummary tmpSummary = calculateSummaryAndUpdate(child.getId());
      summary = mergeSummaries(summary, tmpSummary);
    }

    List<Application> applications = applicationDao.findByProject(project.getId());
    for (Application application : applications) {
      // TODO: do not use Johtoselvitys for calculating summary! Add when johtoselvitys is supported elsewhere in the code
      summary = calculateSummaryFromApplication(application, summary);
    }
    project.setStartTime(summary.minStartTime);
    project.setEndTime(summary.maxEndTime);
    projectDao.update(project.getId(), project);

    return summary;
  }

  private ProjectSummary calculateSummaryFromApplication(Application application, ProjectSummary projectSummary) {
    ProjectSummary ps = new ProjectSummary();
    ps.minStartTime = application.getStartTime();
    ps.maxEndTime = application.getEndTime();
    // TODO: districts
    return mergeSummaries(ps, projectSummary);
  }

  /**
   * Merges given project summaries in the following way:
   * - earliest start time is returned as start time
   * - latest end time is returned as end time
   * - TODO: all districts are combined together
   *
   * @param summary1  Summary to be merged.
   * @param summary2  Summary to be merged.
   * @return Merged summary.
   */
  private ProjectSummary mergeSummaries(ProjectSummary summary1, ProjectSummary summary2) {
    ProjectSummary ps = new ProjectSummary();
    if (summary1.minStartTime != null) {
      if (summary2.minStartTime == null || summary1.minStartTime.isBefore(summary2.minStartTime)) {
        ps.minStartTime = summary1.minStartTime;
      } else {
        ps.minStartTime = summary2.minStartTime;
      }
    } else {
      ps.minStartTime = summary2.minStartTime;
    }
    if (summary1.maxEndTime != null) {
      if (summary2.maxEndTime == null || summary1.maxEndTime.isAfter(summary2.maxEndTime)) {
        ps.maxEndTime = summary1.maxEndTime;
      } else {
        ps.maxEndTime = summary2.maxEndTime;
      }
    } else {
      ps.maxEndTime = summary2.maxEndTime;
    }
    // TODO: districts
    return ps;
  }

  private static class ProjectSummary {
    ZonedDateTime minStartTime;
    ZonedDateTime maxEndTime;
    // TOOD: districts
  }
}
