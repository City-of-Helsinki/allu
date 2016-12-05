package fi.hel.allu.search.config;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Component for building ElasticSearch mapping configuration i.e. the "schema".
 */
@Component
public class ElasticSearchMappingConfig {

  public static final String APPLICATION_INDEX_NAME = "allu";
  public static final String APPLICATION_TYPE_NAME = "application";
  public static final String PROJECT_TYPE_NAME = "project";

  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchMappingConfig.class);
  private Client client;

  @Autowired
  public ElasticSearchMappingConfig(Client client) {
    this.client = client;
  }

  public void initializeIndex() {
    try {
      CreateIndexRequestBuilder createIndexRequestBuilder =
          client.admin().indices().prepareCreate(APPLICATION_INDEX_NAME);
      createIndexRequestBuilder.addMapping(APPLICATION_TYPE_NAME, getMappingBuilderForApplication());
      createIndexRequestBuilder.addMapping(PROJECT_TYPE_NAME, getMappingBuilderForProject());
      createIndexRequestBuilder.execute().actionGet();
    } catch (IndexAlreadyExistsException e) {
      logger.info("ElasticSearch mapping for index " + APPLICATION_INDEX_NAME  + " not created, because it exists already.");
    }
  }

  public XContentBuilder getMappingBuilderForApplication() {
    try {
      XContentBuilder mappingBuilder = null;
      mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject("properties")
              .startObject("creationTime")
                .field("type", "date")
              .endObject()
              .startObject("startTime")
                .field("type", "date")
              .endObject()
              .startObject("endTime")
                .field("type", "date")
              .endObject()
            .endObject()
            .field("date_detection", "false")
          .endObject();
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  public XContentBuilder getMappingBuilderForProject() {
    // as long applications have "close enough" mapping, projects can use the same mapping
    return getMappingBuilderForApplication();
  }
}
