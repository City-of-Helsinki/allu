package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ProjectJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ProjectMapper projectMapper;

  @Autowired
  public ProjectService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ProjectMapper projectMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.projectMapper = projectMapper;
  }

  /**
   * Search projects from ElasticSearch with given query.
   *
   * @param   queryParameters   Search query.
   * @return  Projects matching given search. Never <code>null</code>.
   */
  public List<ProjectJson> search(QueryParametersJson queryParameters) {
    // TODO: replace with real search. Added support for returning all existing project from database to support frontend development
    ResponseEntity<Project[]> responseEntity =
        restTemplate.getForEntity(applicationProperties.getModelServiceUrl("/projects/all"), Project[].class);
    return Arrays.stream(responseEntity.getBody()).map(p -> projectMapper.mapProjectToJson(p)).collect(Collectors.toList());
  }

  /**
   * Find project by id.
   *
   * @param   id  Id of the project.
   * @return  Requested project.
   */
  public ProjectJson findById(int id) {
    ResponseEntity<Project> responseEntity = restTemplate.getForEntity(applicationProperties.getProjectByIdUrl(), Project.class, id);
    return projectMapper.mapProjectToJson(responseEntity.getBody());
  }

  /**
   * Find project children.
   *
   * @param   id    Id of the project whose children will be returned.
   * @return  List of children projects. Never <code>null</code>.
   */
  public List<ProjectJson> findProjectChildren(int id) {
    ResponseEntity<Project[]> responseEntity = restTemplate.getForEntity(applicationProperties.getProjectChildrenUrl(), Project[].class, id);
    return Arrays.stream(responseEntity.getBody()).map(p -> projectMapper.mapProjectToJson(p)).collect(Collectors.toList());
  }

  /**
   * Find project parents.
   *
   * @param   id    Id of the project whose parents will be returned.
   * @return  List of parent projects. Never <code>null</code>.
   */
  public List<ProjectJson> findProjectParents(int id) {
    ResponseEntity<Project[]> responseEntity = restTemplate.getForEntity(applicationProperties.getProjectParentsUrl(), Project[].class, id);
    return Arrays.stream(responseEntity.getBody()).map(p -> projectMapper.mapProjectToJson(p)).collect(Collectors.toList());
  }

  /**
   * Create a new project.
   *
   * @param projectJson   Project that is going to be created
   * @return Created project
   */
  public ProjectJson insert(ProjectJson projectJson) {
    Project projectModel = restTemplate.postForObject(
        applicationProperties.getProjectCreateUrl(),
        projectMapper.createProjectModel(projectJson),
        Project.class);
    return projectMapper.mapProjectToJson(projectModel);
  }


  /**
   * Find applications of given project.
   *
   * @param   id    Id of the project whose applications should be returned.
   * @return  Applications of the given project. Never <code>null</code>.
   */
  public List<Application> findApplicationsByProject(int id) {
    ApplicationJson applicationJson = new ApplicationJson();
    ResponseEntity<Application[]> responseEntity =
        restTemplate.getForEntity(applicationProperties.getApplicationsByProjectUrl(), Application[].class, id);
    return Arrays.asList(responseEntity.getBody());
  }

  /**
   * Updates the information of given projects by going through the project / application hierarchy to find the up-to-date values. Updates
   * also all projects related to the given projects.
   *
   * @param projectIds List of projects to be updated.
   * @return Projects that have been updated. May contain more projects than in the list provided as a parameter.
   */
  public List<ProjectJson> updateProjectInformation(List<Integer> projectIds) {
    HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(projectIds);
    ResponseEntity<Project[]> updatedProjectResult = restTemplate.exchange(
        applicationProperties.getProjectInformationUpdateUrl(), HttpMethod.PUT, requestEntity, Project[].class);
    return Arrays.asList(updatedProjectResult.getBody()).stream().map(p -> projectMapper.mapProjectToJson(p)).collect(Collectors.toList());
  }

  /**
   * Update given project.
   *
   * @param projectJson Project that is going to be updated
   */
  ProjectJson update(int projectId, ProjectJson projectJson) {
    HttpEntity<Project> requestEntity = new HttpEntity<>(projectMapper.createProjectModel(projectJson));
    ResponseEntity<Project> response = restTemplate.exchange(
        applicationProperties.getProjectUpdateUrl(), HttpMethod.PUT, requestEntity, Project.class, projectId);
    return projectMapper.mapProjectToJson(response.getBody());
  }

  /**
   * Updates applications of given project to the given set of applications.
   *
   * @param id              Id of the project.
   * @param applicationIds  Application ids to be attached to the given project. Use empty list to clear all references to the given project.
   */
  ProjectJson updateProjectApplications(int id, List<Integer> applicationIds) {
    HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(applicationIds);
    ResponseEntity<Project> updatedProjectResult = restTemplate.exchange(
        applicationProperties.getApplicationProjectUpdateUrl(), HttpMethod.PUT, requestEntity, Project.class, id);
    return projectMapper.mapProjectToJson(updatedProjectResult.getBody());
  }

  /**
   * Update parent of the given project.
   *
   * @param id              Project whose parent should be updated.
   * @param parentProject   New parent project.
   * @return  Updated project.
   */
  ProjectJson updateProjectParent(int id, Integer parentProject) {
    HttpEntity<Integer> requestEntity = new HttpEntity<>(parentProject);
    ResponseEntity<Project> updatedProjectResult = restTemplate.exchange(
        applicationProperties.getProjectParentUpdateUrl(), HttpMethod.PUT, requestEntity, Project.class, id);
    return projectMapper.mapProjectToJson(updatedProjectResult.getBody());
  }
}