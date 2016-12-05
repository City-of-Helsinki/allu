package fi.hel.allu.ui.service;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ProjectJson;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import fi.hel.allu.ui.mapper.ProjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
  @SuppressWarnings("unused")
  private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private ApplicationMapper applicationMapper;
  private ProjectMapper projectMapper;

  @Autowired
  public SearchService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      ApplicationMapper applicationMapper,
      ProjectMapper projectMapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.applicationMapper = applicationMapper;
    this.projectMapper = projectMapper;
  }

  public void insertApplication(ApplicationJson applicationJson) {
    restTemplate.postForObject(
        applicationProperties.getApplicationSearchCreateUrl(),
        applicationMapper.createApplicationESModel(applicationJson),
        ApplicationES.class);
  }

  /**
   * Update multiple applications to search index.
   *
   * @param applicationJsons Applications to be updated.
   */
  public void updateApplications(List<ApplicationJson> applicationJsons) {
    List<ApplicationES> applications =
        applicationJsons.stream().map(a -> applicationMapper.createApplicationESModel(a)).collect(Collectors.toList());
    restTemplate.put(
        applicationProperties.getApplicationsSearchUpdateUrl(),
        applications);
  }

  /**
   * Insert project to search index.
   *
   * @param projectJson Project to be indexed.
   */
  public void insertProject(ProjectJson projectJson) {
    restTemplate.postForObject(
        applicationProperties.getProjectSearchCreateUrl(),
        projectMapper.createProjectESModel(projectJson),
        ApplicationES.class);
  }

  /**
   * Update project in search index.
   *
   * @param projectJson Project to be updated.
   */
  public void updateProject(ProjectJson projectJson) {
    restTemplate.put(
        applicationProperties.getProjectSearchUpdateUrl(),
        projectMapper.createProjectESModel(projectJson),
        projectJson.getId().intValue());
  }

  /**
   * Update multiple projects to search index.
   *
   * @param projectJsons Projects to be updated.
   */
  public void updateProjects(List<ProjectJson> projectJsons) {
    List<ProjectES> projects = projectJsons.stream().map(p -> projectMapper.createProjectESModel(p)).collect(Collectors.toList());
    restTemplate.put(
        applicationProperties.getProjectsSearchUpdateUrl(),
        projects);
  }

  /**
   * Find applications by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of ids of found applications.
   */
  public List<Integer> searchApplication(QueryParameters queryParameters) {
    ResponseEntity<Integer[]> applicationResult = restTemplate.postForEntity(
        applicationProperties.getApplicationSearchUrl(), queryParameters, Integer[].class);

    return Arrays.asList(applicationResult.getBody());
  }

  /**
   * Find projects by given fields.
   *
   * @param queryParameters list of query parameters
   * @return List of ids of found projects.
   */
  public List<Integer> searchProject(QueryParameters queryParameters) {
    ResponseEntity<Integer[]> projectResult = restTemplate.postForEntity(
        applicationProperties.getProjectSearchUrl(), queryParameters, Integer[].class);

    return Arrays.asList(projectResult.getBody());
  }
}
