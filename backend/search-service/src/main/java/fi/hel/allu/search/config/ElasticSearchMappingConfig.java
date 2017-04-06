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

  public static final String APPLICATION_INDEX_NAME = "applications";
  public static final String CUSTOMER_INDEX_NAME = "customers";

  public static final String APPLICATION_TYPE_NAME = "application";
  public static final String PROJECT_TYPE_NAME = "project";
  public static final String APPLICANT_TYPE_NAME = "applicant";
  public static final String CONTACT_TYPE_NAME = "contact";

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
    try {
      CreateIndexRequestBuilder createIndexRequestBuilder =
          client.admin().indices().prepareCreate(CUSTOMER_INDEX_NAME).setSettings(getIndexSettingsForCustomer());
      createIndexRequestBuilder.addMapping(APPLICANT_TYPE_NAME, getMappingBuilderForApplicant());
      createIndexRequestBuilder.addMapping(CONTACT_TYPE_NAME, getMappingBuilderForContact());
      createIndexRequestBuilder.execute().actionGet();
    } catch (IndexAlreadyExistsException e) {
      logger.info("ElasticSearch mapping for index " + CUSTOMER_INDEX_NAME  + " not created, because it exists already.");
    }
  }

  public XContentBuilder getMappingBuilderForApplication() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
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

  public XContentBuilder getIndexSettingsForCustomer() {
    try {
      XContentBuilder settingsBuilder = null;
      settingsBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject("analysis")
              .startObject("filter")
                .startObject("autocomplete_filter")
                  .field("type", "edge_ngram")
                  .field("min_gram", "1")
                  .field("max_gram", "20")
                .endObject()
              .endObject()
              .startObject("analyzer")
                .startObject("autocomplete")
                  .field("type", "custom")
                  .field("tokenizer", "standard")
                  .array("filter", "lowercase", "autocomplete_filter")
                .endObject()
              .endObject()
            .endObject()
          .endObject();
      logger.debug("customer index settings {}", settingsBuilder.string());
      return settingsBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  public XContentBuilder getMappingBuilderForApplicant() {
    try {
      XContentBuilder mappingBuilder = null;
      mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject("properties")
              .startObject("name")
                // autocomplete analyzer for name field of applicant
                .field("type", "string")
                .field("analyzer", "autocomplete")
                .field("search_analyzer", "standard")
              .endObject()
            .endObject()
          .endObject();
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  public XContentBuilder getMappingBuilderForContact() {
    // mapping for applicant and contact was equal at the time of writing
    return getMappingBuilderForApplicant();
  }
}
