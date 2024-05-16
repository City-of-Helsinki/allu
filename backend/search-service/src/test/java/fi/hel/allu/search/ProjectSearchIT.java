package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.indexConductor.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.indexConductor.ProjectIndexConductor;
import fi.hel.allu.search.service.ProjectSearchService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static fi.hel.allu.search.util.Constants.APPLICATION_INDEX_ALIAS;
import static fi.hel.allu.search.util.Constants.PROJECT_INDEX_ALIAS;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ProjectSearchIT extends BaseIntegrationTest {
    private static final String projectName = "testiname";
    private ProjectSearchService projectSearchService;
    private ApplicationSearchService applicationSearchService;
    private RestHighLevelClient client;

    @BeforeEach
    public void setUp() {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create(container.getHttpHostAddress())));
        ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
        projectSearchService = new ProjectSearchService(
                elasticSearchMappingConfig,
                client,
                new ProjectIndexConductor());
        applicationSearchService = new ApplicationSearchService(
                elasticSearchMappingConfig,
                client,
                new ApplicationIndexConductor());
    }

    @Test
    void testInsertProject() {
        ProjectES projectES = createProject(1, 2);
        projectSearchService.insert(projectES);
        projectSearchService.refreshIndex();
        assertInsertion(PROJECT_INDEX_ALIAS, projectES.getId().toString());
    }

    @Test
    void testInsertApplicationAndProject() {
        ProjectES projectES = createProject(1, 2);
        ApplicationES applicationES = ApplicationSearchIT.createApplication(123);
        // insert both project and application to catch possible property type mismatches: project first and then
        // application
        projectSearchService.insert(projectES);
        applicationSearchService.insert(applicationES);
        projectSearchService.refreshIndex();
        applicationSearchService.refreshIndex();
        assertInsertion(PROJECT_INDEX_ALIAS, projectES.getId().toString());
        assertInsertion(APPLICATION_INDEX_ALIAS, applicationES.getId().toString());
    }

    @Test
    void testInsertProjectAndApplication() {
        ProjectES projectES = createProject(1, 2);
        ApplicationES applicationES = ApplicationSearchIT.createApplication(123);
        // insert both project and application to catch possible property type mismatches: : application first and
        // then project
        applicationSearchService.insert(applicationES);
        projectSearchService.insert(projectES);
        projectSearchService.refreshIndex();
        applicationSearchService.refreshIndex();
        assertInsertion(PROJECT_INDEX_ALIAS, projectES.getId().toString());
        assertInsertion(APPLICATION_INDEX_ALIAS, applicationES.getId().toString());
    }

    private void assertInsertion(String index, String id) {
        GetRequest getRequest = new GetRequest(
                index,
                "_doc",
                id);
        GetResponse getResponse = null;
        try {
            getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertTrue(getResponse.isExists());
    }

    @Test
    void testFindByField() {
        ProjectES projectES = createProject(1, 2);
        projectSearchService.insert(projectES);
        QueryParameters params = SearchTestUtil.createQueryParameters("name", projectName);
        projectSearchService.refreshIndex();
        List<Integer> appList = projectSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(1, appList.size());
    }

    private ProjectES createProject(int projectId, int projectParentId) {
        ProjectES projectES = new ProjectES();
        projectES.setId(projectId);
        projectES.setParentId(projectParentId);
        projectES.setName(projectName);
        projectES.setOwnerName("owner");
        projectES.setAdditionalInfo("Additional info");
        projectES.setCustomerReference("Customer reference");
        projectES.setStartTime(ZonedDateTime.parse("2016-07-05T06:23:04.000Z").toInstant().toEpochMilli());
        projectES.setEndTime(ZonedDateTime.parse("2016-07-06T06:23:04.000Z").toInstant().toEpochMilli());
        return projectES;
    }
}