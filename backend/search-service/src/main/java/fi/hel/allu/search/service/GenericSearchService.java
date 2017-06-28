package fi.hel.allu.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
          client.prepareIndex(indexName, indexTypeName, id).setSource(json, XContentType.JSON).get();
      if (response.status() != RestStatus.CREATED) {
        throw new SearchException("Unable to insert record to " + indexTypeName + " with id " + id);
      }
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  /**
   * Bulk insert objects to search index.
   *
   * @param idToIndexedObject   A map having object id as key and indexed object as value.
   */
  public void bulkInsert(Map<String, Object> idToIndexedObject) {
    List<DocWriteRequest> indexRequests =
        idToIndexedObject.entrySet().stream().map(entry -> createRequest(entry.getKey(), entry.getValue())).collect(Collectors.toList());

    executeBulk(indexRequests);
  }

  /**
   * Bulk update of the search index. This method can be used for partial updating existing object. If the value of <code>Map</code>
   * is a <code>Map</code>, the key of the value map is used as name of nested document to update. For example, if you want to update
   * application.customer with new customer, you can provide a following parameter (pseudocode)
   * <code> Map<applicationId, Map<"customer", customerObject>> </></code>.
   *
   * @param idToUpdatedObject Map having id of the updated object as key and object that will be updated to search index as JSON.
   */
  public void bulkUpdate(Map<String, Object> idToUpdatedObject) {
    List<DocWriteRequest> updateRequests =
        idToUpdatedObject.entrySet().stream().map(entry -> updateRequest(entry.getKey(), entry.getValue())).collect(Collectors.toList());

    executeBulk(updateRequests);
  }

  /**
   * Delete object from search index.
   *
   * @param id              Id to be deleted.
   */
  public void delete(String id) {
    DeleteResponse response = client.prepareDelete(indexName, indexTypeName, id).get();
    if (response == null || response.status() != RestStatus.OK) {
      throw new SearchException("Unable to delete record, id = " + id);
    }
  }

  /**
   * Search index with the given query parameters.
   * <p>Supports also partial searching. Note that partial search requires special ElasticSearch mapping for the field
   * (see {{@link ElasticSearchMappingConfig}}.
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

      // TODO: paging to search results. Before paging is implemented, the maximum number of results is configured below (100)
      SearchRequestBuilder srBuilder = client.prepareSearch(indexName).setSize(100).setTypes(indexTypeName).setQuery(qb);

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

  private UpdateRequest updateRequest(String id, Object indexedObject) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(indexedObject);
      logger.debug("Creating update request object in search index: {}", objectMapper.writeValueAsString(indexedObject));
      UpdateRequest updateRequest = new UpdateRequest();
      updateRequest.index(indexName);
      updateRequest.type(indexTypeName);
      updateRequest.id(id);
      updateRequest.doc(json, XContentType.JSON);
      return updateRequest;
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  private IndexRequest createRequest(String id, Object indexedObject) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(indexedObject);
      logger.debug("Creating create request object in search index: {}", objectMapper.writeValueAsString(indexedObject));
      IndexRequest indexRequest = new IndexRequest();
      indexRequest.index(indexName);
      indexRequest.type(indexTypeName);
      indexRequest.id(id);
      indexRequest.source(json, XContentType.JSON);
      return indexRequest;
    } catch (JsonProcessingException e) {
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
    if (QueryParameter.FIELD_NAME_RECURRING_APPLICATION.equals(queryParameter.getFieldName())) {
      // very special handling for recurring applications
      return createRecurringQueryBuilder(queryParameter);
    } else if (queryParameter.getFieldValue() != null) {
      return QueryBuilders.matchQuery(
          queryParameter.getFieldName(), queryParameter.getFieldValue()).operator(Operator.AND);
    } else if (queryParameter.getStartDateValue() != null && queryParameter.getEndDateValue() != null) {
      return QueryBuilders.rangeQuery(queryParameter.getFieldName())
          .gte(queryParameter.getStartDateValue().toInstant().toEpochMilli())
          .lte(queryParameter.getEndDateValue().toInstant().toEpochMilli());
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

  /**
   * Create recurring query, with the following conditions.
   * period1: search start time <= application end time AND search end time >= application start time
   * OR
   * period2: search start time <= application end time AND search end time >= application start time
   *
   * The period1 and period2 are calculated from given search period. If search period overlaps with one or more calendar years, the search
   * period is divided into two periods: period1 and period2.
   */
  private QueryBuilder createRecurringQueryBuilder(QueryParameter queryParameter) {
    ZonedDateTime startDate =
        queryParameter.getStartDateValue() == null ? RecurringApplication.BEGINNING_1972_DATE : queryParameter.getStartDateValue();
    ZonedDateTime endDate = queryParameter.getEndDateValue() == null ? RecurringApplication.MAX_END_TIME : queryParameter.getEndDateValue();
    RecurringApplication recurringApplication = new RecurringApplication(startDate, endDate, endDate);

    // in case any period start or end is set to zero, the search period is adjusted to 1 because otherwise range searches always include
    // results with 0 time (i.e. range search 0 <= x <= max is true vs. 1 <= x <= max is false, where x is 0). Non-existent periods
    // (meaning period2, in case indexed period is within one calendar year) are  indexed with 0 in start and end period
    long startPeriod1 = getRangeSearchCompliantPeriodLimit(recurringApplication.getPeriod1Start());
    long endPeriod1 = getRangeSearchCompliantPeriodLimit(recurringApplication.getPeriod1End());
    long startPeriod2 = getRangeSearchCompliantPeriodLimit(recurringApplication.getPeriod2Start());
    long endPeriod2 = getRangeSearchCompliantPeriodLimit(recurringApplication.getPeriod2End());

    BoolQueryBuilder qbPeriod1_1 = QueryBuilders.boolQuery();
    qbPeriod1_1 = qbPeriod1_1.must(QueryBuilders.rangeQuery("recurringApplication.period1Start").lte(endPeriod1));
    qbPeriod1_1 = qbPeriod1_1.must(QueryBuilders.rangeQuery("recurringApplication.period1End").gte(startPeriod1));

    BoolQueryBuilder qbPeriod1_2 = QueryBuilders.boolQuery();
    qbPeriod1_2 = qbPeriod1_2.must(QueryBuilders.rangeQuery("recurringApplication.period1Start").lte(endPeriod2));
    qbPeriod1_2 = qbPeriod1_2.must(QueryBuilders.rangeQuery("recurringApplication.period1End").gte(startPeriod2));

    BoolQueryBuilder qbPeriod2_1 = QueryBuilders.boolQuery();
    qbPeriod2_1 = qbPeriod2_1.must(QueryBuilders.rangeQuery("recurringApplication.period2Start").lte(endPeriod1));
    qbPeriod2_1 = qbPeriod2_1.must(QueryBuilders.rangeQuery("recurringApplication.period2End").gte(startPeriod1));

    BoolQueryBuilder qbPeriod2_2 = QueryBuilders.boolQuery();
    qbPeriod2_2 = qbPeriod2_2.must(QueryBuilders.rangeQuery("recurringApplication.period2Start").lte(endPeriod2));
    qbPeriod2_2 = qbPeriod2_2.must(QueryBuilders.rangeQuery("recurringApplication.period2End").gte(startPeriod2));

    BoolQueryBuilder qbRecurring1 = QueryBuilders.boolQuery();
    qbRecurring1 = qbRecurring1.should(qbPeriod1_1);
    qbRecurring1 = qbRecurring1.should(qbPeriod1_2);
    qbRecurring1.minimumShouldMatch(1);

    BoolQueryBuilder qbRecurring2 = QueryBuilders.boolQuery();
    qbRecurring2 = qbRecurring2.should(qbPeriod2_1);
    qbRecurring2 = qbRecurring2.should(qbPeriod2_2);
    qbRecurring2.minimumShouldMatch(1);

    BoolQueryBuilder qbCombined = QueryBuilders.boolQuery();
    qbCombined = qbCombined.must(QueryBuilders.rangeQuery("recurringApplication.startTime").lte(recurringApplication.getEndTime()));
    qbCombined = qbCombined.must(QueryBuilders.rangeQuery("recurringApplication.endTime").gte(recurringApplication.getStartTime()));
    qbCombined = qbCombined.should(qbRecurring1);
    qbCombined = qbCombined.should(qbRecurring2);
    qbCombined.minimumShouldMatch(1);

    return qbCombined;
  }

  private long getRangeSearchCompliantPeriodLimit(long startOrEnd) {
    return (startOrEnd == 0) ? 1 : startOrEnd;
  }

  private void executeBulk(List<DocWriteRequest> requests) {
    final BulkProcessor bp = BulkProcessor.builder(client, new BulkProcessorListener())
        .setConcurrentRequests(1)           // at most 1 concurrent request
        .setBulkActions(1000)               // maximum of 1000 updates per request
        .setBulkSize(new ByteSizeValue(-1)) // no byte size limit for bulk
        .build();

    requests.forEach(req -> bp.add(req));
    try {
      bp.awaitClose(10, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      throw new SearchException(e);
    }
  }

  private class BulkProcessorListener implements BulkProcessor.Listener {
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
  }
}
