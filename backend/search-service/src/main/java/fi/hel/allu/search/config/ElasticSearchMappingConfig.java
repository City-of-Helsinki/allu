package fi.hel.allu.search.config;

import org.elasticsearch.ResourceAlreadyExistsException;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.Strings;

import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static fi.hel.allu.search.util.Constants.*;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Component for building ElasticSearch mapping configuration i.e. the "schema".
 */
@Component
public class ElasticSearchMappingConfig {

  private static final String ANALYZER_CASE_INSENSITIVE_SORT = "case_insensitive_sort";
  private static final String ANALYZER_AUTOCOMPLETE = "autocomplete";
  private static final String ANALYZER_AUTOCOMPLETE_KEYWORD = "autocomplete_keyword";
  private static final String FILTER_AUTOCOMPLETE = "autocomplete_filter";
  private static final String FILTER_AUTOCOMPLETE_KEYWORD = "autocomplete_keyword_filter";
  private static final String FILTER = "filter";
  private static final String FIELDS = "fields";
  private static final String MAPPING_TYPE = "_default_";
  private static final Logger logger = LoggerFactory.getLogger(ElasticSearchMappingConfig.class);
  private static final String BUILDER_ERROR = "Unexpected exception while creating ElasticSearch mapping builder";
  private final RestHighLevelClient client;

  @Autowired
  public ElasticSearchMappingConfig(RestHighLevelClient client) {
    this.client = client;
  }

  /**
   * Initialize single ElasticSearch index
   *
   * @param indexName the name of the index. Must be a known name.
   */
  public void initializeIndex(String indexName) {
    try {
      CreateIndexRequest indexRequest = new CreateIndexRequest(indexName);
      if (indexName.startsWith(APPLICATION_INDEX_ALIAS)) {
        indexRequest.settings(getIndexSettingsForApplication());
        indexRequest.mapping(getMappingBuilderForDefaultApplicationsIndex());
        indexRequest.mapping(getMappingBuilderForApplication());
      } else if (indexName.startsWith(CUSTOMER_INDEX_ALIAS)) {
        indexRequest.settings(getIndexSettingsForCustomer());
        indexRequest.mapping(getMappingBuilderForDefaultNameIndex("Customer"));
        indexRequest.mapping(getMappingBuilderForCustomer());
      } else if (indexName.startsWith(PROJECT_INDEX_ALIAS)) {
        indexRequest.settings(getIndexSettingsForApplication());
        indexRequest.mapping(getMappingBuilderForDefaultApplicationsIndex());
        indexRequest.mapping(getMappingBuilderForProject());
      } else if (indexName.startsWith(CONTACT_INDEX_ALIAS)) {
        indexRequest.settings(getIndexSettingsForCustomer());
        indexRequest.mapping(getMappingBuilderForDefaultNameIndex("Contact"));
        indexRequest.mapping(getMappingBuilderForContact());
      } else {
        logger.error("Unknown ElasticSearch index name {} ", indexName);
        throw new IllegalArgumentException("Unknown ElasticSearch index name " + indexName);
      }
      client.indices().create(indexRequest, RequestOptions.DEFAULT);
    } catch (ResourceAlreadyExistsException e) {
      logger.info("ElasticSearch mapping for index {} not created, because it exists already.", indexName);
    } catch (IOException e) {
      logger.error("Error initializing index {}: {}", indexName, e.toString());
      throw new RuntimeException(e);
    }
  }

  /**
   * Default mappings for applications index that's applicable to all types.
   *
   * @return XContentBuilder
   */
  public XContentBuilder getMappingBuilderForDefaultApplicationsIndex() {
    try {
    XContentBuilder mappingBuilder = jsonBuilder()
        .startObject()
          .startObject(MAPPING_TYPE)
            .startObject(PROPERTIES_INDEX_ALIAS)
            .endObject()
          .endObject()
        .endObject();
    if(logger.isDebugEnabled()) {
      logger.debug("Default applications index mapping: {}", mappingBuilder);
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
      XContentBuilder mappingBuilder = jsonBuilder()
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
        logger.debug("Applications mapping: {}", mappingBuilder);
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
      XContentBuilder mappingBuilder = jsonBuilder()
          .startObject()
            .startObject(PROPERTIES_INDEX_ALIAS)
              .field("identifier").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("ownerName").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("creator").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("customerReference").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
            .endObject()
          .endObject();
      if (logger.isDebugEnabled()) {
        logger.debug("Project mapping: {}", mappingBuilder);
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
        logger.debug("application index settings {}", settingsBuilder);
      }
      return settingsBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  /**
   * @return  Default mappings for customers index that's applicable to all types.
   */
  public XContentBuilder getMappingBuilderForDefaultNameIndex(String indexName) {
    try {
      XContentBuilder mappingBuilder = jsonBuilder()
          .startObject()
            .startObject(MAPPING_TYPE)
              .startObject(PROPERTIES_INDEX_ALIAS)
                // alphabetical sorting with autocomplete for all name-properties in the index
                .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .endObject()
            .endObject()
          .endObject();

      if (logger.isDebugEnabled()) {
        logger.debug("Default {} index mapping: {}", indexName, mappingBuilder);
      }
      return mappingBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  public XContentBuilder getMappingBuilderForContact() {
    try {
      XContentBuilder mappingBuilder = jsonBuilder()
              .startObject()
              .startObject(PROPERTIES_INDEX_ALIAS)
              .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .endObject()
              .endObject();
      if (logger.isDebugEnabled()) {
        logger.debug("Contact mapping: {}", mappingBuilder);
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
      XContentBuilder mappingBuilder = jsonBuilder()
          .startObject()
            .startObject(PROPERTIES_INDEX_ALIAS)
              .field("name").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzer()))
              .field("registryKey").copyCurrentStructure(parser(autocompleteWithAlphaSortingMappingAnalyzerAndKeywordSearchAnalyzer()))
            .endObject()
          .endObject();
      if (logger.isDebugEnabled()) {
        logger.debug("Customers mapping: {}", mappingBuilder);
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
        logger.debug("customer index settings {}", settingsBuilder);
      }
      return settingsBuilder;
    } catch (IOException e) {
      throw new RuntimeException(BUILDER_ERROR, e);
    }
  }

  private XContentParser parser(XContentBuilder xContentBuilder) throws IOException {
    return JsonXContent.jsonXContent.createParser(NamedXContentRegistry.EMPTY, DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
                                                  Strings.toString(xContentBuilder));
  }

  private XContentBuilder autocompleteSettingsFilter() throws IOException {
    return ngramTokenFilter(1, 28);
  }

  private XContentBuilder autocompleteKeywordSettingsFilter() throws IOException {
    return ngramTokenFilter(2, 50);
  }

  private XContentBuilder ngramTokenFilter(int minGram, int maxGram) throws IOException {
    return jsonBuilder()
        .startObject()
          .field("type", "edge_ngram")
          .field("min_gram", String.valueOf(minGram))
          .field("max_gram", String.valueOf(maxGram))
        .endObject();

  }


  private XContentBuilder autocompleteSettingsAnalyzer() throws IOException {
    return jsonBuilder()
        .startObject()
        .field("type", "custom")
        .field("tokenizer", "standard")
        .array(FILTER, "lowercase", FILTER_AUTOCOMPLETE)
        .endObject();
  }

  private XContentBuilder autocompleteSettingsAnalyzerWithKeywordTokenizer() throws IOException {
    return jsonBuilder()
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
    return jsonBuilder()
        .startObject()
          .field("type", "text")
          .field("analyzer", ANALYZER_AUTOCOMPLETE)
          .field("search_analyzer", "standard")
          .field(FIELDS).copyCurrentStructure(parser(alphasort()))
        .endObject();
  }

  private XContentBuilder autocompleteWithAlphaSortingMappingAnalyzerAndKeywordSearchAnalyzer() throws IOException {
    return jsonBuilder()
        .startObject()
          .field("type", "text")
          .field("analyzer", ANALYZER_AUTOCOMPLETE_KEYWORD)
          .field("search_analyzer", "keyword")
          .field(FIELDS).copyCurrentStructure(parser(alphasort()))
        .endObject();
  }

  private XContentBuilder caseInsensitiveSortAnalyzer() throws IOException {
    return jsonBuilder()
        .startObject()
          .field("type", "custom")
          .array("char_filter")
          .array(FILTER, "lowercase", "asciifolding")
        .endObject();
  }

  private XContentBuilder alphasort() throws IOException {
    return jsonBuilder()
        .startObject()
          .field("alphasort")
            .startObject()
              .field("type", "keyword")
              .field("normalizer", ANALYZER_CASE_INSENSITIVE_SORT)
            .endObject()
        .endObject();
  }

  private XContentBuilder commonIndexSettings() throws IOException {
    return jsonBuilder()
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
