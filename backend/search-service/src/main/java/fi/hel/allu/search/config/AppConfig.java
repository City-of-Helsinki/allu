package fi.hel.allu.search.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
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
  public Client client() throws UnknownHostException {
    Settings settings = Settings.settingsBuilder().put("cluster.name", "allu-cluster").build();
    Client client = TransportClient.builder().settings(settings).build()
        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticsearchHost),
            elasticsearchPort));
    return client;
  }
}
