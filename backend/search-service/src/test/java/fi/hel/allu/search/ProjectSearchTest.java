package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.ProjectIndexConductor;
import fi.hel.allu.search.service.ProjectSearchService;

import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.List;

import static fi.hel.allu.search.util.Constants.APPLICATION_INDEX_ALIAS;
import static fi.hel.allu.search.util.Constants.PROJECT_INDEX_ALIAS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ExtendWith(SpringExtension.class)
class ProjectSearchTest extends BaseIntegrationTest {
	private ProjectSearchService projectSearchService;
	private ApplicationSearchService applicationSearchService;

	private static final String projectName = "testiname";

	private Client client;

	@BeforeEach
	public void setUp() throws UnknownHostException {
		TransportAddress transportAddress = new TransportAddress(InetAddress.getByName(container.getHost()),
																														 container.getMappedPort(9300));
		Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
		client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
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
		assertInsertion(PROJECT_INDEX_ALIAS);
	}

	@Test
	void testInsertApplicationAndProject() {
		ProjectES projectES = createProject(1, 2);
		ApplicationES applicationES = ApplicationSearchIT.createApplication(123);
		// insert both project and application to catch possible property type mismatches: project first and then application
		projectSearchService.insert(projectES);
		applicationSearchService.insert(applicationES);
		projectSearchService.refreshIndex();
		applicationSearchService.refreshIndex();
		assertInsertion(PROJECT_INDEX_ALIAS);
		assertInsertion(APPLICATION_INDEX_ALIAS);
	}

	@Test
	void testInsertProjectAndApplication() {
		ProjectES projectES = createProject(1, 2);
		ApplicationES applicationES = ApplicationSearchIT.createApplication(123);
		// insert both project and application to catch possible property type mismatches: : application first and then project
		applicationSearchService.insert(applicationES);
		projectSearchService.insert(projectES);
		projectSearchService.refreshIndex();
		applicationSearchService.refreshIndex();
		assertInsertion(PROJECT_INDEX_ALIAS);
		assertInsertion(APPLICATION_INDEX_ALIAS);
	}

	private void assertInsertion(String index){
		IndicesStatsResponse indicesStatsResponse = client.admin().indices().prepareStats(index).get();
		assertEquals(1, indicesStatsResponse.getIndices().get(index).getTotal().docs.getCount());
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
