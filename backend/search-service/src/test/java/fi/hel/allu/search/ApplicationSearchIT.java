package fi.hel.allu.search;


import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import static fi.hel.allu.search.ApplicationSearchTest.createApplicationTypeData;
import static fi.hel.allu.search.ApplicationSearchTest.createUser;

@ExtendWith(SpringExtension.class)
@Testcontainers
class ApplicationSearchIT {
	private static final String CLUSTER_NAME = "allu-cluster";
	private static final String NODE_NAME = "allu-node";
	private static final String ELASTIC_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:5.6.0";
	@Container
	private static final ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE)
			.withExposedPorts(9300, 9200)
			.withEnv("xpack.security" + ".enabled", "false")
			.withEnv("network.host", "_site_")
			.withEnv("network" + ".publish_host", "_local_")
			.withEnv("node.name", NODE_NAME)
			.withEnv("cluster.name", CLUSTER_NAME);
	private Client client;

	private ApplicationSearchService applicationSearchService;


	@BeforeEach
	void SetUp() throws UnknownHostException {
		TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(container.getHost()),
				container.getMappedPort(9300));
		Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
		client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
		ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
		applicationSearchService = new ApplicationSearchService(elasticSearchMappingConfig, client,
				new ApplicationIndexConductor());
	}

	@Test
	void isRunning() {
		Assertions.assertTrue(container.isRunning());
	}

	@Test
	void correctSettings() {
		ClusterHealthResponse healths = client.admin().cluster().prepareHealth().get();
		String clusterName = healths.getClusterName();
		Assertions.assertEquals(CLUSTER_NAME, clusterName);
	}

	@Test
	void insertApplication() {
		ApplicationES applicationES = createApplication();
		applicationES.setName("testi");
		applicationSearchService.insert(applicationES);

		List<Integer> appList = getOneQueryResult(SearchTestUtil.createApplicationQueryParameters("name", "testi"));
		Assertions.assertNotNull(appList);
		Assertions.assertEquals(1, appList.size());
		applicationSearchService.delete("1");
	}

	@Test
	void searchApplicationExactParameter() {

		ApplicationES applicationES = createApplication();
		String parameter = "Olympiastadioninaukio";
		applicationES.setName(parameter);
		applicationES.setLocations(Collections.singletonList(createLocation()));
		applicationSearchService.insert(applicationES);
		ApplicationQueryParameters queryParameters = SearchTestUtil.createApplicationQueryParameters("name",
				parameter);
		List<Integer> appList = getOneQueryResult(queryParameters);
		Assertions.assertNotNull(appList);
		Assertions.assertEquals(1, appList.size());
		applicationSearchService.delete("1");
	}

	@Test
	void searchApplicationWitPartlyParameter() {
		ApplicationES applicationES = createApplication();
		applicationES.setName("Olympiastadioninaukio");
		applicationES.setLocations(Collections.singletonList(createLocation()));

		applicationSearchService.insert(applicationES);

		ApplicationQueryParameters queryParameters = SearchTestUtil.createApplicationQueryParameters("name",
				"Olympiastadioninauki");
		List<Integer> appList = getOneQueryResult(queryParameters);
		Assertions.assertNotNull(appList);
		Assertions.assertEquals(1, appList.size());
		applicationSearchService.delete("1");
	}


	private List<Integer> getOneQueryResult(ApplicationQueryParameters params) {
		applicationSearchService.refreshIndex();
		return applicationSearchService.findByField(params, null).getContent();
	}

	private ApplicationES createApplication() {
		ApplicationES applicationES = new ApplicationES();
		applicationES.setType(new ApplicationTypeES(ApplicationType.EVENT));
		applicationES.setId(1);
		applicationES.setOwner(createUser());
		applicationES.setStatus(new StatusTypeES(StatusType.PENDING));
		applicationES.setApplicationTypeData(createApplicationTypeData());
		return applicationES;
	}

	private LocationES createLocation() {
		LocationES locationES = new LocationES();
		locationES.setLocationKey(1);
		locationES.setStreetAddress("Olympiastadioninaukio");
		locationES.setAddress("Olympiastadioninaukio");
		locationES.setPostalCode("30973");
		locationES.setCity("Turku");
		locationES.setCityDistrictId(1);
		return locationES;
	}


}
