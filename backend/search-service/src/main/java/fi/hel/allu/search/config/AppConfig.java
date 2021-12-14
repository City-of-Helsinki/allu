package fi.hel.allu.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import fi.hel.allu.common.controller.handler.ControllerExceptionHandlerConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

@Configuration
@EnableAutoConfiguration
public class AppConfig {

  @Bean
  public ElasticsearchClient client() throws UnknownHostException {
    RestClient restClient = RestClient.builder(
      new HttpHost("localhost", 9300)).build();
    // Create the transport with a Jackson mapper
    ElasticsearchTransport transport = new RestClientTransport(
      restClient, new JacksonJsonpMapper());
    // And create the API client
    ElasticsearchClient client = new ElasticsearchClient(transport);
    return client;
  }

  @Bean
  public ControllerExceptionHandlerConfig controllerExceptionHandlerConfig() {
    ControllerExceptionHandlerConfig config = new ControllerExceptionHandlerConfig();
    config.setTranslateErrorMessages(false);
    return config;
  }
}
