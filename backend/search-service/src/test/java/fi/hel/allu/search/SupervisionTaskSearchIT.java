package fi.hel.allu.search;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.domain.SupervisionTypeES;
import fi.hel.allu.model.domain.SupervisionWorkItem;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.indexConductor.SupervisionTaskIndexConductor;
import fi.hel.allu.search.service.SupervisionTaskSearchService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fi.hel.allu.search.util.Constants.SUPERVISION_TASK_INDEX;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class SupervisionTaskSearchIT extends BaseIntegrationTest {

    private RestHighLevelClient client;
    private SupervisionTaskSearchService supervisionTaskSearchService;

    @BeforeEach
    void SetUp() {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create(container.getHttpHostAddress())));
        ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
        supervisionTaskSearchService = new SupervisionTaskSearchService(elasticSearchMappingConfig, client,
                                                                        new SupervisionTaskIndexConductor());
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
    void testInsertTask() throws IOException {
        Integer id = 52;
        SupervisionWorkItem task = createTask(id);
        supervisionTaskSearchService.insert(task);
        GetRequest getRequest = new GetRequest(SUPERVISION_TASK_INDEX, "_doc", id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        assertTrue(getResponse.isExists());
        assertEquals(id.toString(), getResponse.getId());
    }

    @Test
    void removeOwner() throws IOException {
        List<Integer> taskIds = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        List<SupervisionWorkItem> tasks = createMultipleTask(taskIds);
        tasks.forEach(supervisionTaskSearchService::insert);
        supervisionTaskSearchService.updateOwner(null, taskIds, false);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("owner.id", 42));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        supervisionTaskSearchService.refreshIndex();

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        assertEquals(0, searchResponse.getHits().getTotalHits());
    }

    @Test
    void updateOwner() throws IOException {
        List<Integer> taskIds = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        User user = createSupervisionUser();
        List<SupervisionWorkItem> tasks = createMultipleTask(taskIds);
        tasks.forEach(supervisionTaskSearchService::insert);
        supervisionTaskSearchService.updateOwner(user, taskIds, false);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("owner.id", user.getId()));
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        supervisionTaskSearchService.refreshIndex();

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        assertEquals(10, searchResponse.getHits().getTotalHits());
    }

    @Test
    void deleteTask() throws IOException {
        Integer id = 52;
        SupervisionWorkItem task = createTask(id);
        supervisionTaskSearchService.insert(task);
        supervisionTaskSearchService.delete(task.getId().toString());
        GetRequest getRequest = new GetRequest(SUPERVISION_TASK_INDEX, "_doc", id.toString());
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        assertFalse(getResponse.isExists());
    }

    private List<SupervisionWorkItem> createMultipleTask(List<Integer> ids) {
        List<SupervisionWorkItem> result = new ArrayList<>();
        for (Integer id : ids) {
            result.add(createTask(id));
        }
        return result;
    }

    private SupervisionWorkItem createTask(Integer id) {
        User user = new User();
        user.setId(42);
        SupervisionWorkItem task = new SupervisionWorkItem();
        task.setId(id);
        task.setApplicationId(1000 + id);
        task.setType(new SupervisionTypeES(SupervisionTaskType.FINAL_SUPERVISION));
        task.setApplicationIdText("AL0000" + id);
        task.setApplicationStatus(StatusType.DECISION);
        task.setCreator(user);
        task.setPlannedFinishingTime(ZonedDateTime.now().plusYears(2));
        task.setAddress(new String[]{"Helsinginkatu 15", "Pasila 3"});
        task.setProjectName("Testi Projekti");
        task.setOwner(user);
        task.setCityDistrictId(9);
        return task;
    }

    private User createSupervisionUser() {
        User user = new User();
        user.setId(66);
        user.setUserName("Pasi");
        user.setActive(true);
        user.setAssignedRoles(Collections.emptyList());
        user.setCityDistrictIds(Collections.emptyList());
        user.setEmailAddress("antero@gmail.com");
        user.setAllowedApplicationTypes(Collections.emptyList());
        user.setLastLogin(ZonedDateTime.now());
        user.setIsActive(true);
        user.setPhone("749217496986");
        user.setRealName("Lucifer Morningstar");
        user.setTitle("Ylijumala");
        return user;
    }
}