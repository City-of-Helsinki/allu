package fi.hel.allu.search.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_INDEX_NAME;

/**
 * Generic ElasticSearch functionality for different kinds of searches.
 */
@Service
public class GenericSearchService {
  private static final Logger logger = LoggerFactory.getLogger(GenericSearchService.class);

  private ElasticSearchMappingConfig elasticSearchMappingConfig;
  private Client client;
  private ObjectMapper objectMapper;

  @Autowired
  public GenericSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig, Client client) {
    this.elasticSearchMappingConfig = elasticSearchMappingConfig;
    this.client = client;
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  /**
   * Insert given JSON to search index using given type.
   *
   * @param indexTypeName   Type of the object added to search index.
   * @param id              Id of the object added to search index.
   * @param json            Data added to search index.
   */
  public void insert(String indexTypeName, String id, byte[] json) {
    logger.debug("Insert to search index: {}", indexTypeName);
    IndexResponse response =
        client.prepareIndex(APPLICATION_INDEX_NAME, indexTypeName, id).setSource(json).get();
    if (!response.isCreated()) {
      throw new SearchException("Unable to insert record to " + indexTypeName + " with id " + id);
    }
  }

  /**
   * Update search index.
   *
   * @param indexTypeName   Type of the object updated to search index.
   * @param id              Id of the object updated to search index.
   * @param json            Data to be updated to search index.
   */
  public void update(String indexTypeName, String id, byte[] json) {
    try {
      UpdateRequest updateRequest = new UpdateRequest();
      updateRequest.index(APPLICATION_INDEX_NAME);
      updateRequest.type(indexTypeName);
      updateRequest.id(id);
      updateRequest.doc(json);
      UpdateResponse updateResponse = client.update(updateRequest).get();
      if (updateResponse == null) {
        throw new SearchException("Unable to update record of type " + indexTypeName + " with id " + id);
      }
    } catch (InterruptedException e) {
      throw new SearchException(e);
    } catch (ExecutionException e) {
      throw new SearchException(e);
    }
  }

  /**
   * Delete object from search index.
   *
   * @param indexTypeName   Type of the deleted object.
   * @param id              Id to be deleted.
   */
  public void delete(String indexTypeName, String id) {
    DeleteResponse response = client.prepareDelete(APPLICATION_INDEX_NAME, indexTypeName, id).get();
    if (response == null || !response.isFound()) {
      throw new SearchException("Unable to delete record, id = " + id);
    }
  }

  /**
   * Search index with the given query parameters.
   *
   * @param indexTypeName     Type of the searched object.
   * @param queryParameters   Query parameters.
   * @return List of ids found from search index. The list is ordered as specified by the query parameters.
   */
  public List<Integer> findByField(String indexTypeName, QueryParameters queryParameters) {
    try {
      BoolQueryBuilder qb = QueryBuilders.boolQuery();

      for (QueryParameter param : queryParameters.getQueryParameters()) {
        qb.must(createQueryBuilder(param));
      }

      SearchRequestBuilder srBuilder = client.prepareSearch(APPLICATION_INDEX_NAME).setTypes(indexTypeName).setQuery(qb);

      if (queryParameters.getSort() != null) {
        SortBuilder sb = SortBuilders.fieldSort(queryParameters.getSort().field);
        if (queryParameters.getSort().direction.equals(QueryParameters.Sort.Direction.ASC)) {
          sb.order(SortOrder.ASC);
        } else {
          sb.order(SortOrder.DESC);
        }
        srBuilder.addSort(sb);
      }

      logger.debug("Searching with the following query:\n {}", srBuilder.toString());

      SearchResponse response = srBuilder.setFetchSource("id","").execute().actionGet();
      return iterateIntSearchResponse(response);
    } catch (IOException e) {
      throw new SearchException(e);
    }
  }

  /**
   * Deletes search index and re-initializes new index with the correct mapping.
   */
  public void deleteIndex() {
    DeleteIndexResponse response = client.admin().indices().delete(new DeleteIndexRequest(APPLICATION_INDEX_NAME)).actionGet();
    if (response == null || !response.isAcknowledged()) {
      throw new SearchException("Unable to delete application index");
    } else {
      // make sure index with proper configuration exists for later use
      elasticSearchMappingConfig.initializeIndex();
    }
  }

  private List<Integer> iterateIntSearchResponse(SearchResponse response) throws IOException {
    List<Integer> appList = new ArrayList<>();
    if (response != null) {
      for (SearchHit hit : response.getHits()) {
        IntResponse intResponse = objectMapper.readValue(hit.getSourceAsString(), IntResponse.class);
        appList.add(intResponse.getId());
      }
    }
    return appList;
  }

  private static class IntResponse {
    private Integer id;

    public Integer getId() {
      return id;
    }

    public void setId(Integer id) {
      this.id = id;
    }
  }

  private QueryBuilder createQueryBuilder(QueryParameter queryParameter) {
    if (queryParameter.getFieldValue() != null) {
      return QueryBuilders.matchQuery(
          queryParameter.getFieldName(), queryParameter.getFieldValue()).operator(MatchQueryBuilder.Operator.AND);
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
