package fi.hel.allu.search;

import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.network.NetworkModule;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.node.Node;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

@Configuration
@EnableAutoConfiguration
public class AppTestConfig {

  @Bean
  public Client client() throws Exception {
    EmbeddedElasticsearchServer embeddedElasticsearchServer = new EmbeddedElasticsearchServer();
    embeddedElasticsearchServer.start();
    return embeddedElasticsearchServer.getClient();
  }

  public class EmbeddedElasticsearchServer {

    // Suitable location for use with Maven
    private static final String DEFAULT_HOME_DIRECTORY = "target/elasticsearch-data";

    // The embedded ES instance
    private final Node node;

    // Setting "path.home" should point to the directory in which Elasticsearch is installed.
    private final String homeDirectory;

    /**
     * Default Constructor.
     */
    public EmbeddedElasticsearchServer() {
      this(DEFAULT_HOME_DIRECTORY);
    }

    /**
     * Explicit Constructor.
     */
    public EmbeddedElasticsearchServer(String homeDirectory) {
      this.homeDirectory = homeDirectory;
      deleteHome();

      Settings.Builder elasticsearchSettings = Settings.builder()
          .put(Node.NODE_NAME_SETTING.getKey(), "testNode")
          .put(NetworkModule.TRANSPORT_TYPE_KEY, "local")
          .put(ClusterName.CLUSTER_NAME_SETTING.getKey(), "testCluster")
          .put(Environment.PATH_HOME_SETTING.getKey(), homeDirectory)
          .put(NetworkModule.HTTP_ENABLED.getKey(), false)
          .put("discovery.zen.ping_timeout", 0); // make startup faster

      this.node = new Node(elasticsearchSettings.build());
    }

    public void start() throws Exception {
      this.node.start();
    }

    public Client getClient() {
      return node.client();
    }

    public void shutdown() throws IOException {
      node.close();
      deleteHome();
    }

    private void deleteHome() {
      try {
        Path rootPath = Paths.get(homeDirectory);
        Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
      } catch (NoSuchFileException e) {
        // ok, nothing to delete
      } catch (IOException e) {
        throw new RuntimeException("Could not delete home directory of embedded elasticsearch server", e);
      }
    }
  }
}
