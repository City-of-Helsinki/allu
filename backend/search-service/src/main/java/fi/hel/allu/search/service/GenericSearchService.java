package fi.hel.allu.search.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.search.util.CustomersIndexUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;


/**
 * Generic ElasticSearch functionality for different kinds of searches.
 */
public class GenericSearchService<T, Q extends QueryParameters> {
  private static final Logger logger = LoggerFactory.getLogger(GenericSearchService.class);
  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_PAGESIZE = 100;
  protected static final Pageable DEFAULT_PAGEREQUEST = PageRequest.of(DEFAULT_PAGE, DEFAULT_PAGESIZE);

  /* Sort field suffix for alphabetic sort */
  private static final String ALPHASORT = ".alphasort";

  /* Sort field suffix for ordinal sort */
  private static final String ORDINAL = ".ordinal";

  /* Fields that should be sorted alphabetically */
  private static final String[] alphaSortFields = {
    "name",
    "customers.applicant.customer.name",
    "customer.name",
    "contacts.name",
    "locations.streetAddress",
    "locations.address",
    "applicationId",
    "ownerName",
    "owner.userName",
    "owner.realName",
    "registryKey",
    "project.identifier",
    "identifier"

  };

  /* Fields that should be sorted ordinally */
  private static final String[] ordinalSortFields = {"status", "type"};

  private final ElasticSearchMappingConfig elasticSearchMappingConfig;
  private final Client client;
  private final String indexTypeName;
  protected final ObjectMapper objectMapper;
  private final IndexConductor indexConductor;
  private final Function<T, String> keyMapper;
  private final Class<T> valueType;
  private final Map<String, String> propertyToSort;

  /**
   * Instantiate a search service.
   *
   * @param elasticSearchMappingConfig {@link ElasticSearchMappingConfig} to use
   * @param client                     The ElasticSearch client
   * @param indexTypeName              Type name in index
   * @param indexConductor             An index conductor for managing/tracking the index
   *                                   state
   * @param keyMapper                  Lambda from element to its key
   * @param valueType                  The element's class type for JSON parsing
   */
  protected GenericSearchService(
    ElasticSearchMappingConfig elasticSearchMappingConfig,
    Client client,
    String indexTypeName,
    IndexConductor indexConductor,
    Function<T, String> keyMapper,
    Class<T> valueType) {
    this.elasticSearchMappingConfig = elasticSearchMappingConfig;
    this.client = client;
    this.indexTypeName = indexTypeName;
    this.objectMapper = new ObjectMapper();
    this.indexConductor = indexConductor;
    this.keyMapper = keyMapper;
    this.valueType = valueType;
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    propertyToSort = createPropertyToSort();
  }

  public void initIndex(boolean checkVersion) {
    String currentIndexName = getCurrentIndexName();

    if (checkVersion && currentIndexName != null && !elasticSearchMappingConfig.areMappingsUpToDate()) {
      logger.debug("Index {} is outdated, deleting it", currentIndexName);

      deleteIndex(indexConductor.getIndexAliasName());
      currentIndexName = null;
    }

    if (currentIndexName == null) {
      logger.debug("No current index -> create one");

      indexConductor.generateNewIndexName();
      initializeIndex(indexConductor.getNewIndexName());
      addAlias(indexConductor.getNewIndexName(), indexConductor.getIndexAliasName());
      indexConductor.commitNewIndex();
    } else {
      logger.debug("Current index name for {} is {} ({})", indexConductor.getIndexAliasName(), currentIndexName, this);
      indexConductor.setCurrentIndexName(currentIndexName);
    }
  }

  /* Setup the lookup map for property's sort suffix: */
  private Map<String, String> createPropertyToSort() {
    Map<String, String> map = new HashMap<>();
    for (String field : alphaSortFields) {
      map.put(field, ALPHASORT);
    }
    for (String field : ordinalSortFields) {
      map.put(field, ORDINAL);
    }
    return map;
  }

  /**
   * Insert given JSON to search index using given type.
   *
   * @param indexedObject Data added to search index.
   */
  public void insert(T indexedObject) {
    insertInto(indexConductor.getIndexAliasName(), indexedObject);
    if (indexConductor.isSyncActive()) {
      insertInto(indexConductor.getNewIndexName(), indexedObject);
    }
  }

  /* Insert into given index */
  private void insertInto(String indexName, T indexedObject) {
    try {
      byte[] json = objectMapper.writeValueAsBytes(indexedObject);
      String id = keyMapper.apply(indexedObject);
      logger.debug("Inserting new object to search index {}: {}", indexName, objectMapper.writeValueAsString(indexedObject));
      IndexResponse response =
        client.prepareIndex(indexName, indexTypeName, id).setSource(json, XContentType.JSON).get();
      if (response.status() != RestStatus.CREATED) {
        throw new SearchException("Unable to insert record to " + indexName + " with id " + id);
      }
    } catch (JsonProcessingException e) {
      throw new SearchException(e);
    }
  }

  /**
   * Bulk insert objects to search index.
   *
   * @param objectsToInsert List of objects that will be inserted to search
   *                        index as JSON.
   */
  public void bulkInsert(List<T> objectsToInsert) {
    bulkInsertInto(indexConductor.getIndexAliasName(), objectsToInsert);
    if (indexConductor.isSyncActive()) {
      bulkInsertInto(indexConductor.getNewIndexName(), objectsToInsert);
    }
  }

  /* Bulk insert into given index */
  private void bulkInsertInto(String indexName, List<T> objectsToInsert) {
    List<DocWriteRequest<?>> indexRequests =
      objectsToInsert.stream().map(entry -> createRequestInto(indexName, keyMapper.apply(entry), entry))
        .collect(Collectors.toList());

    executeBulk(indexRequests, null);
  }

  /**
   * Bulk update of the search index.
   *
   * @param objectsToUpdate List of objects that will be updated to search index
   *                        as JSON.
   */
  public void bulkUpdate(List<T> objectsToUpdate) {
    bulkUpdate(objectsToUpdate, false);
  }

  public void bulkUpdate(List<T> objectsToUpdate, Boolean waitRefresh) {
    RefreshPolicy refreshPolicy = Boolean.TRUE.equals(waitRefresh) ? RefreshPolicy.WAIT_UNTIL : null;
    bulkUpdateInto(indexConductor.getIndexAliasName(), objectsToUpdate, refreshPolicy);
    if (indexConductor.isSyncActive()) {
      bulkUpdateInto(indexConductor.getNewIndexName(), objectsToUpdate, refreshPolicy);
    }
  }

  /*
   * Given a property name (e.g., "userName"), return a suitable sort fied for
   * it (e.g., "userName.alphasort")
   */
  private String getSortFieldForProperty(String propertyName) {
    return propertyName + propertyToSort.getOrDefault(propertyName, "");
  }

  private void bulkUpdateInto(String indexName, List<T> objectsToUpdate, RefreshPolicy refreshPolicy) {
    List<DocWriteRequest<?>> updateRequests =
      objectsToUpdate.stream().map(entry -> updateRequestInto(indexName, keyMapper.apply(entry), entry))
        .collect(Collectors.toList());

    executeBulk(updateRequests, refreshPolicy);
  }

  /**
   * Partial update: instead of updating whole objects, only modify a subset of
   * them.
   *
   * @param idToPartialUpdateObj Map where key is the key of the object to
   *                             partially update and value is an object (or map) containing the
   *                             fields to modify and their new values.
   */
  public void partialUpdate(Map<Integer, Object> idToPartialUpdateObj, Boolean waitRefresh) {
    partialUpdateInto(indexConductor.getIndexAliasName(), idToPartialUpdateObj, waitRefresh);
    if (indexConductor.isSyncActive()) {
      partialUpdateInto(indexConductor.getNewIndexName(), idToPartialUpdateObj, waitRefresh);
    }
  }

  public void partialUpdateInto(String indexName, Map<Integer, Object> idToPartialUpdateObj, Boolean waitRefresh) {
    List<DocWriteRequest<?>> updateRequests = idToPartialUpdateObj.entrySet().stream()
      .map(entry -> updateRequestInto(indexName, entry.getKey().toString(), entry.getValue()))
      .collect(Collectors.toList());
    RefreshPolicy refreshPolicy = BooleanUtils.isTrue(waitRefresh) ? RefreshPolicy.WAIT_UNTIL : null;
    executeBulk(updateRequests, refreshPolicy);
  }

  /**
   * Update customers and their contacts on an application
   *
   * @param applicationId       application whose customers and contacts to update
   * @param customersByRoleType new customer and contact data mapped by the role of the customer
   */
  public void updateCustomersWithContacts(Integer applicationId, Map<CustomerRoleType, CustomerWithContactsES> customersByRoleType) {
    Map<String, Map<String, Map<String, Object>>> customerUpdateStructure = CustomersIndexUtil.getCustomerWithContactsUpdateStructure(customersByRoleType);
    partialUpdate(Collections.singletonMap(applicationId, customerUpdateStructure), false);
  }

  /**
   * Delete object from search index.
   *
   * @param id Id to be deleted.
   */
  public void delete(String id) {
    deleteFrom(indexConductor.getIndexAliasName(), id);
    if (indexConductor.isSyncActive()) {
      deleteFrom(indexConductor.getNewIndexName(), id);
    }
  }

  private void deleteFrom(String indexName, String id) {
    DeleteResponse response = client.prepareDelete(indexName, indexTypeName, id).get();
    if (response == null || response.status() != RestStatus.OK) {
      throw new SearchException("Unable to delete record, id = " + id);
    }
  }

  /**
   * Search index with the given query parameters.
   * <p>
   * Supports also partial searching. Note that partial search requires special
   * ElasticSearch mapping for the field (see
   * {{@link ElasticSearchMappingConfig}}.
   *
   * @param queryParameters Query parameters.
   * @param pageRequest     Page request. Can be null, in which case the default
   *                        request is assumed.
   * @return A page of matching application IDs. Results are ordered as
   * specified by the query parameters.
   */
  public Page<Integer> findByField(Q queryParameters, Pageable pageRequest, Boolean matchAny) {
    if (pageRequest == null) {
      pageRequest = DEFAULT_PAGEREQUEST;
    }
    SearchRequestBuilder srBuilder = buildSearchRequest(queryParameters, pageRequest, matchAny);
    return fetchResponse(pageRequest, srBuilder);
  }

  protected Page<Integer> fetchResponse(Pageable pageRequest, SearchRequestBuilder srBuilder) {
    try {
      SearchResponse response = srBuilder.setFetchSource("id", "").execute().actionGet();
      long totalHits = Optional.ofNullable(response).map(r -> r.getHits().getTotalHits()).orElse(0L);
      List<Integer> results = (totalHits == 0) ? Collections.emptyList() : iterateIntSearchResponse(response);
      return new PageImpl<>(results, pageRequest, totalHits);
    } catch (IOException e) {
      throw new SearchException(e);
    }
  }

  public SearchRequestBuilder buildSearchRequest(Q queryParameters, Pageable pageRequest,
                                                 Boolean matchAny) {
    boolean isScoringQuery = isScoringQuery(queryParameters);
    BoolQueryBuilder qb = QueryBuilders.boolQuery();
    QueryParameter active = queryParameters.remove("active");
    handleActive(qb, active);
    addQueryParameters(queryParameters, matchAny, qb);
    BoolQueryBuilder withAdditionalParameters = addAdditionalQueryParameters(qb, queryParameters);
    SearchRequestBuilder srBuilder = prepareSearch(pageRequest, withAdditionalParameters);
    addSearchOrder(pageRequest, srBuilder, isScoringQuery);

    logger.debug("Searching index {} with the following query:\n {}", indexConductor.getIndexAliasName(),
      srBuilder);
    return srBuilder;
  }

  protected boolean isScoringQuery(Q queryParameters) {
    return queryParameters.getQueryParameters().stream().anyMatch(queryParameter -> queryParameter.getBoost() != null);
  }

  protected void addQueryParameters(QueryParameters queryParameters, Boolean matchAny, BoolQueryBuilder qb) {
    List<QueryParameter> parameters = queryParameters.getQueryParameters().stream().filter(QueryParameter::hasValue)
      .collect(Collectors.toList());
    for (QueryParameter param : parameters) {
      if (Boolean.TRUE.equals(matchAny)) {
        qb.should(createQueryBuilder(param));
      } else {
        qb.must(createQueryBuilder(param));
      }
    }
  }

  protected SearchRequestBuilder prepareSearch(Pageable pageRequest, QueryBuilder qb) {
    SearchRequestBuilder srBuilder = client.prepareSearch(indexConductor.getIndexAliasName())
      .setFrom((int) pageRequest.getOffset()).setSize(pageRequest.getPageSize())
      .setTypes(indexTypeName).setQuery(qb);
    return addFieldFilter(srBuilder);
  }

  protected SearchRequestBuilder addFieldFilter(SearchRequestBuilder srBuilder) {
    return srBuilder;
  }

  protected void addSearchOrder(Pageable pageRequest, SearchRequestBuilder srBuilder, boolean isScoringQuery) {
    // only add sorting by score when it is required because scoring
    // is not trivial and can produce unexpected order in results
    if (isScoringQuery) {
      srBuilder.addSort(SortBuilders.scoreSort());
    }

    Optional.ofNullable(pageRequest.getSort()).ifPresent(s -> s.forEach(o -> {
      SortBuilder<?> sb = SortBuilders.fieldSort(getSortFieldForProperty(o.getProperty()));
      if (o.isAscending()) {
        sb.order(SortOrder.ASC);
      } else {
        sb.order(SortOrder.DESC);
      }
      srBuilder.addSort(sb);
    }));
  }

  protected BoolQueryBuilder addAdditionalQueryParameters(BoolQueryBuilder qb, Q queryParameters) {
    return qb;
  }

  protected BoolQueryBuilder should(QueryBuilder left, QueryBuilder right) {
    BoolQueryBuilder shouldBuilder = QueryBuilders.boolQuery();
    shouldBuilder.should(left);
    shouldBuilder.should(right);
    return shouldBuilder;
  }

  protected void handleActive(BoolQueryBuilder qb, QueryParameter active) {
    Optional.ofNullable(active)
      .map(a -> QueryBuilders.termQuery(a.getFieldName(), a.getFieldValue()))
      .ifPresent(qb::filter);
  }

  public Page<Integer> findByField(Q queryParameters, Pageable pageRequest) {
    return findByField(queryParameters, pageRequest, false);
  }

  /**
   * Prepare for sync: create a new index and mark sync active
   */
  public void prepareSync() {
    if (indexConductor.tryStartSync()) {
      try {
        indexConductor.generateNewIndexName();
        initializeIndex(indexConductor.getNewIndexName());
        indexConductor.setSyncActive();
      } catch (Exception e) {
        indexConductor.setSyncPassive();
        throw e;
      }
    }
  }

  /**
   * Insert objects to temporary index for syncing them to ElasticSearch.
   *
   * @param objectsToSync list of objects to sync into temporary index.
   */
  public void syncData(List<T> objectsToSync) {
    if (indexConductor.isSyncActive()) {
      bulkInsertInto(indexConductor.getNewIndexName(), objectsToSync);
    }
  }

  /**
   * End sync operation: Update alias and delete old index
   */
  public void endSync() {
    if (indexConductor.tryDeactivateSync()) {
      try {
        updateAlias(indexConductor.getCurrentIndexName(), indexConductor.getNewIndexName(), indexConductor.getIndexAliasName());
        final String oldIndex = indexConductor.getCurrentIndexName();
        indexConductor.commitNewIndex();
        deleteIndex(oldIndex);
        indexConductor.setSyncPassive();
      } catch (Exception e) {
        indexConductor.setSyncActive();
        throw e;
      }
    }
  }

  /**
   * Cancel sync: delete the temporary index and go to "sync not active" state
   */
  public void cancelSync() {
    if (indexConductor.tryDeactivateSync()) {
      try {
        deleteIndex(indexConductor.getNewIndexName());
      } finally {
        indexConductor.setSyncPassive();
      }
    }
  }

  private String getCurrentIndexName() {

    ImmutableOpenMap<String, List<AliasMetaData>> aliases =
      client.admin().indices().prepareGetAliases(indexConductor.getIndexAliasName()).get().getAliases();

    if (aliases.isEmpty()) {
      return null;
    } else if (aliases.size() > 1) {
      logger.error("Index aliases are messed up for index {}", indexConductor.getIndexAliasName());
    }
    return aliases.keysIt().next();
  }

  private void addAlias(String indexName, String alias) {
    logger.debug("Add alias {} for index {}", alias, indexName);
    client.admin().indices().prepareAliases().addAlias(indexName, alias).execute().actionGet();
  }

  private void updateAlias(String oldIndexName, String newIndexName, String alias) {
    logger.debug("Update alias '{}' {}->{}", alias, oldIndexName, newIndexName);
    client.admin().indices().prepareAliases().removeAlias(oldIndexName, alias).addAlias(newIndexName, alias).execute().actionGet();
  }

  private void initializeIndex(String indexName) {
    logger.debug("initializeIndex {}", indexName);
    elasticSearchMappingConfig.initializeIndex(indexName);
  }

  /**
   * Finds an object from ElasticSearch with given id.
   *
   * @param id Id of the searched object.
   * @return Found value.
   */
  public Optional<T> findObjectById(String id) {
    QueryBuilder qb = QueryBuilders.matchQuery("_id", id);
    SearchRequestBuilder srBuilder = client.prepareSearch(indexConductor.getIndexAliasName()).setTypes(indexTypeName)
      .setQuery(qb);
    logger.debug("Finding object with the following query:\n {}", srBuilder);
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
  public void deleteIndex() {
    indexConductor.generateNewIndexName();
    initializeIndex(indexConductor.getNewIndexName());
    updateAlias(indexConductor.getCurrentIndexName(), indexConductor.getCurrentIndexName(), indexConductor.getIndexAliasName());
    final String oldIndex = indexConductor.getCurrentIndexName();
    indexConductor.commitNewIndex();
    deleteIndex(oldIndex);
  }

  private void deleteIndex(String indexName) {
    logger.debug("deleteIndex {}", indexName);
    client.admin().indices().delete(new DeleteIndexRequest(indexName)).actionGet();
  }

  /**
   * Force index refresh. Use for testing only.
   */
  public void refreshIndex() {
    client.admin().indices().prepareRefresh(indexConductor.getIndexAliasName()).execute().actionGet();
  }

  private UpdateRequest updateRequestInto(String indexName, String id, Object indexedObject) {
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

  private IndexRequest createRequestInto(String indexName, String id, T indexedObject) {
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
    } else if (queryParameter.getBoost() != null) {
      return QueryBuilders.constantScoreQuery(QueryBuilders.matchQuery(
          queryParameter.getFieldName(), queryParameter.getFieldValue()).operator(Operator.AND))
        .boost(queryParameter.getBoost());
    } else if (queryParameter.getFieldValue() != null) {
      MatchQueryBuilder builder = QueryBuilders.matchQuery(
        queryParameter.getFieldName(), queryParameter.getFieldValue()).operator(Operator.AND);
      if (!"registryKey".equals(queryParameter.getFieldName())) {
        builder.fuzziness(Fuzziness.AUTO);
      }
      return builder;
    } else if (queryParameter.getStartDateValue() != null || queryParameter.getEndDateValue() != null) {
      ZonedDateTime startDate = queryParameter.getStartDateValue() != null ? queryParameter.getStartDateValue() : RecurringApplication.BEGINNING_1972_DATE;
      ZonedDateTime endDate = queryParameter.getEndDateValue() != null ? queryParameter.getEndDateValue() : RecurringApplication.MAX_END_TIME;
      return QueryBuilders.rangeQuery(queryParameter.getFieldName())
        .gte(startDate.toInstant().toEpochMilli())
        .lte(endDate.toInstant().toEpochMilli());
    } else if (queryParameter.getFieldMultiValue() != null) {
      BoolQueryBuilder qb = QueryBuilders.boolQuery();
      for (String searchTerm : queryParameter.getFieldMultiValue()) {
        qb = qb.should(QueryBuilders.matchQuery(queryParameter.getFieldName(), searchTerm));
      }
      return qb;
    } else {
      throw new UnsupportedOperationException("Unknown query value type: " + queryParameter.getFieldValue().getClass());
    }
  }

  /**
   * Create recurring query, with the following conditions.
   * period1: search start time <= application end time AND search end time >= application start time
   * OR
   * period2: search start time <= application end time AND search end time >= application start time
   * <p>
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

  private void executeBulk(List<DocWriteRequest<?>> requests, RefreshPolicy refreshPolicy) {
    final BulkProcessor bp = BulkProcessor.builder(client, new BulkProcessorListener(refreshPolicy))
      .setConcurrentRequests(1)           // at most 1 concurrent request
      .setBulkActions(1000)               // maximum of 1000 updates per request
      .setBulkSize(new ByteSizeValue(-1)) // no byte size limit for bulk
      .build();

    requests.forEach(bp::add);

    try {
      bp.awaitClose(10, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new SearchException(e);
    }
  }

  private class BulkProcessorListener implements BulkProcessor.Listener {

    private final RefreshPolicy refreshPolicy;

    private BulkProcessorListener(RefreshPolicy refreshPolicy) {
      this.refreshPolicy = refreshPolicy;
    }

    @Override
    public void beforeBulk(long executionId, BulkRequest request) {
      if (refreshPolicy != null) {
        request.setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
      }
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, Throwable t) {
      logger.error("Bulk operation failed", t);
    }

    @Override
    public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
      logger.debug("Bulk execution completed [{}]. Took (ms): {}. Failures: {}. Count: {}",
        executionId, response.getTookInMillis(), response.hasFailures(), response.getItems().length);
    }
  }
}
