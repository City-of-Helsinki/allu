package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ProjectES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.ProjectIndexConductor;
import fi.hel.allu.search.service.ProjectSearchService;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(SpringExtension.class)
@Testcontainers
class ProjectSearchTest {



	private static final String CLUSTER_NAME = "allu-cluster";
	private static final String NODE_NAME = "allu-node";
	private static final String ELASTIC_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:6.0.0";


	@Container
	private static final ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE)
			.withExposedPorts(9300, 9200)
			.withEnv("xpack.security" + ".enabled", "false")
			.withEnv("network.host", "_site_")
			.withEnv("network" + ".publish_host", "_local_")
			.withEnv("node.name", NODE_NAME)
			.withEnv("cluster.name", CLUSTER_NAME);
	private Client client;
	private ProjectSearchService projectSearchService;
	private ApplicationSearchService applicationSearchService;

	private static final String projectName = "testiname";

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
	}

	@Test
	void testInsertApplicationAndProject() {
		ProjectES projectES = createProject(1, 2);
		ApplicationES applicationES = ApplicationSearchTest.createApplication(123);
		// insert both project and application to catch possible property type mismatches: project first and then application
		projectSearchService.insert(projectES);
		applicationSearchService.insert(applicationES);
	}

	@Test
	void testInsertProjectAndApplication() {
		ProjectES projectES = createProject(1, 2);
		ApplicationES applicationES = ApplicationSearchTest.createApplication(123);
		// insert both project and application to catch possible property type mismatches: : application first and then project
		applicationSearchService.insert(applicationES);
		projectSearchService.insert(projectES);
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
