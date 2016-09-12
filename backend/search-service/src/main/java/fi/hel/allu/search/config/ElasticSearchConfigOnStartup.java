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

  private ElasticSearchMappingConfig elasticSearchMappingConfig;

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
    elasticSearchMappingConfig.initializeIndex();
  }
}
