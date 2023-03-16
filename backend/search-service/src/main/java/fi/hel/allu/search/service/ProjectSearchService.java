package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.util.ClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectSearchService extends GenericSearchService<ProjectES, QueryParameters> {

  @Autowired
  public ProjectSearchService(
      ElasticSearchMappingConfig elasticSearchMappingConfig,
      ClientWrapper clientWrapper,
      ProjectIndexConductor projectIndexConductor) {
    super(elasticSearchMappingConfig,
          clientWrapper,
          projectIndexConductor,
          p -> p.getId().toString(),
          ProjectES.class);
  }
}