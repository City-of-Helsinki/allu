package fi.hel.allu.search;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class BaseIntegrationTest {

    protected static final String CLUSTER_NAME = "allu-cluster";
    protected static final String NODE_NAME = "allu-node";
    protected static final String ELASTIC_IMAGE = "docker.elastic.co/elasticsearch/elasticsearch:6.0.0";

    @Container
    protected static final ElasticsearchContainer container = new ElasticsearchContainer(ELASTIC_IMAGE)
            .withExposedPorts(9300, 9200)
            .withEnv("xpack.security" + ".enabled", "false")
            .withEnv("network.host", "_site_")
            .withEnv("network" + ".publish_host", "_local_")
            .withEnv("node.name", NODE_NAME)
            .withEnv("cluster.name", CLUSTER_NAME);

    @Test
    void isRunning() {
        Assertions.assertTrue(container.isRunning());
    }

}