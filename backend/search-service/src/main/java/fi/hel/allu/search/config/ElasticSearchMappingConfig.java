package fi.hel.allu.search.config;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
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
  public static final String CUSTOMER_TYPE_NAME = "customer";
  public static final String CONTACT_TYPE_NAME = "contact";

  private static final String ANALYZER_CASE_INSENSITIVE_SORT = "case_insensitive_sort";
  private static final String ANALYZER_AUTOCOMPLETE = "autocomplete";
  private static final String FILTER_AUTOCOMPLETE = "autocomplete_filter";

  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchMappingConfig.class);
  private Client client;

  @Autowired
  public ElasticSearchMappingConfig(Client client) {
    this.client = client;
  }

  public void initializeIndex() {
    try {
      CreateIndexRequestBuilder createIndexRequestBuilder =
          client.admin().indices().prepareCreate(APPLICATION_INDEX_NAME).setSettings(getIndexSettingsForApplication());
      createIndexRequestBuilder.addMapping("_default_", getMappingBuilderForDefaultApplicationsIndex());
      createIndexRequestBuilder.addMapping(APPLICATION_TYPE_NAME, getMappingBuilderForApplication());
      createIndexRequestBuilder.execute().actionGet();
    } catch (IndexAlreadyExistsException e) {
      logger.info("ElasticSearch mapping for index " + APPLICATION_INDEX_NAME  + " not created, because it exists already.");
    }
    try {
      CreateIndexRequestBuilder createIndexRequestBuilder =
          client.admin().indices().prepareCreate(CUSTOMER_INDEX_NAME).setSettings(getIndexSettingsForCustomer());
      createIndexRequestBuilder.addMapping("_default_", getMappingBuilderForDefaultCustomersIndex());
      createIndexRequestBuilder.execute().actionGet();
    } catch (IndexAlreadyExistsException e) {
      logger.info("ElasticSearch mapping for index " + CUSTOMER_INDEX_NAME  + " not created, because it exists already.");
    }
  }

  /**
   * Default mappings for applications index that's applicable to all types.
   *
   * @return
   */
  public XContentBuilder getMappingBuilderForDefaultApplicationsIndex() {
    try {
    XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
        .startObject()
          .startObject("_default_")
            .startObject("properties")
              .startObject("name") // alphabetical sorting for all name-properties in the index
                .field("type", "string")
                .field("fields").copyCurrentStructure(parser(alphasort()))
              .endObject()
            .endObject()
          .endObject()
        .endObject();
      logger.debug("Default applications index mapping: " + mappingBuilder.string());
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  /**
   * @return  Application specific type mappings for applications index.
   */
  public XContentBuilder getMappingBuilderForApplication() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject("properties")
              .field("applicationId").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .startObject("handler") // alphabetical sorting for handler.userName
                .startObject("properties")
                  .startObject("userName")
                    .field("type", "string")
                    .field("fields").copyCurrentStructure(parser(alphasort()))
                  .endObject()
                .endObject()
              .endObject()
              .startObject("customers") // alphabetical sorting for applicant name i.e. customers.applicant.customer.name
                .startObject("properties")
                  .startObject("applicant")
                    .startObject("properties")
                      .startObject("customer")
                        .startObject("properties")
                          .startObject("name")
                            .field("type", "string")
                            .field("fields").copyCurrentStructure(parser(alphasort()))
                          .endObject()
                        .endObject()
                      .endObject()
                    .endObject()
                  .endObject()
                .endObject()
              .endObject()
              .startObject("locations") // alphabetical sorting for locations.address
                .startObject("properties")
                  .startObject("streetAddress")
                    .field("type", "string")
                    .field("fields").copyCurrentStructure(parser(alphasort()))
                  .endObject()
                .endObject()
              .endObject()
            .endObject()
            .field("date_detection", "false")
          .endObject();
      logger.debug("Applications mapping: " + mappingBuilder.string());
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  /**
   * @return  Project specific type mappings for applications index.
   */
  public XContentBuilder getMappingBuilderForProject() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject("properties")
              .startObject("ownerName")
                .field("type", "string")
                .field("fields").copyCurrentStructure(parser(alphasort()))
              .endObject()
            .endObject()
          .endObject();
      logger.debug("Applications mapping: " + mappingBuilder.string());
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  /**
   * @return  Applications index settings.
   */
  public XContentBuilder getIndexSettingsForApplication() {
    try {
      XContentBuilder settingsBuilder = commonIndexSettings();
      logger.debug("application index settings {}", settingsBuilder.string());
      return settingsBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  /**
   * @return  Default mappings for customers index that's applicable to all types.
   */
  public XContentBuilder getMappingBuilderForDefaultCustomersIndex() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject("_default_")
              .startObject("properties")
                // alphabetical sorting with autocomplete for all name-properties in the index
                .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .endObject()
            .endObject()
          .endObject();
      logger.debug("Default customers index mapping: " + mappingBuilder.string());
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  /**
   * @return  Customers index settings.
   */
  public XContentBuilder getIndexSettingsForCustomer() {
    try {
      XContentBuilder settingsBuilder = commonIndexSettings();
      logger.debug("customer index settings {}", settingsBuilder.string());
      return settingsBuilder;
    } catch (IOException e) {
      throw new RuntimeException("Unexpected exception while creating ElasticSearch mapping builder", e);
    }
  }

  private XContentParser parser(XContentBuilder xContentBuilder) throws IOException {
    return JsonXContent.jsonXContent.createParser(xContentBuilder.string());
  }

  private XContentBuilder autocompleteSettingsFilter() throws IOException {
    XContentBuilder builder =  XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "edge_ngram")
          .field("min_gram", "1")
          .field("max_gram", "20")
        .endObject();
    return builder;
  }

  private XContentBuilder autocompleteSettingsAnalyzer() throws IOException {
    XContentBuilder builder = XContentFactory.jsonBuilder()
        .startObject()
        .field("type", "custom")
        .field("tokenizer", "standard")
        .array("filter", "lowercase", FILTER_AUTOCOMPLETE)
        .endObject();
    return builder;
  }

  /**
   * ElasticSearch analyzer settings for mapping with autocomplete and alphabetical sorting.
   */
  private XContentBuilder autocompleteWithAlphaSortingMappingAnalyzer() throws IOException {
    XContentBuilder builder =  XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "string")
          .field("analyzer", ANALYZER_AUTOCOMPLETE)
          .field("search_analyzer", "standard")
          .field("fields").copyCurrentStructure(parser(alphasort()))
        .endObject();
    return builder;
  }

  private XContentBuilder caseInsensitiveSortAnalyzer() throws IOException {
    XContentBuilder builder =  XContentFactory.jsonBuilder()
        .startObject()
          .field("tokenizer", "keyword")
          .array("filter", "lowercase")
        .endObject();
    return builder;
  }

  private XContentBuilder alphasort() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .field("alphasort")
            .startObject()
              .field("type", "string")
              .field("analyzer", ANALYZER_CASE_INSENSITIVE_SORT)
            .endObject()
        .endObject();
  }

  private XContentBuilder commonIndexSettings() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .startObject("analysis")
            .startObject("filter")
              .field(FILTER_AUTOCOMPLETE).copyCurrentStructure(parser(autocompleteSettingsFilter()))
            .endObject()
            .startObject("analyzer")
              .field(ANALYZER_AUTOCOMPLETE).copyCurrentStructure(parser(autocompleteSettingsAnalyzer()))
              .field(ANALYZER_CASE_INSENSITIVE_SORT).copyCurrentStructure(parser(caseInsensitiveSortAnalyzer()))
            .endObject()
          .endObject()
        .endObject();
  }
}
