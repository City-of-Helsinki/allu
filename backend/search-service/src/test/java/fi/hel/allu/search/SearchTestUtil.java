package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.IndexNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_INDEX_NAME;
import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_TYPE_NAME;

public class SearchTestUtil {
  public static ElasticSearchMappingConfig searchIndexSetup(Client client) {
    ElasticSearchMappingConfig elasticSearchMappingConfig = new ElasticSearchMappingConfig(null);
    XContentBuilder mappingBuilder = new ElasticSearchMappingConfig(null).getMappingBuilderForApplication();

    try {
      client.admin().indices().delete(new DeleteIndexRequest(APPLICATION_INDEX_NAME)).actionGet();
    } catch (IndexNotFoundException e) {
      System.out.println("Index not found for deleting...");
    }

    CreateIndexRequestBuilder createIndexRequestBuilder =
        client.admin().indices().prepareCreate(APPLICATION_INDEX_NAME);
    createIndexRequestBuilder.addMapping(APPLICATION_TYPE_NAME, mappingBuilder);
    createIndexRequestBuilder.execute().actionGet();

    try {
      client.admin().indices().prepareGetMappings(APPLICATION_INDEX_NAME).get();
    } catch (IndexNotFoundException e) {
      System.out.println("Warning, index was not created immediately... test may fail because of this");
    }

    return elasticSearchMappingConfig;
  }


  public static QueryParameters createQueryParameters(String fieldName, String queryParameter) {
    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter(fieldName, queryParameter);
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    return params;
  }
}
