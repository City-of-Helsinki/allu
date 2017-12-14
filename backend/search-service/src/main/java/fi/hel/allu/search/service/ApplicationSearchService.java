package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationSearchService extends GenericSearchService {

  @Autowired
  public ApplicationSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig, Client client) {
    super(elasticSearchMappingConfig, client, ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICATION_TEMP_INDEX_NAME, ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
  }

}
