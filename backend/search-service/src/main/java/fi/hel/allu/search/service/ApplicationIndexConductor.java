package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;

import org.springframework.stereotype.Component;

/**
 * Conductor component for the application index.
 */
@Component
public class ApplicationIndexConductor extends IndexConductor {

  public ApplicationIndexConductor() {
    super(ElasticSearchMappingConfig.APPLICATION_INDEX_NAME, ElasticSearchMappingConfig.APPLICATION_TEMP_INDEX_NAME);
  }

}
