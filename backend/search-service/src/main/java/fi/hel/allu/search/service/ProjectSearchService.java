package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectSearchService extends GenericSearchService {

  @Autowired
  public ProjectSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig, Client client,
      ApplicationIndexConductor applicationIndexConductor) {
    super(elasticSearchMappingConfig, client, ElasticSearchMappingConfig.PROJECT_TYPE_NAME, applicationIndexConductor);
  }
}
