package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerSearchService extends GenericSearchService {

  @Autowired
  public CustomerSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig, Client client,
      CustomerIndexConductor customerIndexConductor) {
    super(elasticSearchMappingConfig, client, ElasticSearchMappingConfig.CUSTOMER_TYPE_NAME, customerIndexConductor);
  }

}
