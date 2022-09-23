package fi.hel.allu.search.config;

import fi.hel.allu.common.controller.handler.ControllerExceptionHandlerConfig;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotEmpty;
import java.net.UnknownHostException;

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
    public RestHighLevelClient client() throws UnknownHostException {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost(elasticsearchHost, elasticsearchPort, "http")));


    }

    @Bean
    public ControllerExceptionHandlerConfig controllerExceptionHandlerConfig() {
        ControllerExceptionHandlerConfig config = new ControllerExceptionHandlerConfig();
        config.setTranslateErrorMessages(false);
        return config;
    }
}
