package fi.hel.allu.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_INDEX_NAME;
import static fi.hel.allu.search.config.ElasticSearchMappingConfig.PROJECT_TYPE_NAME;

/**
 * Support for project indexing and searching.
 */
@Service
public class ProjectSearchService {
  private Client client;
  private ObjectMapper objectMapper;
  private GenericSearchService genericSearchService;

  @Autowired
  public ProjectSearchService(
      GenericSearchService genericSearchService,
      Client client) {
    this.genericSearchService = genericSearchService;
    this.client = client;
    this.objectMapper = genericSearchService.getObjectMapper();
  }

  public void insertProject(ProjectES projectES) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(projectES);
      genericSearchService.insert(PROJECT_TYPE_NAME, projectES.getId().toString(), json);
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  public void updateProject(ProjectES projectES) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(projectES);
      genericSearchService.update(PROJECT_TYPE_NAME, projectES.getId().toString(), json);
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  public void updateProjects(List<ProjectES> projectESs) {
    projectESs.forEach(p -> updateProject(p));
  }

  public void deleteProject(String id) {
    genericSearchService.delete(PROJECT_TYPE_NAME, id);
  }

  public List<Integer> findByField(QueryParameters queryParameters) {
    return genericSearchService.findByField(PROJECT_TYPE_NAME, queryParameters);
  }

  public void deleteIndex() {
    genericSearchService.deleteIndex();
  }

  /**
   * Force index refresh. Use for testing only.
   */
  public void refreshIndex() {
    client.admin().indices().prepareRefresh(APPLICATION_INDEX_NAME).execute().actionGet();
  }
}
