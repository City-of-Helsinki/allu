package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.CustomerES;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerSearchService extends GenericSearchService<CustomerES> {

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

}
