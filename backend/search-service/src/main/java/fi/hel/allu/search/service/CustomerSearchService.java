package fi.hel.allu.search.service;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.indexConductor.CustomerIndexConductor;
import fi.hel.allu.search.util.Constants;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerSearchService extends GenericSearchService<CustomerES, QueryParameters> {

  private static final Logger logger = LoggerFactory.getLogger(CustomerSearchService.class);

  @Autowired
  public CustomerSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      RestHighLevelClient client,
      CustomerIndexConductor customerIndexConductor) {
    super(elasticSearchMappingConfig,
        client,
        customerIndexConductor,
        c -> c.getId().toString(),
        CustomerES.class);
  }

  /**
   * Search customers with given type and query parameters.
   */
  public Page<Integer> findByTypeAndField(CustomerType type, QueryParameters queryParameters, Pageable pageRequest,
      Boolean matchAny) {
    if (pageRequest == null) {
      pageRequest = DEFAULT_PAGEREQUEST;
    }

    SearchSourceBuilder srBuilder = buildSearchRequest(type, queryParameters, pageRequest, matchAny);
    return fetchResponse(pageRequest, srBuilder);
  }

  private SearchSourceBuilder buildSearchRequest(CustomerType type, QueryParameters queryParameters,
      Pageable pageRequest, Boolean matchAny) {
    boolean isScoringQuery = isScoringQuery(queryParameters);

    BoolQueryBuilder qb = QueryBuilders.boolQuery();

    qb.filter(QueryBuilders.matchQuery("type", type.name()));
    QueryParameter active = queryParameters.remove("active");
    handleActive(qb, active);
    QueryParameter invoicingOnly = queryParameters.remove("invoicingOnly");
    handleInvoicingOnly(qb, invoicingOnly);

    BoolQueryBuilder fieldQb = QueryBuilders.boolQuery();
    addQueryParameters(queryParameters, matchAny, fieldQb);
    qb.must(fieldQb);

    SearchSourceBuilder srBuilder = prepareSearch(pageRequest, qb);
    addSearchOrder(pageRequest, srBuilder, isScoringQuery);
    if (logger.isDebugEnabled()) {
      logger.debug("Searching index {} with the following query:\n {}", Constants.CUSTOMER_INDEX_ALIAS,
                   srBuilder);
    }
    return srBuilder;
  }
}