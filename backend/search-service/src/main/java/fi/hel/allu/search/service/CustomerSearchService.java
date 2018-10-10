package fi.hel.allu.search.service;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.CustomerES;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;

@Service
public class CustomerSearchService extends GenericSearchService<CustomerES, QueryParameters> {

  private static final Logger logger = LoggerFactory.getLogger(CustomerSearchService.class);

  @Autowired
  public CustomerSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client,
      CustomerIndexConductor customerIndexConductor) {
    super(elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.CUSTOMER_TYPE_NAME,
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
    SearchRequestBuilder srBuilder = buildSearchRequest(type, queryParameters, pageRequest, matchAny);
    return fetchResponse(pageRequest, srBuilder);
  }

  private SearchRequestBuilder buildSearchRequest(CustomerType type, QueryParameters queryParameters,
      Pageable pageRequest, Boolean matchAny) {
    BoolQueryBuilder qb = QueryBuilders.boolQuery();
    qb.filter(QueryBuilders.matchQuery("type", type.name()));
    BoolQueryBuilder fieldQb = QueryBuilders.boolQuery();
    addQueryParameters(queryParameters, matchAny, fieldQb);
    qb.filter(fieldQb);
    SearchRequestBuilder srBuilder = prepareSearch(pageRequest, qb);
    addSearchOrder(pageRequest, srBuilder);
    logger.debug("Searching index {} with the following query:\n {}", ElasticSearchMappingConfig.CUSTOMER_INDEX_ALIAS,
        srBuilder.toString());
    return srBuilder;

  }

}
