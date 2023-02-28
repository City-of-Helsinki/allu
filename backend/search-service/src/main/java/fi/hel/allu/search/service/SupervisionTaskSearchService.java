package fi.hel.allu.search.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationQueryParameters;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.indexConductor.SupervisionTaskIndexConductor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    public Page<SupervisionWorkItem> findSupervisionTaskByField(ApplicationQueryParameters queryParameters, Pageable pageRequest, Boolean matchAny) {
        if (pageRequest == null) {
            pageRequest = DEFAULT_PAGEREQUEST;
        }
        try {
            SearchSourceBuilder srBuilder = buildSearchRequest(queryParameters, pageRequest, matchAny);
            SearchResponse response = executeSearchRequest(srBuilder);
            return createResult(pageRequest, response, queryParameters.getZoom());
        } catch (IOException e) {
            throw new SearchException(e);
        }
    }

    protected Page<SupervisionWorkItem> createResult(Pageable pageRequest, SearchResponse response, Integer zoom) throws IOException {
        long totalHits = Optional.ofNullable(response).map(r -> r.getHits().getTotalHits()).orElse(0L);
        List<SupervisionWorkItem> results = (totalHits == 0) ? Collections.emptyList() : iterateSearchResponse(response);
        return new PageImpl<>(results, pageRequest, totalHits);
    }

    private List<SupervisionWorkItem> iterateSearchResponse(SearchResponse response) throws IOException {
        List<SupervisionWorkItem> appList = new ArrayList<>();
        if (response != null) {
            for (SearchHit hit : response.getHits()) {
                SupervisionWorkItem applicationES = objectMapper.readValue(hit.getSourceAsString(), SupervisionWorkItem.class);
                appList.add(applicationES);
            }
        }
        return appList;
    }

    public void updateApplicationStatus(Integer applicationId, StatusType statusType){
        updateByQuery(applicationId, statusType);
    }
}