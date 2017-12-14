package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactSearchService extends GenericSearchService {

  @Autowired
  public ContactSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig, Client client) {
    super(elasticSearchMappingConfig, client, ElasticSearchMappingConfig.CUSTOMER_INDEX_NAME, null,
        ElasticSearchMappingConfig.CONTACT_TYPE_NAME);
  }

}
