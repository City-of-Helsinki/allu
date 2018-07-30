package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.domain.QueryParametersJson;
import fi.hel.allu.servicecore.mapper.QueryParameterMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service for composing different project related services together. The main purpose of this class is to avoid circular references
 * between different services.
 */
@Service
public class ProjectServiceComposer {

  private final ApplicationService applicationService;
  private final ProjectService projectService;
  private final SearchService searchService;
  private final ApplicationJsonService applicationJsonService;

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
  public Page<ProjectJson> search(QueryParametersJson queryParameters, Pageable pageRequest) {
    return searchService.searchProject(QueryParameterMapper.mapToQueryParameters(queryParameters), pageRequest,
        idlist -> {
          List<ProjectJson> resultList = projectService.findByIds(idlist);
          SearchService.orderByIdList(idlist, resultList, (projectJson) -> projectJson.getId());
          return resultList;
        });
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

  public void delete(int id, List<Integer> applicationIds) {
    projectService.delete(id);
    searchService.deleteProject(id);

    final List<ApplicationJson> applications = getFullyPopulatedApplications(applicationIds);
    searchService.updateApplications(applications);
  }


  public List<ApplicationJson> addApplications(int id, List<Integer> applicationIds) {
    Set<Integer> relatedProjects = getRelatedProjects(id, applicationIds);
    List<Integer> addedApplicationIds = projectService.addApplications(id, applicationIds);

    updateProjectSearch(new ArrayList<>(relatedProjects));

    List<ApplicationJson> added = getFullyPopulatedApplications(addedApplicationIds);
    searchService.updateApplications(added);
    return added;
  }

  public void removeApplication(int id) {
    Set<Integer> relatedProjects = getProjectsByApplications(Collections.singletonList(id));
    projectService.removeApplication(id);
    updateProjectSearch(new ArrayList<>(relatedProjects));

    List<ApplicationJson> added = getFullyPopulatedApplications(Arrays.asList(id));
    searchService.updateApplications(added);
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

  private Set<Integer> getRelatedProjects(int id, List<Integer> applicationIds) {
    Set<Integer> related = getProjectsByApplications(applicationIds);
    related.add(id);
    return related;
  }

  private Set<Integer> getProjectsByApplications(List<Integer> applicationIds) {
    return applicationService.findApplicationsById(applicationIds).stream()
        .map(Application::getProjectId)
        .filter(id -> id != null)
        .collect(Collectors.toSet());
  }

  /**
   * Update search index with the changed projects
   */
  private void updateProjectSearch(List<Integer> projectIds) {
    List<ProjectJson> updated = projectService.findByIds(projectIds).stream()
        .flatMap(project -> Stream.concat(
            Stream.of(project),
            projectService.findProjectParents(project.getId()).stream())
        ).collect(Collectors.toList());

    searchService.updateProjects(updated);
  }

  private List<ApplicationJson> getFullyPopulatedApplications(List<Integer> applicationIds) {
    return applicationService.findApplicationsById(new ArrayList<>(applicationIds)).stream()
        .map(a -> applicationJsonService.getFullyPopulatedApplication(a))
        .collect(Collectors.toList());
  }

}
