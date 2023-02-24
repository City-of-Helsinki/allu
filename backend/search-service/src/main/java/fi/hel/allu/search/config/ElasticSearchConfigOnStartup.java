package fi.hel.allu.search.config;

import fi.hel.allu.search.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Basic configuration for ElasticSearch mappings done during Search Service start-up.
 */
@Component
public class ElasticSearchConfigOnStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final ApplicationSearchService applicationSearchService;
    private final ContactSearchService contactSearchService;
    private final CustomerSearchService customerSearchService;
    private final ProjectSearchService projectSearchService;
    private final SupervisionTaskSearchService superviosionTaskSearchService;

    @Autowired
    public ElasticSearchConfigOnStartup(ApplicationSearchService applicationSearchService,
                                        ContactSearchService contactSearchService,
                                        CustomerSearchService customerSearchService,
                                        ProjectSearchService projectSearchService,
                                        SupervisionTaskSearchService superviosionTaskSearchService) {
        this.applicationSearchService = applicationSearchService;
        this.contactSearchService = contactSearchService;
        this.customerSearchService = customerSearchService;
        this.projectSearchService = projectSearchService;
        this.superviosionTaskSearchService = superviosionTaskSearchService;
    }

    /**
     * Initialize indexes for search service and update mappings version to ElasticSearch on start-up.
     */
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        applicationSearchService.initIndex();
        customerSearchService.initIndex();
        contactSearchService.initIndex();
        projectSearchService.initIndex();
        superviosionTaskSearchService.initIndex();
    }
}