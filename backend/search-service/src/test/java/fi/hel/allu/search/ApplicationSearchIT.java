package fi.hel.allu.search;


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

@ExtendWith(SpringExtension.class)
@Testcontainers
class ApplicationSearchIT {
	private final String CLUSTER_NAME = "allu-cluster";
	private final String NODE_NAME = "allu-node";
	private final String ELASTIC_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:5.6.0";
	@Container
	private final ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE).withExposedPorts(9300, 9200).withEnv("xpack.security.enabled", "false").withEnv("network.host", "_site_").withEnv("network.publish_host", "_local_").withEnv("node.name", NODE_NAME).withEnv("cluster.name", CLUSTER_NAME);
	private Client client;

	private ApplicationSearchService applicationSearchService;


	@BeforeEach
	void SetUp() throws UnknownHostException {
		TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName(container.getHost()), container.getMappedPort(9300));
		Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
		client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
	}

	@Test
	void isRunning() {
		Assertions.assertTrue(container.isRunning());
	}

	@Test
	void testSettings() {
		ClusterHealthResponse healths = client.admin().cluster().prepareHealth().get();
		String clusterName = healths.getClusterName();
		Assertions.assertEquals(clusterName, CLUSTER_NAME);
	}
}
