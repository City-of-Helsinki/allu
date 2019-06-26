package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ContactSearchService extends GenericSearchService<ContactES, QueryParameters> {

  @Autowired
  public ContactSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client,
      CustomerIndexConductor customerIndexConductor) {
    super(elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.CONTACT_TYPE_NAME,
        customerIndexConductor,
        c -> c.getId().toString(),
        ContactES.class);
  }
}
