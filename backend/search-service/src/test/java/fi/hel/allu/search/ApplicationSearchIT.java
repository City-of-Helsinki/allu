package fi.hel.allu.search;


import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.io.IOException;

@ExtendWith(SpringExtension.class)
@Testcontainers
class ApplicationSearchIT {

	@Container
	private ElasticsearchContainer container = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:5.6.0")
			.withExposedPorts(9300, 9200);

	RestClient client;

	@BeforeEach
	void SetUp(){
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "changeme"));
		 client = RestClient.builder(HttpHost.create(container.getHttpHostAddress()))
				.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
				.build();
	}


	@Test
	void isRunning() {
		Assertions.assertTrue(container.isRunning());
	}

	@Test
	void testClusterHealth() throws IOException {



		Response response = client.performRequest("GET", "/_cluster/health");
		System.out.println(response.toString());
		Assertions.assertFalse(response.toString().isEmpty());


	}
}
