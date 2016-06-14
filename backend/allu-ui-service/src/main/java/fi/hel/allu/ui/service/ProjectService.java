package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Project;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ProjectJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ProjectService {
  private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

  private ApplicationProperties applicationProperties;

  private RestTemplate restTemplate;

  @Autowired
  public ProjectService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Create a new project.
   *
   * @param projectJson Project that is going to be created
   * @return Created project
   */
  public ProjectJson createProject(ProjectJson projectJson) {
    if (projectJson == null || projectJson.getId() == null) {
      if (projectJson == null) {
        projectJson = new ProjectJson();
      }
      Project projectModel = restTemplate.postForObject(applicationProperties
              .getUrl(ApplicationProperties.PATH_MODEL_PROJECT_CREATE), createProjectModel(projectJson),
          Project.class);
      mapProjectToJson(projectJson, projectModel);
    }
    return projectJson;
  }

  /**
   * Update given project. Project id is needed to update the given project.
   *
   * @param projectJson Project that is going to be updated
   */
  public void updateProject(ProjectJson projectJson) {
    if (projectJson != null && projectJson.getId() != null && projectJson.getId() > 0) {
      restTemplate.put(applicationProperties.getUrl(ApplicationProperties.PATH_MODEL_PROJECT_UPDATE), createProjectModel(projectJson),
          projectJson.getId().intValue());
    }
  }

  /**
   * Find given project details.
   *
   * @param projectId Project identifier that is used to find details
   * @return Project details or empty project object
   */
  public ProjectJson findProjectById(int projectId)  {
    ProjectJson projectJson = new ProjectJson();
    ResponseEntity<Project> projectResult = restTemplate.getForEntity(applicationProperties
        .getUrl(ApplicationProperties.PATH_MODEL_PROJECT_FIND_BY_ID), Project.class, projectId);
    mapProjectToJson(projectJson, projectResult.getBody());
    return projectJson;
  }

  private Project createProjectModel(ProjectJson projectJson) {
    Project projectDomain = new Project();
    if (projectJson.getId() != null) {
      projectDomain.setId(projectJson.getId());
    }
    if (projectJson.getName() == null) {
      projectDomain.setName("Mock Project");
    } else {
      projectDomain.setName(projectJson.getName());
    }
    return projectDomain;
  }

  private void mapProjectToJson(ProjectJson projectJson, Project projectDomain) {
    projectJson.setId(projectDomain.getId());
    projectJson.setName(projectDomain.getName());
  }
}
