package fi.hel.allu.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.exception.SearchException;
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
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Service
public class ApplicationSearchService {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationSearchService.class);
  public static final String APPLICATION_INDEX_NAME = "allu";
  public static final String APPLICATION_TYPE_NAME = "application";
  public static final String FIND_ALL_FIELDS = "_all";

  private Client client;
  private ObjectMapper objectMapper;

  @Autowired
  public ApplicationSearchService(Client client, ObjectMapper objectMapper) {
    this.client = client;
    this.objectMapper = objectMapper;
    objectMapper.registerModule(new JavaTimeModule());
  }

  public void insertApplication(ApplicationES applicationES) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(applicationES);
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

  public List<ApplicationES> findByField(QueryParameters queryParameters) {
    List<ApplicationES> appList = null;
    try {

      if (queryParameters == null || queryParameters.getQueryParameters() == null) {
        throw new SearchException("Missing query parameters");
      }

      BoolQueryBuilder qb = boolQuery();

      for (QueryParameter param : queryParameters.getQueryParameters()) {
        if (param.getFieldName() != null && param.getFieldValue() != null) {
          qb.must(matchQuery(param.getFieldName(), param.getFieldValue()));
        } else if (param.getStartDateValue() != null && param.getEndDateValue() != null) {
          qb.must(QueryBuilders.rangeQuery(param.getFieldName()).from(param.getStartDateValue().toInstant().toEpochMilli()).to(
              param.getEndDateValue().toInstant().toEpochMilli()));
        }
      }
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
}
