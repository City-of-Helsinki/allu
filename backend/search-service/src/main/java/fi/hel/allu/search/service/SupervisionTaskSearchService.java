package fi.hel.allu.search.service;

import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.indexConductor.SupervisionTaskIndexConductor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Service;

@Service
public class SupervisionTaskSearchService extends GenericSearchService<SupervisionWorkItem, QueryParameters>{
    /**
     * Instantiate a search service.
     *
     * @param elasticSearchMappingConfig {@link ElasticSearchMappingConfig} to use
     * @param client                     The ElasticSearch client
     * @param supervisionTaskIndexConductor             An index conductor for managing/tracking the index
     *                                   state
     */
    protected SupervisionTaskSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig,
                                           RestHighLevelClient client,
                                           SupervisionTaskIndexConductor supervisionTaskIndexConductor) {
        super(elasticSearchMappingConfig, client, supervisionTaskIndexConductor, s -> s.getId().toString(), SupervisionWorkItem.class);
    }
}