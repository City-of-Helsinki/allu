package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ProjectJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
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
  private ApplicationMapper applicationMapper;
  private ApplicationService applicationService;

  @Autowired
  public ProjectService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      ApplicationService applicationService) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.applicationService = applicationService;
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
    return Arrays.stream(responseEntity.getBody()).map(p -> mapProjectToJson(p)).collect(Collectors.toList());
  }

  /**
   * Find project by id.
   *
   * @param   id  Id of the project.
   * @return  Requested project.
   */
  public ProjectJson findById(int id) {
    ResponseEntity<Project> responseEntity = restTemplate.getForEntity(applicationProperties.getProjectByIdUrl(), Project.class, id);
    return mapProjectToJson(responseEntity.getBody());
  }

  /**
   * Find project children.
   *
   * @param   id    Id of the project whose children will be returned.
   * @return  List of children projects. Never <code>null</code>.
   */
  public List<ProjectJson> findProjectChildren(int id) {
    ResponseEntity<Project[]> responseEntity = restTemplate.getForEntity(applicationProperties.getProjectChildrenUrl(), Project[].class, id);
    return Arrays.stream(responseEntity.getBody()).map(p -> mapProjectToJson(p)).collect(Collectors.toList());
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
        createProjectModel(projectJson),
        Project.class);
    return mapProjectToJson(projectModel);
  }


  /**
   * Update given project.
   *
   * @param projectJson Project that is going to be updated
   */
  public ProjectJson update(int projectId, ProjectJson projectJson) {
    HttpEntity<Project> requestEntity = new HttpEntity<>(createProjectModel(projectJson));
    ResponseEntity<Project> response = restTemplate.exchange(
      applicationProperties.getProjectUpdateUrl(), HttpMethod.PUT, requestEntity, Project.class, projectId);
    return mapProjectToJson(response.getBody());
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
   * Updates applications of given project to the given set of applications.
   *
   * @param id              Id of the project.
   * @param applicationIds  Application ids to be attached to the given project. Use empty list to clear all references to the given project.
   */
  public ProjectJson updateProjectApplications(int id, List<Integer> applicationIds) {
    // TODO: Käytiin Kimmon kanssa läpi ja merkkaan korjattavaksi tämän itselleni samalla, kun korjaan ElasticSearch-indeksoinnin.
    // TODO: Tässä oli alla virheenä se, että ensimmäisen REST-kutsun piti hakea modelilta annetuilla hakemus-id:llä eikä annetun
    // TODO: projektin hakemuksia. Tarkoitus on siis se, että annetut hakemus-id:t ovat saattaneet viitata aiemmin johonkin toiseen
    // TODO: projektiin ja näiden tiedot pitää päivittää myös ElasticSearchiin.
    ResponseEntity<Application[]> applicationResult = restTemplate.getForEntity(
        applicationProperties.getApplicationsByProjectUrl(),
        Application[].class,
        id);
    List<Application> updatedApplications = Arrays.asList(applicationResult.getBody());
    List<Integer> updatedProjectIds = updatedApplications.stream()
        .filter(a -> a.getProjectId() != null)
        .map(Application::getProjectId)
        .distinct()
        .collect(Collectors.toList());
    updatedProjectIds.add(id);

    HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(applicationIds);
    ResponseEntity<Project> updatedProjectResult = restTemplate.exchange(
        applicationProperties.getApplicationProjectUpdateUrl(), HttpMethod.PUT, requestEntity, Project.class, id);
    return mapProjectToJson(updatedProjectResult.getBody());
    // TODO: update updatedApplications in the ES (project id change)
    // TODO: update updatedProjectIds in the ES
  }


  /**
   * Update parent of the given project.
   *
   * @param id              Project whose parent should be updated.
   * @param parentProject   New parent project.
   * @return  Updated project.
   */
  public ProjectJson updateProjectParent(int id, Integer parentProject) {
    HttpEntity<Integer> requestEntity = new HttpEntity<>(parentProject);
    ResponseEntity<Project> updatedProjectResult = restTemplate.exchange(
        applicationProperties.getProjectParentUpdateUrl(), HttpMethod.PUT, requestEntity, Project.class, id);
    return mapProjectToJson(updatedProjectResult.getBody());
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
    return Arrays.asList(updatedProjectResult.getBody()).stream().map(p -> mapProjectToJson(p)).collect(Collectors.toList());
  }

  private Project createProjectModel(ProjectJson projectJson) {
    Project projectDomain = new Project();
    projectDomain.setId(projectJson.getId());
    projectDomain.setName(projectJson.getName());
    projectDomain.setStartTime(projectJson.getStartTime());
    projectDomain.setEndTime(projectJson.getEndTime());
    projectDomain.setOwnerName(projectJson.getOwnerName());
    projectDomain.setContactName(projectJson.getContactName());
    projectDomain.setEmail(projectJson.getEmail());
    projectDomain.setPhone(projectJson.getPhone());
    projectDomain.setCustomerReference(projectJson.getCustomerReference());
    projectDomain.setAdditionalInfo(projectJson.getAdditionalInfo());
    projectDomain.setParentId(projectJson.getParentId());
    return projectDomain;
  }

  private ProjectJson mapProjectToJson(Project projectDomain) {
    ProjectJson projectJson = new ProjectJson();
    projectJson.setId(projectDomain.getId());
    projectJson.setName(projectDomain.getName());
    projectJson.setStartTime(projectDomain.getStartTime());
    projectJson.setEndTime(projectDomain.getEndTime());
    projectJson.setOwnerName(projectDomain.getOwnerName());
    projectJson.setContactName(projectDomain.getContactName());
    projectJson.setEmail(projectDomain.getEmail());
    projectJson.setPhone(projectDomain.getPhone());
    projectJson.setCustomerReference(projectDomain.getCustomerReference());
    projectJson.setAdditionalInfo(projectDomain.getAdditionalInfo());
    projectJson.setParentId(projectDomain.getParentId());
    return projectJson;
  }
}
