package fi.hel.allu.search.service;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;

@Service
public class ProjectSearchService extends GenericSearchService<ProjectES, QueryParameters> {

  @Autowired
  public ProjectSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      Client client,
      ProjectIndexConductor projectIndexConductor) {
    super(elasticSearchMappingConfig,
          client,
          ElasticSearchMappingConfig.PROJECT_TYPE_NAME,
          projectIndexConductor,
          p -> p.getId().toString(),
          ProjectES.class);
  }
}
