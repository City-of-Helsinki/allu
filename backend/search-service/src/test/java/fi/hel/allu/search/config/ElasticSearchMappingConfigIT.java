package fi.hel.allu.search.config;

import fi.hel.allu.search.BaseIntegrationTest;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.indexConductor.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static fi.hel.allu.search.util.Constants.APPLICATION_INDEX_ALIAS;
import static org.junit.jupiter.api.Assertions.*;

class ElasticSearchMappingConfigIT extends BaseIntegrationTest {

    private RestHighLevelClient client;

    private ApplicationSearchService applicationSearchService;

    @BeforeEach
    void SetUp() {
        client = new RestHighLevelClient(
                RestClient.builder(HttpHost.create(container.getHttpHostAddress())));
        ElasticSearchMappingConfig elasticSearchMappingConfig = new ElasticSearchMappingConfig(client);
        applicationSearchService = new ApplicationSearchService(elasticSearchMappingConfig, client,
                                                                new ApplicationIndexConductor());
        applicationSearchService.initIndex();
    }

    @Test
    void testCorrectSettings() {
        MainResponse response;

        try {
            response = client.info(RequestOptions.DEFAULT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClusterName clusterName = response.getClusterName();
        assertEquals(CLUSTER_NAME, clusterName.value());
    }

    @Test
    void testReindexing() throws IOException {
        Integer id = 1;
        ApplicationES applicationES = createApplication(id);
        applicationES.setName("testi");
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();
        GetRequest getRequest = new GetRequest(
                APPLICATION_INDEX_ALIAS,
                "_doc",
                id.toString());
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        assertTrue(client.exists(getRequest, RequestOptions.DEFAULT));
        applicationSearchService.initIndex();
         getRequest = new GetRequest(
                APPLICATION_INDEX_ALIAS,
                "_doc",
                id.toString());
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        assertTrue(client.exists(getRequest, RequestOptions.DEFAULT));
        applicationSearchService.delete(id.toString());
    }

    @Test
    void testCheckingMappign() throws IOException {
        GetMappingsRequest request = new GetMappingsRequest();
        request.indices(APPLICATION_INDEX_ALIAS);

        GetMappingsResponse getMappingResponse = client.indices().getMapping(request, RequestOptions.DEFAULT);
        System.out.println(getMappingResponse.mappings());
    }

}