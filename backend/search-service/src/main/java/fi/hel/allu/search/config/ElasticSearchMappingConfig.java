package fi.hel.allu.search.config;

import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.search.SearchModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * Component for building ElasticSearch mapping configuration i.e. the "schema".
 */
@Component
public class ElasticSearchMappingConfig {

  public static final String APPLICATION_INDEX_ALIAS = "applications";
  public static final String CUSTOMER_INDEX_ALIAS = "customers";

  public static final String APPLICATION_TYPE_NAME = "application";
  public static final String PROJECT_TYPE_NAME = "project";
  public static final String CUSTOMER_TYPE_NAME = "customer";
  public static final String CONTACT_TYPE_NAME = "contact";

  private static final String ANALYZER_CASE_INSENSITIVE_SORT = "case_insensitive_sort";
  private static final String ANALYZER_AUTOCOMPLETE = "autocomplete";
  private static final String ANALYZER_AUTOCOMPLETE_KEYWORD = "autocomplete_keyword";
  private static final String FILTER_AUTOCOMPLETE = "autocomplete_filter";

  // Note! Change this version number if you edit mappings. Then changes will be updated to elastic on next startup.
  private static final String MAPPINGS_VERSION_NUMBER = "14";

  private static final String VERSION_INDEX_NAME = "versions";
  private static final String VERSION_TYPE_NAME = "version";
  private static final String VERSION_NUMBER_KEY = "versionNumber";
  private static final String VERSION_NUMBER_ID = "1";

  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchMappingConfig.class);
  private final Client client;

  @Autowired
  public ElasticSearchMappingConfig(Client client) {
    this.client = client;
  }

  public boolean areMappingsUpToDate() {
    try {
      final GetResponse response = client.prepareGet(VERSION_INDEX_NAME, VERSION_TYPE_NAME, VERSION_NUMBER_ID).get();
      if (response.isExists()) {
        final Object version = response.getSource().get(VERSION_NUMBER_KEY);
        if (version != null) {
          return MAPPINGS_VERSION_NUMBER.equals(version);
        }
      }
    } catch (IndexNotFoundException e) {
      // => not up-to-date
    }
    return false;
  }

  /**
   * Initialize single ElasticSearch index
   *
   * @param indexName the name of the index. Must be a known name.
   */
  public void initializeIndex(String indexName) {
    try {
      if (indexName.startsWith(APPLICATION_INDEX_ALIAS)) {
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName)
            .setSettings(getIndexSettingsForApplication());
        createIndexRequestBuilder.addMapping("_default_", getMappingBuilderForDefaultApplicationsIndex());
        createIndexRequestBuilder.addMapping(APPLICATION_TYPE_NAME, getMappingBuilderForApplication());
        createIndexRequestBuilder.addMapping(PROJECT_TYPE_NAME, getMappingBuilderForProject());
        createIndexRequestBuilder.execute().actionGet();
      } else if (indexName.startsWith(CUSTOMER_INDEX_ALIAS)) {
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName)
            .setSettings(getIndexSettingsForCustomer());
        createIndexRequestBuilder.addMapping("_default_", getMappingBuilderForDefaultCustomersIndex());
        createIndexRequestBuilder.addMapping(CUSTOMER_TYPE_NAME, getMappingBuilderForCustomer());
        createIndexRequestBuilder.execute().actionGet();
      } else {
        logger.error("Unknown ElasticSearch index name " + indexName);
        throw new IllegalArgumentException("Unknown ElasticSearch index name " + indexName);
      }
    } catch (ResourceAlreadyExistsException e) {
      logger.info("ElasticSearch mapping for index " + indexName + " not created, because it exists already.");
    }
  }

  public void updateMappingsVersionToIndex() {
    try {
      final IndexRequestBuilder indexRequestBuilder = client.prepareIndex(VERSION_INDEX_NAME, VERSION_TYPE_NAME, VERSION_NUMBER_ID);
      final XContentBuilder contentBuilder = jsonBuilder().startObject().prettyPrint();
      contentBuilder.field(VERSION_NUMBER_KEY, MAPPINGS_VERSION_NUMBER);
      contentBuilder.endObject();
      indexRequestBuilder.setSource(contentBuilder);
      indexRequestBuilder .execute();
    } catch (IOException e) {
      throw new RuntimeException("Unable to write version number to elasticsearch");
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
              .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .startObject("owner") // alphabetical sorting for owner.userName
                .startObject("properties")
                  .startObject("userName")
                    .field("type", "text")
                    .field("fields").copyCurrentStructure(parser(alphasort()))
                  .endObject()
                  .startObject("realName")
                    .field("type", "text")
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
                          .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
                        .endObject()
                      .endObject()
                      .startObject("contacts")
                        .startObject("properties")
                          .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
                        .endObject()
                      .endObject()

                    .endObject()
                  .endObject()
                .endObject()
              .endObject()
              .startObject("locations") // alphabetical sorting for locations.address
                .startObject("properties")
                  .field("address").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
                  .startObject("streetAddress")
                    .field("type", "text")
                    .field("fields").copyCurrentStructure(parser(alphasort()))
                  .endObject()
                  .startObject("searchGeometry")
                    .field("type", "geo_shape")
                    .field("tree", "quadtree")
                    .field("precision", "1m")
                  .endObject()
                .endObject()
              .endObject()
              .startObject("project") // alphabetical sorting for project.identifier
                .startObject("properties")
                  .startObject("identifier")
                    .field("type", "text")
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
              .field("identifier").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("ownerName").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("creator").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
            .endObject()
          .endObject();
      logger.debug("Project mapping: " + mappingBuilder.string());
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
   * @return  Customer specific type mappings for customer index.
   */
  public XContentBuilder getMappingBuilderForCustomer() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject("properties")
              .field("registryKey").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzerAndKeywordSearchAnalyzer()))
            .endObject()
          .endObject();
      logger.debug("Customers mapping: " + mappingBuilder.string());
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
    SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
    return JsonXContent.jsonXContent.createParser(new NamedXContentRegistry(searchModule.getNamedXContents()), xContentBuilder.string());
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

  private XContentBuilder autocompleteSettingsAnalyzerWithKeywordTokenizer() throws IOException {
    XContentBuilder builder =  XContentFactory.jsonBuilder()
      .startObject()
        .field("type", "custom")
        .field("tokenizer", "keyword")
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
          .field("type", "text")
          .field("analyzer", ANALYZER_AUTOCOMPLETE)
          .field("search_analyzer", "standard")
          .field("fields").copyCurrentStructure(parser(alphasort()))
        .endObject();
    return builder;
  }

  private XContentBuilder autocompleteWithAlphaSortingMappingAnalyzerAndKeywordSearchAnalyzer() throws IOException {
    XContentBuilder builder =  XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "text")
          .field("analyzer", ANALYZER_AUTOCOMPLETE_KEYWORD)
          .field("search_analyzer", "keyword")
          .field("fields").copyCurrentStructure(parser(alphasort()))
        .endObject();
    return builder;
  }

  private XContentBuilder caseInsensitiveSortAnalyzer() throws IOException {
    XContentBuilder builder =  XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "custom")
          .array("char_filter")
          .array("filter", "lowercase", "asciifolding")
        .endObject();
    return builder;
  }

  private XContentBuilder alphasort() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .field("alphasort")
            .startObject()
              .field("type", "keyword")
              .field("normalizer", ANALYZER_CASE_INSENSITIVE_SORT)
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
            .endObject()
            .startObject("analyzer")
              .field(ANALYZER_AUTOCOMPLETE_KEYWORD).copyCurrentStructure(parser(autocompleteSettingsAnalyzerWithKeywordTokenizer()))
            .endObject()
            .startObject("normalizer")
              .field(ANALYZER_CASE_INSENSITIVE_SORT).copyCurrentStructure(parser(caseInsensitiveSortAnalyzer()))
            .endObject()
          .endObject()
        .endObject();
  }
}
