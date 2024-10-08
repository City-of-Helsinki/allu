package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.model.domain.ProjectChange;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.CompactProjectES;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ChangeHistoryItemJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ChangeHistoryMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Service
public class ProjectService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(ProjectService.class);

  private final ApplicationProperties applicationProperties;
  private final RestTemplate restTemplate;
  private final ProjectMapper projectMapper;
  private final UserService userService;
  private final ChangeHistoryMapper changeHistoryMapper;

  @Autowired
  public ProjectService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ProjectMapper projectMapper,
      UserService userService,
      ChangeHistoryMapper changeHistoryMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.projectMapper = projectMapper;
    this.userService = userService;
    this.changeHistoryMapper = changeHistoryMapper;
  }

  public ProjectJson findById(int id) {
    return findByIds(Arrays.asList(id)).stream()
        .findFirst()
        .orElseThrow(() -> new NoSuchEntityException("No project found", id));
  }

  /**
   * Find project by id.
   *
   * @param   ids  Ids of the projects.
   * @return  Requested projects.
   */
  public List<ProjectJson> findByIds(List<Integer> ids) {
    Project[] projects = restTemplate.postForObject(applicationProperties.getProjectsByIdUrl(), ids, Project[].class);
    return Arrays.stream(projects).map(p -> projectMapper.mapProjectToJson(p)).collect(Collectors.toList());
  }

  public List<Project> findProjectsByIds(List<Integer> ids) {
    Project[] projects = restTemplate.postForObject(applicationProperties.getProjectsByIdUrl(), ids, Project[].class);
    return Arrays.stream(projects).collect(Collectors.toList());
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
    List<ProjectJson> parentList = Arrays.stream(responseEntity.getBody()).map(p -> projectMapper.mapProjectToJson(p)).collect(Collectors.toList());
    // remove first project as it's always the project itself
    return parentList.subList(1, parentList.size());
  }

  /**
   * Create a new project.
   *
   * @param projectJson   Project that is going to be created
   * @return Created project
   */
  public ProjectJson insert(ProjectJson projectJson) {
    final UserJson currentUser = userService.getCurrentUser();
    projectJson.setCreator(currentUser);
    Project projectModel = restTemplate.postForObject(
        applicationProperties.getProjectCreateUrl(),
        new ProjectChange(currentUser.getId(), projectMapper.createProjectModel(projectJson)),
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
        applicationProperties.getProjectInformationUpdateUrl(), HttpMethod.PUT, requestEntity,
        Project[].class, userService.getCurrentUser().getId());
    return Arrays.asList(updatedProjectResult.getBody()).stream().map(p -> projectMapper.mapProjectToJson(p)).collect(Collectors.toList());
  }

  /**
   * Update given project.
   *
   * @param projectJson Project that is going to be updated
   */
  ProjectJson update(int projectId, ProjectJson projectJson) {
    HttpEntity<ProjectChange> requestEntity = new HttpEntity<>(new ProjectChange(
        userService.getCurrentUser().getId(), projectMapper.createProjectModel(projectJson)));
    ResponseEntity<Project> response = restTemplate.exchange(
        applicationProperties.getProjectUpdateUrl(), HttpMethod.PUT, requestEntity, Project.class, projectId);
    return projectMapper.mapProjectToJson(response.getBody());
  }

  List<Integer> addApplications(int id, List<Integer> applicationIds) {
    HttpEntity<List<Integer>> requestEntity = new HttpEntity<>(applicationIds);
    ResponseEntity<Integer[]> result = restTemplate.exchange(
        applicationProperties.getProjectApplicationsAddUrl(), HttpMethod.PUT, requestEntity,
        Integer[].class, id, userService.getCurrentUser().getId());
    return Arrays.asList(result.getBody());
  }

  void removeApplication(int id) {
    restTemplate.delete(applicationProperties.getProjectApplicationRemoveUrl(), id, userService.getCurrentUser().getId());
  }

  /**
   * Marks project and it's child projects deleted
   */
  void delete(int id) {
    restTemplate.delete(applicationProperties.getProjectUpdateUrl(), id);
  }

  /**
   * Update parent of the given project.
   *
   * @param id              Project whose parent should be updated.
   * @param parentProject   New parent project.
   * @return  Updated project.
   */
  ProjectJson updateProjectParent(int id, Integer parentProject) {
    HttpEntity<String> requestEntity = new HttpEntity<>("empty");
    ResponseEntity<Project> updatedProjectResult = restTemplate.exchange(
        applicationProperties.getProjectParentUpdateUrl(), HttpMethod.PUT, requestEntity,
        Project.class, id, parentProject, userService.getCurrentUser().getId());
    return projectMapper.mapProjectToJson(updatedProjectResult.getBody());
  }

  public List<ChangeHistoryItemJson> getChanges(Integer projectId) {
    return Arrays.stream(
        restTemplate.getForObject(applicationProperties.getProjectHistoryUrl(), ChangeHistoryItem[].class, projectId))
        .map(c -> changeHistoryMapper.mapToJson(c))
        .collect(Collectors.toList());
  }

  public Integer getNextProjectNumber() {
    return restTemplate.postForObject(applicationProperties.getProjectNextProjectNumberUrl(), null, Integer.class);
  }

  public BiConsumer<Project, ApplicationES> setIdentifier = (project, applicationES) -> {
    CompactProjectES projectES = new CompactProjectES();
    projectES.setId(project.getId());
    projectES.setIdentifier(project.getIdentifier());
    applicationES.setProject(projectES);
  };

  public List<ApplicationES> mapProjectToEs(List<ApplicationES> listToPopulated) {
    Map<Integer, List<ApplicationES>> projectIds = listToPopulated.stream().filter(e -> e.getProject() != null)
            .collect(Collectors.groupingBy(e -> e.getProject().getId()));
    List<ApplicationES> result = listToPopulated.stream().filter(e -> e.getProject() == null)
            .collect(Collectors.toList());
    List<Project> projectList = findProjectsByIds(new ArrayList<>(projectIds.keySet()));
    result.addAll(projectMapper.populateValues(projectIds, setIdentifier, projectList));
    return result;
  }
}