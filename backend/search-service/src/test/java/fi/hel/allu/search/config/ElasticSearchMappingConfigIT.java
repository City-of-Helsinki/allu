package fi.hel.allu.search.config;

import fi.hel.allu.search.BaseIntegrationTest;
import fi.hel.allu.search.SearchTestUtil;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.MainResponse;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static fi.hel.allu.search.util.Constants.APPLICATION_INDEX_ALIAS;
import static org.junit.jupiter.api.Assertions.*;

class ElasticSearchMappingConfigIT extends BaseIntegrationTest {

    private ApplicationSearchService applicationSearchService;
    private final String REINDEX = APPLICATION_INDEX_ALIAS + "_xxfew3";

    @BeforeEach
    void SetUp() {
        createdHighRestClient();
        ElasticSearchMappingConfig elasticSearchMappingConfig =  SearchTestUtil.searchIndexSetup(clientWrapper,
                                                                                                 Arrays.asList(REINDEX));
        ApplicationIndexConductor indexConductor = new ApplicationIndexConductor();
        applicationSearchService = new ApplicationSearchService(elasticSearchMappingConfig, clientWrapper,
                                                                indexConductor);
        applicationSearchService.addAlias(REINDEX, "logging only alias name");
    }

    @Test
    void testCorrectSettings() {
        MainResponse response;

        try {
            response = restHighLevelClient.info(RequestOptions.DEFAULT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String clusterName = response.getClusterName();
        assertEquals(CLUSTER_NAME, clusterName);
    }

    @Test
    void testReindexing() throws IOException {
        Integer id = 1;
        ApplicationES applicationES = createApplication(id);
        applicationES.setName("testi");
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();

        GetRequest getRequest = new GetRequest(
            REINDEX,
            id.toString());
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        CountRequest countRequest = new CountRequest();
        countRequest.indices(APPLICATION_INDEX_ALIAS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        countRequest.source(searchSourceBuilder);

        assertTrue(restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT));
        assertEquals(1L, restHighLevelClient.count(countRequest, RequestOptions.DEFAULT).getCount());
        applicationSearchService.initIndex();
        assertFalse(restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT));
        assertEquals(1L, restHighLevelClient.count(countRequest, RequestOptions.DEFAULT).getCount());
        applicationSearchService.delete(id.toString());
    }

    @Test
    void testCheckingMappign() throws IOException {
        GetMappingsRequest request = new GetMappingsRequest();
        request.indices(APPLICATION_INDEX_ALIAS);

        GetMappingsResponse getMappingResponse = restHighLevelClient.indices().getMapping(request, RequestOptions.DEFAULT);
        System.out.println(getMappingResponse.mappings());
    }

}