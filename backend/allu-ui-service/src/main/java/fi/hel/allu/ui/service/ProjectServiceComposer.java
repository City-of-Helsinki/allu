package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.domain.QueryParametersJson;
import fi.hel.allu.ui.mapper.QueryParameterMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for composing different project related services together. The main purpose of this class is to avoid circular references
 * between different services.
 */
@Service
public class ProjectServiceComposer {

  private ApplicationService applicationService;
  private ProjectService projectService;
  private SearchService searchService;
  private ApplicationJsonService applicationJsonService;

  @Autowired
  public ProjectServiceComposer(
      ApplicationService applicationService,
      ProjectService projectService,
      ApplicationJsonService applicationJsonService,
      SearchService searchService) {
    this.applicationService = applicationService;
    this.projectService = projectService;
    this.applicationJsonService = applicationJsonService;
    this.searchService = searchService;
  }

  /**
   * Search projects with given query parameters. Returns projects in the order defined by query.
   *
   * @param   queryParameters   Parameters for search query.
   * @return  found projects in the order defined by query.
   */
  public List<ProjectJson> search(QueryParametersJson queryParameters) {
    List<ProjectJson> resultList = Collections.emptyList();
    List<Integer> ids = searchService.searchProject(QueryParameterMapper.mapToQueryParameters(queryParameters));
    resultList = projectService.findByIds(ids);
    SearchService.orderByIdList(ids, resultList, (projectJson) -> projectJson.getId());
    return resultList;
  }

  /**
   * Create a new project.
   *
   * @param projectJson   Project that is going to be created
   * @return Created project
   */
  public ProjectJson insert(ProjectJson projectJson) {
    ProjectJson insertedProject = projectService.insert(projectJson);
    searchService.insertProject(insertedProject);
    return insertedProject;
  }

  /**
   * Update given project.
   *
   * @param projectJson Project that is going to be updated
   */
  public ProjectJson update(int projectId, ProjectJson projectJson) {
    ProjectJson updatedProject = projectService.update(projectId, projectJson);
    searchService.updateProject(updatedProject);
    return updatedProject;
  }

  /**
   * Updates applications of given project to the given set of applications.
   *
   * @param id              Id of the project.
   * @param applicationIds  Application ids to be attached to the given project. Use empty list to clear all references to the given project.
   */
  public ProjectJson updateProjectApplications(int id, List<Integer> applicationIds) {
    // find applications with (possibly) existing linked projects
    List<Application> updatedApplications = applicationService.findApplicationsById(applicationIds);
    // project ids whose applications are changed
    List<Integer> updatedApplicationProjectIds = updatedApplications.stream()
        .filter(a -> a.getProjectId() != null && a.getProjectId() != id)
        .map(Application::getProjectId)
        .distinct()
        .collect(Collectors.toList());
    // find the applications previously linked to the given project (in case applications are removed from the project)
    Set<Integer> updatedApplicationIds =
        projectService.findApplicationsByProject(id).stream().map(a -> a.getId()).collect(Collectors.toSet());
    // link applications to the given project
    ProjectJson updatedProject = projectService.updateProjectApplications(id, applicationIds);
    updatedApplicationIds.addAll(applicationIds);
    List<Application> applicationsWithUpdatedProjectId = applicationService.findApplicationsById(new ArrayList<>(updatedApplicationIds));
    // find which projects are affected by the project change of the given applications
    List<ProjectJson> changedProjects = new ArrayList<>();
    changedProjects.add(updatedProject);
    if (!updatedApplicationProjectIds.isEmpty()) {
      // find projects that were previously linked to the updated applications
      List<ProjectJson> updatedApplicationProjectParents = updatedApplicationProjectIds.stream()
          .map(uId -> projectService.findProjectParents(uId).stream()).flatMap(p -> p).distinct().collect(Collectors.toList());
      changedProjects.addAll(updatedApplicationProjectParents);
    }
    // update search index with the changed projects
    searchService.updateProjects(changedProjects);
    // update search index with the changed applications
    List<ApplicationJson> applicationJsons = applicationsWithUpdatedProjectId.stream()
        .map(a -> applicationJsonService.getFullyPopulatedApplication(a))
        .collect(Collectors.toList());
    searchService.updateApplications(applicationJsons);

    return updatedProject;
  }

  /**
   * Update parent of the given project.
   *
   * @param id              Project whose parent should be updated.
   * @param parentProject   New parent project.
   * @return  Updated project.
   */
  public ProjectJson updateProjectParent(int id, Integer parentProject) {
    List<ProjectJson> existingParents = projectService.findProjectParents(id);
    ProjectJson updatedProject = projectService.updateProjectParent(id, parentProject);
    List<ProjectJson> updatedParents = projectService.findProjectParents(id);
    HashSet<ProjectJson> searchUpdate = new HashSet<>(existingParents);
    searchUpdate.addAll(updatedParents);
    searchService.updateProjects(new ArrayList(searchUpdate));
    return updatedProject;
  }

  public void updateParentForProjects(Integer parentProject, List<Integer> ids) {
    ids.forEach(id -> updateProjectParent(id, parentProject));
  }
}
