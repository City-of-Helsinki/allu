package fi.hel.allu.search;

import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.IndexNotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static fi.hel.allu.search.util.Constants.*;

public class SearchTestUtil {

    public static ElasticSearchMappingConfig searchIndexSetup(RestHighLevelClient client) {
        ElasticSearchMappingConfig elasticSearchMappingConfig = new ElasticSearchMappingConfig(client);

        try {
            // delete indexes
            if (client.indices().exists(new GetIndexRequest(APPLICATION_INDEX_ALIAS), RequestOptions.DEFAULT))
                client.indices().delete(new DeleteIndexRequest(APPLICATION_INDEX_ALIAS), RequestOptions.DEFAULT);
            if (client.indices().exists(new GetIndexRequest(CUSTOMER_INDEX_ALIAS), RequestOptions.DEFAULT))
                client.indices().delete(new DeleteIndexRequest(CUSTOMER_INDEX_ALIAS), RequestOptions.DEFAULT);
            if (client.indices().exists(new GetIndexRequest(PROJECT_INDEX_ALIAS), RequestOptions.DEFAULT))
                client.indices().delete(new DeleteIndexRequest(PROJECT_INDEX_ALIAS), RequestOptions.DEFAULT);
            if (client.indices().exists(new GetIndexRequest(CONTACT_INDEX_ALIAS), RequestOptions.DEFAULT))
                client.indices().delete(new DeleteIndexRequest(CONTACT_INDEX_ALIAS), RequestOptions.DEFAULT);
            if (client.indices().exists(new GetIndexRequest(SUPERVISION_TASK_INDEX), RequestOptions.DEFAULT))
                client.indices().delete(new DeleteIndexRequest(SUPERVISION_TASK_INDEX), RequestOptions.DEFAULT);
        } catch (IndexNotFoundException e) {
            System.out.println("Index not found for deleting...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        elasticSearchMappingConfig.initializeIndex(APPLICATION_INDEX_ALIAS);
        elasticSearchMappingConfig.initializeIndex(CUSTOMER_INDEX_ALIAS);
        elasticSearchMappingConfig.initializeIndex(PROJECT_INDEX_ALIAS);
        elasticSearchMappingConfig.initializeIndex(CONTACT_INDEX_ALIAS);
        elasticSearchMappingConfig.initializeIndex(SUPERVISION_TASK_INDEX);

        try {
            client.indices().exists(new GetIndexRequest(APPLICATION_INDEX_ALIAS), RequestOptions.DEFAULT);
            client.indices().exists(new GetIndexRequest(CUSTOMER_INDEX_ALIAS), RequestOptions.DEFAULT);
            client.indices().exists(new GetIndexRequest(PROJECT_INDEX_ALIAS), RequestOptions.DEFAULT);
            client.indices().exists(new GetIndexRequest(CONTACT_INDEX_ALIAS), RequestOptions.DEFAULT);
            client.indices().exists(new GetIndexRequest(SUPERVISION_TASK_INDEX), RequestOptions.DEFAULT);
        } catch (IndexNotFoundException e) {
            System.out.println("Warning, indexes were not created immediately... test may fail because of this");
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public static QueryParameters addQueryParameters(QueryParameters params, String fieldName, String queryParameter) {
        QueryParameter parameter = new QueryParameter(fieldName, queryParameter);
        List<QueryParameter> parameterList = params.getQueryParameters();
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