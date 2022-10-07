package fi.hel.allu.search.config;

import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.ContactSearchService;
import fi.hel.allu.search.service.CustomerSearchService;
import fi.hel.allu.search.service.ProjectSearchService;
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
  private final ApplicationSearchService applicationSearchService;
  private final ContactSearchService contactSearchService;
  private final CustomerSearchService customerSearchService;
  private final ProjectSearchService projectSearchService;

  @Autowired
  public ElasticSearchConfigOnStartup(
        ElasticSearchMappingConfig elasticSearchMappingConfig,
        ApplicationSearchService applicationSearchService,
        ContactSearchService contactSearchService,
        CustomerSearchService customerSearchService,
        ProjectSearchService projectSearchService) {
    this.elasticSearchMappingConfig = elasticSearchMappingConfig;
    this.applicationSearchService = applicationSearchService;
    this.contactSearchService = contactSearchService;
    this.customerSearchService = customerSearchService;
    this.projectSearchService = projectSearchService;
  }

  /**
   * Initialize indexes for search service and update mappings version to ElasticSearch on start-up.
   */
  @Override
  public void onApplicationEvent(final ApplicationReadyEvent event) {
    applicationSearchService.initIndex(true);
    customerSearchService.initIndex(true);
    contactSearchService.initIndex(true);
    projectSearchService.initIndex(true);
    elasticSearchMappingConfig.updateMappingsVersionToIndex();
  }
}
