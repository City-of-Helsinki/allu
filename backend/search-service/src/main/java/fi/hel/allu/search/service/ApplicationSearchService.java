package fi.hel.allu.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_INDEX_NAME;
import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_TYPE_NAME;

@Service
public class ApplicationSearchService {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationSearchService.class);
  public static final String FIND_ALL_FIELDS = "_all";

  ElasticSearchMappingConfig elasticSearchMappingConfig;
  private Client client;
  private ObjectMapper objectMapper;

  @Autowired
  public ApplicationSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig, Client client, ObjectMapper objectMapper) {
    this.elasticSearchMappingConfig = elasticSearchMappingConfig;
    this.client = client;
    this.objectMapper = objectMapper;
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public void insertApplication(ApplicationES applicationES) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(applicationES);
      System.out.println(objectMapper.writeValueAsString(applicationES));
      IndexResponse response = client.prepareIndex(APPLICATION_INDEX_NAME, APPLICATION_TYPE_NAME, applicationES.getId().toString())
          .setSource(json).get();
      if (!response.isCreated()) {
        throw new SearchException("Unable to insert application record");
      }
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  public void updateApplication(String id, ApplicationES applicationES) {
    try {
      if (id == null || applicationES.getId() == null || !id.equals(applicationES.getId().toString())) {
        throw new SearchException("Invalid application id");
      }

      byte[] json = objectMapper.writeValueAsBytes(applicationES);
      UpdateRequest updateRequest = new UpdateRequest();
      updateRequest.index(APPLICATION_INDEX_NAME);
      updateRequest.type(APPLICATION_TYPE_NAME);
      updateRequest.id(applicationES.getId().toString());
      updateRequest.doc(json);
      UpdateResponse updateResponse = client.update(updateRequest).get();
      if (updateResponse == null) {
        throw new SearchException("Unable to update application record");
      }
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    } catch (InterruptedException e) {
      throw new SearchException(e);
    } catch (ExecutionException e) {
      throw new SearchException(e);
    }
  }

  public void deleteApplication(String id) {
    DeleteResponse response = client.prepareDelete(APPLICATION_INDEX_NAME, APPLICATION_TYPE_NAME, id).get();
    if (response == null || !response.isFound()) {
      throw new SearchException("Unable to delete application record, id=" + id);
    }
  }

  public void deleteIndex() {
    DeleteIndexResponse response = client.admin().indices().delete(new DeleteIndexRequest(APPLICATION_INDEX_NAME)).actionGet();
    if (response == null || !response.isAcknowledged()) {
      throw new SearchException("Unable to delete application index");
    } else {
      // make sure index with proper configuration exists for later use
      elasticSearchMappingConfig.initializeIndex();
    }
  }

  public ApplicationES findById(String id) {
    ApplicationES applicationES = null;
    GetResponse response = client.prepareGet(APPLICATION_INDEX_NAME, APPLICATION_TYPE_NAME, id).get();
    if (response.isExists()) {
      try {
        applicationES = objectMapper.readValue(response.getSourceAsBytes(), ApplicationES.class);
      } catch (IOException e) {
        throw new SearchException(e);
      }
    } else {
      throw new NoSuchEntityException("Application not found");
    }
    return applicationES;
  }

  // TODO: Perttu 13.7.16.: it would be smarter to return list of application ids and avoid mapping ElasticSearch data back to Application data
  public List<ApplicationES> findByField(QueryParameters queryParameters) {
    List<ApplicationES> appList = null;
    try {

      if (queryParameters == null || queryParameters.getQueryParameters() == null) {
        throw new SearchException("Missing query parameters");
      }

      BoolQueryBuilder qb = QueryBuilders.boolQuery();

      for (QueryParameter param : queryParameters.getQueryParameters()) {
        qb.must(createQueryBuilder(param));
      }

      logger.debug("Searching with the following query:\n {}", qb.toString());

      SearchResponse response = client.prepareSearch(APPLICATION_INDEX_NAME)
          .setTypes(APPLICATION_TYPE_NAME)
          .setQuery(qb)
          .execute()
          .actionGet();

      appList = iterateSearchResponse(response);
    } catch (IOException e) {
      throw new SearchException(e);
    }
    return appList;
  }

  // TODO: Perttu 13.7.16.: it would be smarter to return list of application ids and avoid mapping ElasticSearch data back to Application data
  public List<ApplicationES> findFromAllFields(String queryString) {
    List<ApplicationES> appList = null;
    try {

      SearchResponse response = client.prepareSearch(APPLICATION_INDEX_NAME)
          .setTypes(APPLICATION_TYPE_NAME)
          .setQuery(QueryBuilders.wildcardQuery(FIND_ALL_FIELDS, queryString))
          .execute()
          .actionGet();

      appList = iterateSearchResponse(response);
    } catch (IOException e) {
      throw new SearchException(e);
    }
    return appList;
  }

  /**
   * Force index refresh. Use for testing only.
   */
  public void refreshIndex() {
    client.admin().indices().prepareRefresh(APPLICATION_INDEX_NAME).execute().actionGet();
  }

  private List<ApplicationES> iterateSearchResponse(SearchResponse response) throws IOException {
    List<ApplicationES> appList = new ArrayList<>();
    if (response != null) {
      for (SearchHit hit : response.getHits()) {
        ApplicationES applicationES = null;
        applicationES = objectMapper.readValue(hit.getSourceAsString(), ApplicationES.class);
        appList.add(applicationES);
      }
    }
    return appList;
  }

  private QueryBuilder createQueryBuilder(QueryParameter queryParameter) {
    if (queryParameter.getFieldValue() != null) {
      return QueryBuilders.matchQuery(
          queryParameter.getFieldName(), queryParameter.getFieldValue())
          .operator(MatchQueryBuilder.Operator.AND);
    } else if (queryParameter.getStartDateValue() != null && queryParameter.getEndDateValue() != null) {
      return QueryBuilders.rangeQuery(queryParameter.getFieldName()).from(queryParameter.getStartDateValue().toInstant().toEpochMilli()).to(
          queryParameter.getEndDateValue().toInstant().toEpochMilli());
    } else if (queryParameter.getFieldMultiValue() != null) {
      BoolQueryBuilder qb = QueryBuilders.boolQuery();
      for (String searchTerm : queryParameter.getFieldMultiValue()) {
        qb = qb.should(QueryBuilders.matchQuery(queryParameter.getFieldName(), searchTerm));
      }
      return qb;
    } else {
      throw new UnsupportedOperationException("Unknown query value type: " + queryParameter.getFieldValue().getClass().toString());
    }
  }
}
