package fi.hel.allu.search.service;

import fi.hel.allu.search.indexConductor.ProjectIndexConductor;
import org.elasticsearch.client.RestHighLevelClient;
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
      RestHighLevelClient client,
      ProjectIndexConductor projectIndexConductor) {
    super(elasticSearchMappingConfig,
          client,
          projectIndexConductor,
          p -> p.getId().toString(),
          ProjectES.class);
  }
}