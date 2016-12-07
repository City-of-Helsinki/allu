package fi.hel.allu.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_INDEX_NAME;
import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_TYPE_NAME;

@Service
public class ApplicationSearchService {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationSearchService.class);

  private Client client;
  private ObjectMapper objectMapper;
  private GenericSearchService genericSearchService;

  @Autowired
  public ApplicationSearchService(
      GenericSearchService genericSearchService,
      Client client) {
    this.genericSearchService = genericSearchService;
    this.client = client;
    this.objectMapper = genericSearchService.getObjectMapper();
  }

  public void insertApplication(ApplicationES applicationES) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(applicationES);
      logger.debug("Inserting new application to search index: {}", objectMapper.writeValueAsString(applicationES));
      genericSearchService.insert(APPLICATION_TYPE_NAME, applicationES.getId().toString(), json);
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  public void updateApplications(List<ApplicationES> applicationESs) {
    applicationESs.forEach(a -> updateApplication(a));
  }

  public void deleteApplication(String id) {
    genericSearchService.delete(APPLICATION_TYPE_NAME, id);
  }

  public List<Integer> findByField(QueryParameters queryParameters) {
    return genericSearchService.findByField(APPLICATION_TYPE_NAME, queryParameters);
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

  private void updateApplication(ApplicationES applicationES) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(applicationES);
      logger.debug("Updating application to search index: {}", objectMapper.writeValueAsString(applicationES));
      genericSearchService.update(APPLICATION_TYPE_NAME, applicationES.getId().toString(), json);
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }
}
