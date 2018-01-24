package fi.hel.allu.search.service;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;

import org.springframework.stereotype.Component;

/**
 * Conductor component for the application index.
 */
@Component
public class CustomerIndexConductor extends IndexConductor {

  public CustomerIndexConductor() {
    super(ElasticSearchMappingConfig.CUSTOMER_INDEX_ALIAS);
  }

}
