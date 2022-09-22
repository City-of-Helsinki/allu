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

  public static final String PROJECT_INDEX_ALIAS = "projects";
  public static final String CUSTOMER_INDEX_ALIAS = "customers";

  public static final String CONTACT_INDEX_ALIAS = "contacts";
  public static final String PROPERTIES_INDEX_ALIAS = "properties";

  public static final String APPLICATION_TYPE_NAME = "application";
  public static final String PROJECT_TYPE_NAME = "project";
  public static final String CUSTOMER_TYPE_NAME = "customer";
  public static final String CONTACT_TYPE_NAME = "contact";

  private static final String ANALYZER_CASE_INSENSITIVE_SORT = "case_insensitive_sort";
  private static final String ANALYZER_AUTOCOMPLETE = "autocomplete";
  private static final String ANALYZER_AUTOCOMPLETE_KEYWORD = "autocomplete_keyword";
  private static final String FILTER_AUTOCOMPLETE = "autocomplete_filter";
  private static final String FILTER_AUTOCOMPLETE_KEYWORD = "autocomplete_keyword_filter";

  private static final String FILTER = "filter";

  // Note! Change this version number if you edit mappings. Then changes will be updated to elastic on next startup.
  private static final String MAPPINGS_VERSION_NUMBER = "26";

  private static final String FIELDS = "fields";
  private static final String VERSION_INDEX_NAME = "versions";
  private static final String VERSION_TYPE_NAME = "version";
  private static final String VERSION_NUMBER_KEY = "versionNumber";
  private static final String VERSION_NUMBER_ID = "1";
  private static final String MAPPING_TYPE = "_default_";

  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchMappingConfig.class);
  private static final String BUILDER_ERROR = "Unexpected exception while creating ElasticSearch mapping builder";
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
        createIndexRequestBuilder.addMapping(MAPPING_TYPE, getMappingBuilderForDefaultApplicationsIndex());
        createIndexRequestBuilder.addMapping(APPLICATION_TYPE_NAME, getMappingBuilderForApplication());
        createIndexRequestBuilder.execute().actionGet();
      } else if (indexName.startsWith(CUSTOMER_INDEX_ALIAS)) {
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName)
            .setSettings(getIndexSettingsForCustomer());
        createIndexRequestBuilder.addMapping(MAPPING_TYPE, getMappingBuilderForDefaultCustomersIndex());
        createIndexRequestBuilder.addMapping(CUSTOMER_TYPE_NAME, getMappingBuilderForCustomer());
        createIndexRequestBuilder.execute().actionGet();
      } else if (indexName.startsWith(PROJECT_INDEX_ALIAS)) {
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName)
            .setSettings(getIndexSettingsForApplication());
        createIndexRequestBuilder.addMapping(MAPPING_TYPE, getMappingBuilderForDefaultApplicationsIndex());
       createIndexRequestBuilder.addMapping(PROJECT_TYPE_NAME, getMappingBuilderForProject());
        createIndexRequestBuilder.execute().actionGet();
      } else if (indexName.startsWith(CONTACT_INDEX_ALIAS)) {
        CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices().prepareCreate(indexName)
            .setSettings(getIndexSettingsForCustomer());
        createIndexRequestBuilder.addMapping(MAPPING_TYPE, getMappingBuilderForDefaultCustomersIndex());
        createIndexRequestBuilder.execute().actionGet();
        createIndexRequestBuilder.addMapping(CONTACT_TYPE_NAME, getMappingBuilderForCustomer());
      } else {
        logger.error("Unknown ElasticSearch index name {} ", indexName);
        throw new IllegalArgumentException("Unknown ElasticSearch index name " + indexName);
      }
    } catch (ResourceAlreadyExistsException e) {
      logger.info("ElasticSearch mapping for index {} not created, because it exists already.", indexName);
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
   * @return XContentBuilder
   */
  public XContentBuilder getMappingBuilderForDefaultApplicationsIndex() {
    try {
    XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
        .startObject()
          .startObject(MAPPING_TYPE)
            .startObject(PROPERTIES_INDEX_ALIAS)
            .endObject()
          .endObject()
        .endObject();
    if(logger.isDebugEnabled()) {
      logger.debug("Default applications index mapping: {}", mappingBuilder.string());
    }
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  /**
   * @return  Application specific type mappings for applications index.
   */
  public XContentBuilder getMappingBuilderForApplication() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject(PROPERTIES_INDEX_ALIAS)
              .field("applicationId").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .startObject("owner") // alphabetical sorting for owner.userName
                .startObject(PROPERTIES_INDEX_ALIAS)
                    .field("userName").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzerAndKeywordSearchAnalyzer()))
                  .startObject("realName")
                    .field("type", "text")
                    .field(FIELDS).copyCurrentStructure(parser(alphasort()))
                  .endObject()
                .endObject()
              .endObject()
              .startObject(CUSTOMER_INDEX_ALIAS) // alphabetical sorting for applicant name i.e. customers.applicant.customer.name
                .startObject(PROPERTIES_INDEX_ALIAS)
                  .startObject("applicant")
                    .startObject(PROPERTIES_INDEX_ALIAS)
                      .startObject(CUSTOMER_TYPE_NAME)
                        .startObject(PROPERTIES_INDEX_ALIAS)
                          .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
                        .endObject()
                      .endObject()
                      .startObject(CONTACT_INDEX_ALIAS)
                        .startObject(PROPERTIES_INDEX_ALIAS)
                          .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
                        .endObject()
                      .endObject()
                    .endObject()
                  .endObject()
                .endObject()
              .endObject()
              .startObject("locations") // alphabetical sorting for locations.address
                .startObject(PROPERTIES_INDEX_ALIAS)
                  .field("address").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
                  .field("extendedAddress").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
                  .startObject("streetAddress")
                    .field("type", "text")
                    .field(FIELDS).copyCurrentStructure(parser(alphasort()))
                  .endObject()
                  .startObject("searchGeometry")
                    .field("type", "geo_shape")
                    .field("tree", "quadtree")
                    .field("precision", "1m")
                  .endObject()
                .endObject()
              .endObject()
              .startObject(PROJECT_TYPE_NAME) // alphabetical sorting for project.identifier
                .startObject(PROPERTIES_INDEX_ALIAS)
                  .startObject("identifier")
                    .field("type", "text")
                    .field(FIELDS).copyCurrentStructure(parser(alphasort()))
                  .endObject()
                .endObject()
              .endObject()

            .endObject()
            .field("date_detection", "false")
          .endObject();

      if (logger.isDebugEnabled()) {
        logger.debug("Applications mapping: {}", mappingBuilder.string());
      }
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  /**
   * @return  Project specific type mappings for applications index.
   */
  public XContentBuilder getMappingBuilderForProject() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject(PROPERTIES_INDEX_ALIAS)
              .field("identifier").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("ownerName").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("creator").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("customerReference").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
            .endObject()
          .endObject();
      if (logger.isDebugEnabled()) {
        logger.debug("Project mapping: {}", mappingBuilder.string());
      }
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  /**
   * @return  Applications index settings.
   */
  public XContentBuilder getIndexSettingsForApplication() {
    try {
      XContentBuilder settingsBuilder = commonIndexSettings();
      if (logger.isDebugEnabled()) {
        logger.debug("application index settings {}", settingsBuilder.string());
      }
      return settingsBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  /**
   * @return  Default mappings for customers index that's applicable to all types.
   */
  public XContentBuilder getMappingBuilderForDefaultCustomersIndex() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject(MAPPING_TYPE)
              .startObject(PROPERTIES_INDEX_ALIAS)
                // alphabetical sorting with autocomplete for all name-properties in the index
                .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .endObject()
            .endObject()
          .endObject();

      if (logger.isDebugEnabled()) {
        logger.debug("Default customers index mapping: {}", mappingBuilder.string());
      }
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  /**
   * @return  Customer specific type mappings for customer index.
   */
  public XContentBuilder getMappingBuilderForCustomer() {
    try {
      XContentBuilder mappingBuilder = XContentFactory.jsonBuilder()
          .startObject()
            .startObject(PROPERTIES_INDEX_ALIAS)
              .field("registryKey").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzerAndKeywordSearchAnalyzer()))
            .endObject()
          .endObject();
      if (logger.isDebugEnabled()) {
        logger.debug("Customers mapping: {}", mappingBuilder.string());
      }
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  /**
   * @return  Customers index settings.
   */
  public XContentBuilder getIndexSettingsForCustomer() {
    try {
      XContentBuilder settingsBuilder = commonIndexSettings();
      if (logger.isDebugEnabled()) {
        logger.debug("customer index settings {}", settingsBuilder.string());
      }
      return settingsBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  private XContentParser parser(XContentBuilder xContentBuilder) throws IOException {
    SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
    return JsonXContent.jsonXContent.createParser(new NamedXContentRegistry(searchModule.getNamedXContents()), xContentBuilder.string());
  }

  private XContentBuilder autocompleteSettingsFilter() throws IOException {
    return ngramTokenFilter(1, 28);
  }

  private XContentBuilder autocompleteKeywordSettingsFilter() throws IOException {
    return ngramTokenFilter(2, 50);
  }

  private XContentBuilder ngramTokenFilter(int minGram, int maxGram) throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "edge_ngram")
          .field("min_gram", String.valueOf(minGram))
          .field("max_gram", String.valueOf(maxGram))
        .endObject();

  }


  private XContentBuilder autocompleteSettingsAnalyzer() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
        .field("type", "custom")
        .field("tokenizer", "standard")
        .array(FILTER, "lowercase", FILTER_AUTOCOMPLETE)
        .endObject();
  }

  private XContentBuilder autocompleteSettingsAnalyzerWithKeywordTokenizer() throws IOException {
    return XContentFactory.jsonBuilder()
      .startObject()
        .field("type", "custom")
        .field("tokenizer", "keyword")
        .array(FILTER, "lowercase", FILTER_AUTOCOMPLETE_KEYWORD)
      .endObject();
  }

  /**
   * ElasticSearch analyzer settings for mapping with autocomplete and alphabetical sorting.
   */
  private XContentBuilder autocompleteWithAlphaSortingMappingAnalyzer() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "text")
          .field("analyzer", ANALYZER_AUTOCOMPLETE)
          .field("search_analyzer", "standard")
          .field(FIELDS).copyCurrentStructure(parser(alphasort()))
        .endObject();
  }

  private XContentBuilder autocompleteWithAlphaSortingMappingAnalyzerAndKeywordSearchAnalyzer() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "text")
          .field("analyzer", ANALYZER_AUTOCOMPLETE_KEYWORD)
          .field("search_analyzer", "keyword")
          .field(FIELDS).copyCurrentStructure(parser(alphasort()))
        .endObject();
  }

  private XContentBuilder caseInsensitiveSortAnalyzer() throws IOException {
    return XContentFactory.jsonBuilder()
        .startObject()
          .field("type", "custom")
          .array("char_filter")
          .array(FILTER, "lowercase", "asciifolding")
        .endObject();
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
            .startObject(FILTER)
              .field(FILTER_AUTOCOMPLETE).copyCurrentStructure(parser(autocompleteSettingsFilter()))
              .field(FILTER_AUTOCOMPLETE_KEYWORD).copyCurrentStructure(parser(autocompleteKeywordSettingsFilter()))
            .endObject()
            .startObject("analyzer")
              .field(ANALYZER_AUTOCOMPLETE).copyCurrentStructure(parser(autocompleteSettingsAnalyzer()))
              .field(ANALYZER_AUTOCOMPLETE_KEYWORD).copyCurrentStructure(parser(autocompleteSettingsAnalyzerWithKeywordTokenizer()))
            .endObject()
            .startObject("normalizer")
              .field(ANALYZER_CASE_INSENSITIVE_SORT).copyCurrentStructure(parser(caseInsensitiveSortAnalyzer()))
            .endObject()
          .endObject()
        .endObject();
  }
}
