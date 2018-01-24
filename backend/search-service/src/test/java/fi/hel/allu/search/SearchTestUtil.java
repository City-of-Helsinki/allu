package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_INDEX_ALIAS;
import static fi.hel.allu.search.config.ElasticSearchMappingConfig.CUSTOMER_INDEX_ALIAS;

public class SearchTestUtil {

  public static ElasticSearchMappingConfig searchIndexSetup(Client client) {
    ElasticSearchMappingConfig elasticSearchMappingConfig = new ElasticSearchMappingConfig(client);

    try {
      // delete indexes
      client.admin().indices().delete(new DeleteIndexRequest(APPLICATION_INDEX_ALIAS)).actionGet();
      client.admin().indices().delete(new DeleteIndexRequest(CUSTOMER_INDEX_ALIAS)).actionGet();
    } catch (IndexNotFoundException e) {
      System.out.println("Index not found for deleting...");
    }

    elasticSearchMappingConfig.initializeIndex(APPLICATION_INDEX_ALIAS);
    elasticSearchMappingConfig.initializeIndex(CUSTOMER_INDEX_ALIAS);

    try {
      client.admin().indices().prepareGetMappings(APPLICATION_INDEX_ALIAS).get();
      client.admin().indices().prepareGetMappings(CUSTOMER_INDEX_ALIAS).get();
    } catch (IndexNotFoundException e) {
      System.out.println("Warning, indexes were not created immediately... test may fail because of this");
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

  public static CustomerWithContactsES createCustomerWithContacts(CustomerES customerES) {
    CustomerWithContactsES cwcES = new CustomerWithContactsES();
    cwcES.setCustomer(customerES);
    return cwcES;
  }

  public static CustomerWithContactsES createCustomerWithContacts(CustomerES customerES, List<ContactES> contacts) {
    CustomerWithContactsES cwcES = createCustomerWithContacts(customerES);
    cwcES.setContacts(contacts);
    return cwcES;
  }
}
