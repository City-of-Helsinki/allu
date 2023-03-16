package fi.hel.allu.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import fi.hel.allu.common.controller.handler.ControllerExceptionHandlerConfig;
import fi.hel.allu.search.util.ClientWrapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;

@Configuration
@EnableAutoConfiguration
public class AppConfig {

    @Value("${elasticsearch.host:localhost}")
    @NotEmpty
    private String elasticsearchHost;
    @Value("${elasticsearch.port:9300}")
    @NotEmpty
    private int elasticsearchPort;


    @Bean
    public ClientWrapper HLRClient() {
        RestClient httpClient = RestClient.builder(
            new HttpHost(elasticsearchHost, elasticsearchPort)
        ).build();
        RestHighLevelClient hlrc = new RestHighLevelClientBuilder(httpClient).setApiCompatibilityMode(true).build();
        ElasticsearchTransport transport = new RestClientTransport(
            httpClient,
            new JacksonJsonpMapper()
        );
        ElasticsearchClient esClient =  new ElasticsearchClient(transport);

        return new ClientWrapper(hlrc, esClient);
    }


    @Bean
    public ControllerExceptionHandlerConfig controllerExceptionHandlerConfig() {
        ControllerExceptionHandlerConfig config = new ControllerExceptionHandlerConfig();
        config.setTranslateErrorMessages(false);
        return config;
    }
}