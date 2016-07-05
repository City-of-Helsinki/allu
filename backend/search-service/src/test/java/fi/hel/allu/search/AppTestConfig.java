package fi.hel.allu.search;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.UnknownHostException;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

@Configuration
@EnableAutoConfiguration
public class AppTestConfig {
  @Bean
  public Client client() throws UnknownHostException {
    Settings settings = Settings.settingsBuilder()
        .put("http.enabled", "false")
        .put("path.home", "target/elasticsearch-data")
        .put("path.data", "target/elasticsearch-data").build();

    return nodeBuilder().local(true).settings(settings).node().client();
  }
}