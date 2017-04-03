package fi.hel.allu.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Generic ElasticSearch functionality for different kinds of searches.
 */
public class GenericSearchService {
  private static final Logger logger = LoggerFactory.getLogger(GenericSearchService.class);

  private ElasticSearchMappingConfig elasticSearchMappingConfig;
  private Client client;
  private ObjectMapper objectMapper;
  private String indexName;
  private String indexTypeName;

  public GenericSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client,
      String indexName,
      String indexTypeName) {
    this.elasticSearchMappingConfig = elasticSearchMappingConfig;
    this.client = client;
    this.indexName = indexName;
    this.indexTypeName = indexTypeName;
    this.objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  /**
   * Insert given JSON to search index using given type.
   *
   * @param id              Id of the object added to search index.
   * @param indexedObject   Data added to search index.
   */
  public void insert(String id, Object indexedObject) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(indexedObject);
      logger.debug("Inserting new object to search index {}: {}", indexName, objectMapper.writeValueAsString(indexedObject));
      IndexResponse response =
          client.prepareIndex(indexName, indexTypeName, id).setSource(json).get();
      if (!response.isCreated()) {
        throw new SearchException("Unable to insert record to " + indexTypeName + " with id " + id);
      }
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  /**
   * Update search index with given objects. This method can be used for partial updating existing object. If the value of <code>Map</code>
   * is a <code>Map</code>, the key of the value map is used as name of nested document to update. For example, if you want to update
   * application.applicant with new applicant, you can provide a following parameter (pseudocode)
   * <code> Map<applicationId, Map<"applicant", applicantObject>> </></code>.
   *
   * @param idToIndexedObjects  Map having id as value and indexed object update as value.
   */
  public void update(Map<String, Object> idToIndexedObjects) {
    // TODO: change to use bulk update
    idToIndexedObjects.entrySet().forEach(entry -> update(entry.getKey(), entry.getValue()));
  }

  /**
   * Bulk update of the search index.
   *
   * @param idToUpdateDate Map having id of the updated object as key and object that will be updated to search index as JSON.
   */
  public void bulkUpdate(Map<String, Object> idToUpdateDate) {
    final BulkProcessor bp = BulkProcessor.builder(
        client,
        new BulkProcessor.Listener() {
          @Override
          public void beforeBulk(long executionId, BulkRequest request) {
          }

          @Override
          public void afterBulk(long executionId, BulkRequest request, Throwable t) {
            logger.error("Bulk operation failed", t);
          }

          @Override
          public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            logger.debug("Bulk execution completed [" + executionId + "]. Took (ms): " + response.getTookInMillis() + ". Failures: "
                + response.hasFailures() + ". Count: " + response.getItems().length);
          }
        })
        .setConcurrentRequests(1)           // at most 1 concurrent request
        .setBulkActions(1000)               // maximum of 1000 updates per request
        .setBulkSize(new ByteSizeValue(-1)) // no byte size limit for bulk
        .build();
    try {
      for (Map.Entry<String, Object> entry : idToUpdateDate.entrySet()) {
        bp.add(new UpdateRequest(indexName, indexTypeName, entry.getKey()).doc(objectMapper.writeValueAsBytes(entry.getValue())));
      }
    } catch (Exception e) {
      // this should never happen
      throw new SearchException(e);
    } finally {
      try {
        bp.awaitClose(10, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        throw new SearchException(e);
      }
    }
  }

  /**
   * Delete object from search index.
   *
   * @param id              Id to be deleted.
   */
  public void delete(String id) {
    DeleteResponse response = client.prepareDelete(indexName, indexTypeName, id).get();
    if (response == null || !response.isFound()) {
      throw new SearchException("Unable to delete record, id = " + id);
    }
  }

  /**
   * Search index with the given query parameters.
   *
   * @param queryParameters   Query parameters.
   * @return List of ids found from search index. The list is ordered as specified by the query parameters.
   */
  public List<Integer> findByField(QueryParameters queryParameters) {
    try {
      BoolQueryBuilder qb = QueryBuilders.boolQuery();

      for (QueryParameter param : queryParameters.getQueryParameters()) {
        qb.must(createQueryBuilder(param));
      }

      SearchRequestBuilder srBuilder = client.prepareSearch(indexName).setTypes(indexTypeName).setQuery(qb);

      if (queryParameters.getSort() != null) {
        SortBuilder sb = SortBuilders.fieldSort(queryParameters.getSort().field);
        if (queryParameters.getSort().direction.equals(QueryParameters.Sort.Direction.ASC)) {
          sb.order(SortOrder.ASC);
        } else {
          sb.order(SortOrder.DESC);
        }
        srBuilder.addSort(sb);
      }

      logger.debug("Searching index {} with the following query:\n {}", indexName, srBuilder.toString());

      SearchResponse response = srBuilder.setFetchSource("id","").execute().actionGet();
      return iterateIntSearchResponse(response);
    } catch (IOException e) {
      throw new SearchException(e);
    }
  }

  /**
   * Partial search against given field. Note that partial search requires special ElasticSearch mapping for the field
   * (see {{@link ElasticSearchMappingConfig}}.
   *
   * @param field         Field matched against search string.
   * @param searchString  String to be searched.
   */
  public List<Integer> findPartial(String field, String searchString) {

    try {
      QueryBuilder qb = QueryBuilders.matchQuery(field, searchString);
      SearchRequestBuilder srBuilder = client.prepareSearch(indexName).setTypes(indexTypeName).setQuery(qb);
      logger.debug("Partial searching with the following query:\n {}", srBuilder.toString());
      SearchResponse response = srBuilder.setFetchSource("id","").execute().actionGet();
      return iterateIntSearchResponse(response);
    } catch (IOException e) {
      throw new SearchException(e);
    }
  }

  /**
   * Finds an object from ElasticSearch with given id.
   *
   * @param id          Id of the searched object.
   * @param valueType   Type of the object.
   * @return Found value.
   */
  public <T> Optional<T> findObjectById(String id, Class<T> valueType) {
    QueryBuilder qb = QueryBuilders.matchQuery("_id", id);
    SearchRequestBuilder srBuilder = client.prepareSearch(indexName).setTypes(indexTypeName).setQuery(qb);
    logger.debug("Finding object with the following query:\n {}", srBuilder.toString());
    SearchResponse response = srBuilder.execute().actionGet();
    if (response != null) {
      SearchHits hits = response.getHits();
      if (hits.getTotalHits() != 1) {
        return Optional.empty();
      } else {
        try {
          return Optional.of(objectMapper.readValue(hits.getAt(0).getSourceAsString(), valueType));
        } catch (IOException e) {
          throw new SearchException(e);
        }
      }
    } else {
      return Optional.empty();
    }
  }

  /**
   * Deletes search index and re-initializes new index with the correct mapping.
   */
  public void deleteIndex(String index) {
    DeleteIndexResponse response = client.admin().indices().delete(new DeleteIndexRequest(index)).actionGet();
    if (response == null || !response.isAcknowledged()) {
      throw new SearchException("Unable to delete application index");
    } else {
      // make sure index with proper configuration exists for later use
      elasticSearchMappingConfig.initializeIndex();
    }
  }

  public void deleteIndex() {
    deleteIndex(indexName);
  }

  /**
   * Force index refresh. Use for testing only.
   */
  public void refreshIndex() {
    client.admin().indices().prepareRefresh(indexName).execute().actionGet();
  }


  private void update(String id, Object indexedObject) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(indexedObject);
      logger.debug("Updating object in search index: {}", objectMapper.writeValueAsString(indexedObject));
      UpdateRequest updateRequest = new UpdateRequest();
      updateRequest.index(indexName);
      updateRequest.type(indexTypeName);
      updateRequest.id(id);
      updateRequest.doc(json);
      UpdateResponse updateResponse = client.update(updateRequest).get();
      if (updateResponse == null) {
        throw new SearchException("Unable to update record of type " + indexTypeName + " with id " + id);
      }
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    } catch (InterruptedException e) {
      throw new SearchException(e);
    } catch (ExecutionException e) {
      throw new SearchException(e);
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
