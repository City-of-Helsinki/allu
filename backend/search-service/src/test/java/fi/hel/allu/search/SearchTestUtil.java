package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.util.ClientWrapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.IndexNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SearchTestUtil {

    public static ElasticSearchMappingConfig searchIndexSetup(ClientWrapper clientWrapper, List<String> indexes) {
        RestHighLevelClient client = clientWrapper.getHlrc();
        ElasticSearchMappingConfig elasticSearchMappingConfig = new ElasticSearchMappingConfig(clientWrapper);
        for (String index : indexes) {
            try {
                // delete index
               deleteIndex(client, index);
            } catch (IndexNotFoundException e) {
                System.out.println("Index not found for deleting...");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            elasticSearchMappingConfig.initializeIndex(index);

            try {
                client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
            } catch (IndexNotFoundException e) {
                System.out.println("Warning, indexes were not created immediately... test may fail because of this");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return elasticSearchMappingConfig;
    }

    public static void deleteIndex(RestHighLevelClient client, String index) throws IOException {
        if (client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT))
            client.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
    }

    public static QueryParameters createQueryParameters(String fieldName, String queryParameter) {
        QueryParameters params = new QueryParameters();
        QueryParameter parameter = new QueryParameter(fieldName, queryParameter);
        List<QueryParameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);
        params.setQueryParameters(parameterList);
        return params;
    }

    public static ApplicationQueryParameters createApplicationQueryParameters(String fieldName, String queryParameter) {
        ApplicationQueryParameters params = new ApplicationQueryParameters();
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