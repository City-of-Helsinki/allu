package fi.hel.allu.search.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Basic configuration for ElasticSearch mappings done during Search Service start-up.
 */
@Component
public class ElasticSearchConfigOnStartup implements ApplicationListener<ApplicationReadyEvent> {

  private final ElasticSearchMappingConfig elasticSearchMappingConfig;

  @Autowired
  public ElasticSearchConfigOnStartup(ElasticSearchMappingConfig elasticSearchMappingConfig) {
    this.elasticSearchMappingConfig = elasticSearchMappingConfig;
  }

  /**
   * Initialize ElasticSearch index mapping on start-up to guarantee that ElasticSearch contains correct "schema".
   *
   * @param event
   */
  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    if (!elasticSearchMappingConfig.areMappingsUpToDate()) {
      // Mappings need to be updated -> just delete indices and they are recreated when indices are initialized.
      elasticSearchMappingConfig.deleteIndices();
    }
    elasticSearchMappingConfig.initializeIndices();
  }
}
