package fi.hel.allu.search.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.SearchException;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.indexConductor.SupervisionTaskIndexConductor;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class SupervisionTaskSearchService extends GenericSearchService<SupervisionWorkItem, QueryParameters> {

    /**
     * Instantiate a search service.
     *
     * @param elasticSearchMappingConfig    {@link ElasticSearchMappingConfig} to use
     * @param client                        The ElasticSearch client
     * @param supervisionTaskIndexConductor An index conductor for managing/tracking the index
     *                                      state
     */
    public SupervisionTaskSearchService(ElasticSearchMappingConfig elasticSearchMappingConfig,
                                        RestHighLevelClient client,
                                        SupervisionTaskIndexConductor supervisionTaskIndexConductor) {
        super(elasticSearchMappingConfig, client, supervisionTaskIndexConductor, s -> s.getId().toString(),
              SupervisionWorkItem.class);
    }

    public Page<SupervisionWorkItem> findSupervisionTaskByField(QueryParameters queryParameters, Pageable pageRequest,
                                                                Boolean matchAny) {
        if (pageRequest == null) {
            pageRequest = DEFAULT_PAGEREQUEST;
        }
        try {
            SearchSourceBuilder srBuilder = buildSearchRequest(queryParameters, pageRequest, matchAny);
            SearchResponse response = executeSearchRequest(srBuilder);
            return createResult(pageRequest, response);
        } catch (IOException e) {
            throw new SearchException(e);
        }
    }

    protected Page<SupervisionWorkItem> createResult(Pageable pageRequest, SearchResponse response) throws IOException {
        long totalHits = Optional.ofNullable(response).map(r -> r.getHits().getTotalHits()).orElse(0L);
        List<SupervisionWorkItem> results = (totalHits == 0) ? Collections.emptyList() : iterateSearchResponse(
                response);
        return new PageImpl<>(results, pageRequest, totalHits);
    }

    public List<SupervisionWorkItem> iterateSearchResponse(SearchResponse response) throws IOException {
        List<SupervisionWorkItem> appList = new ArrayList<>();
        if (response != null) {
            for (SearchHit hit : response.getHits()) {
                SupervisionWorkItem supervisionWorkItem = objectMapper.readValue(hit.getSourceAsString(),
                                                                                 SupervisionWorkItem.class);
                appList.add(supervisionWorkItem);
            }
        }
        return appList;
    }

    public void updateApplicationStatus(Integer applicationId, StatusType statusType) {
        updateByQuery(applicationId, statusType);
    }

    public void updateOwner(User owner, List<Integer> taskIds, boolean waitRefresh) {
        Map<Integer, Object> idToTask = new HashMap<>();
        for (Integer id : taskIds) {
            idToTask.put(id, Collections.singletonMap("owner", owner));
        }
        partialUpdate(idToTask, waitRefresh);
    }

    /**
     * normal bulk that is executed in async was not optimal for ownerupdate because then change don't show on frontend
     * used for owner update calls.
     * Overrides GenericSearch executeBulk that is implemented on async.
     * @param requests
     * @param refreshPolicy
     */
    @Override
    protected void executeBulk(List<DocWriteRequest<?>> requests, WriteRequest.RefreshPolicy refreshPolicy) {
        BulkRequest requesta = new BulkRequest();
        requesta.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        requesta.add(requests);
        requesta.timeout(TimeValue.timeValueSeconds(30L));
        try {
            client.bulk(requesta, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}