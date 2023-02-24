package fi.hel.allu.search;


import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.indexConductor.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.main.MainResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

import static fi.hel.allu.search.util.Constants.APPLICATION_INDEX_ALIAS;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests of this class share same container because it saves time on testing.
 * Might change after integration and unit tests are separeted
 */
@ExtendWith(SpringExtension.class)
class ApplicationSearchIT extends BaseIntegrationTest {
    private RestHighLevelClient client;

    private ApplicationSearchService applicationSearchService;

    @BeforeEach
    void SetUp() {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create(container.getHttpHostAddress())));
        ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
        applicationSearchService = new ApplicationSearchService(elasticSearchMappingConfig, client,
                                                                new ApplicationIndexConductor());
    }

    @Test
    void testCorrectSettings() {
        MainResponse response;

        try {
            response = client.info(RequestOptions.DEFAULT);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ClusterName clusterName = response.getClusterName();
        assertEquals(CLUSTER_NAME, clusterName.value());
    }

    @Test
    void testInsertApplication() throws IOException {
        Integer id = 1;
        ApplicationES applicationES = createApplication(id);
        applicationES.setName("testi");
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();
        GetRequest getRequest = new GetRequest(APPLICATION_INDEX_ALIAS, "_doc", id.toString());
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        assertTrue(client.exists(getRequest, RequestOptions.DEFAULT));
        applicationSearchService.delete(id.toString());
    }

    @Test
    void testFindByField() {
        Integer applicationId = 42;
        ApplicationES applicationES = createApplication(applicationId);
        applicationES.setName("test");
        applicationSearchService.insert(applicationES);

        verifyOneApplicationQueryResult("name", "test", applicationId);
        applicationSearchService.delete(applicationId.toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"TP000001", "TP00"})
    void testFindByFieldPartial(String queryParameter) {
        Integer applicationId = 1;
        ApplicationES applicationES = createApplication(1);
        applicationES.setName("TP000001");
        applicationSearchService.insert(applicationES);

        verifyOneApplicationQueryResult("applicationId", queryParameter, applicationId);

        applicationSearchService.delete("1");
    }

    @Test
    void testFindByFieldSorted() {
        ApplicationES applicationES1 = createApplication(1);
        applicationES1.setName("c 3");
        ApplicationES applicationES2 = createApplication(2);
        applicationES2.setName("a 2");
        ApplicationES applicationES3 = createApplication(3);
        applicationES3.setName("b 1");
        applicationSearchService.insert(applicationES1);
        applicationSearchService.insert(applicationES2);
        applicationSearchService.insert(applicationES3);

        ApplicationQueryParameters params = new ApplicationQueryParameters();
        QueryParameter parameter = new QueryParameter("owner.userName",
                                                      Arrays.asList("notexisting1", USERNAME, "notexisting2"));
        List<QueryParameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);
        params.setQueryParameters(parameterList);
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.Direction.ASC, "name");
        applicationSearchService.refreshIndex();
        List<Integer> appList = applicationSearchService.findByField(params, pageRequest).getContent();
        assertNotNull(appList);
        assertEquals(3, appList.size());
        assertEquals(2, appList.get(0).intValue());
        assertEquals(3, appList.get(1).intValue());
        assertEquals(1, appList.get(2).intValue());
        applicationSearchService.delete("1");
        applicationSearchService.delete("2");
        applicationSearchService.delete("3");
    }

    @Test
    void testPagedSearch() {
        for (int i = 10; i < 99; ++i) {
            ApplicationES applicationES = createApplication(i);
            applicationES.setName("Application #" + i);
            applicationSearchService.insert(applicationES);
        }
        applicationSearchService.refreshIndex();
        ApplicationQueryParameters parameters = new ApplicationQueryParameters();
        parameters.setQueryParameters(Collections.singletonList(new QueryParameter("owner.userName", USERNAME)));
        Page<Integer> appPage = applicationSearchService.findByField(parameters,
                                                                     PageRequest.of(2, 10, Sort.Direction.ASC, "name"));
        assertEquals(10, appPage.getSize());
        assertEquals(Arrays.asList(30, 31, 32, 33, 34, 35, 36, 37, 38, 39), appPage.getContent());
        assertEquals(89, appPage.getTotalElements());
    }

    /**
     * Name search is sort id determinied by id number
     *
     * @param properties search filter
     */
    @ParameterizedTest
    @ValueSource(
            strings = {"name", "owner.userName", "customers.applicant.customer.name", "locations.streetAddress",
                    "locations.cityDistrictId"})
    void testFieldMappingsAndSorting(String properties) {
        ApplicationES applicationES1 = createApplication(1, 1);
        applicationES1.setCustomers(createRoleTypedCustomer(USERNAME + " " + 1));
        applicationES1.setLocations(
                Arrays.asList(new LocationES(1, "AEnsimmäinen osoite 9", "00100", "Sinki", 1, "Eka lisätieto"),
                              new LocationES(2, "Zviimonen 777", "00100", "Sinki", 5, "Vika lisätieto")));

        ApplicationES applicationES2 = createApplication(2, 2);
        applicationES2.setCustomers(createRoleTypedCustomer(USERNAME + " " + 2));
        applicationES2.setLocations(Collections.singletonList(
                new LocationES(3, "bToinen osoite 1", "00100", "Sinki", 2, "Toka lisätieto")));

        ApplicationES applicationES3 = createApplication(3, 3);
        applicationES3.setCustomers(createRoleTypedCustomer(USERNAME + " " + 3));
        applicationES3.setLocations(
                Arrays.asList(new LocationES(4, "Zviimonen 777", "00100", "Sinki", 3, "Vika lisätieto"),
                              new LocationES(5, "Ckolmas osoite 5", "00100", "Sinki", 4, "Kolmoslisätieto")));

        applicationSearchService.insert(applicationES1);
        applicationSearchService.insert(applicationES2);
        applicationSearchService.insert(applicationES3);
        applicationSearchService.refreshIndex();

        ApplicationQueryParameters params = new ApplicationQueryParameters();
        QueryParameter nameParameter = new QueryParameter("name", USERNAME);
        QueryParameter ownerNameParameter = new QueryParameter("owner.userName", Collections.singletonList(USERNAME));
        QueryParameter customerNameParameter = new QueryParameter("customers.applicant.customer.name",
                                                                  Collections.singletonList(USERNAME));

        List<QueryParameter> parameterList = new ArrayList<>(
                Arrays.asList(nameParameter, ownerNameParameter, customerNameParameter));
        params.setQueryParameters(parameterList);

        PageRequest pageRequest = PageRequest.of(0, 100, Sort.Direction.ASC, properties);
        List<Integer> applist = applicationSearchService.findByField(params, pageRequest).getContent();

        assertEquals(3, applist.size());
        assertEquals(Arrays.asList(1, 2, 3), applist);

        applicationSearchService.delete("1");
        applicationSearchService.delete("2");
        applicationSearchService.delete("3");
    }

    @Test
    void testFindByMultipleTypesSorted() {
        ApplicationES applicationES1 = createApplication(1);
        ApplicationES applicationES2 = createApplication(2);
        ApplicationES applicationES3 = createApplication(3);

        applicationES1.setStatus(new StatusTypeES(StatusType.FINISHED));
        applicationES2.setStatus(new StatusTypeES(StatusType.HANDLING));
        applicationES3.setStatus(new StatusTypeES(StatusType.DECISION));

        applicationSearchService.insert(applicationES1);
        applicationSearchService.insert(applicationES2);
        applicationSearchService.insert(applicationES3);

        ApplicationQueryParameters params = new ApplicationQueryParameters();
        QueryParameter parameter = new QueryParameter("status.value", Arrays.asList(StatusType.FINISHED.name(),
                                                                                    StatusType.HANDLING.name(),
                                                                                    StatusType.DECISION.name()));
        List<QueryParameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);
        params.setQueryParameters(parameterList);
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.Direction.ASC, "status");
        applicationSearchService.refreshIndex();
        List<Integer> appList = applicationSearchService.findByField(params, pageRequest).getContent();
        assertNotNull(appList);
        assertEquals(3, appList.size());
        // results should be sorted by the enumeration order of StatusType
        assertEquals(2, appList.get(0).intValue());
        assertEquals(3, appList.get(1).intValue());
        assertEquals(1, appList.get(2).intValue());
        applicationSearchService.delete("1");
        applicationSearchService.delete("2");
        applicationSearchService.delete("3");
    }

    @Test
    void testFindByContact() {
        Integer applicatonId = 1;
        ApplicationES applicationES = createApplication(applicatonId);
        CustomerES customerES = new CustomerES();
        RoleTypedCustomerES roleTypedCustomerES = new RoleTypedCustomerES(
                Collections.singletonMap(CustomerRoleType.APPLICANT,
                                         SearchTestUtil.createCustomerWithContacts(customerES, createContacts())));
        applicationES.setCustomers(roleTypedCustomerES);
        applicationSearchService.insert(applicationES);

        verifyOneApplicationQueryResult("customers.applicant.contacts.name", "kontakti", applicatonId);
        applicationSearchService.delete("1");
    }

    @Test
    void testFindByMultipleOwners() {
        ApplicationES applicationES = createApplication(1);
        applicationSearchService.insert(applicationES);

        ApplicationQueryParameters params = new ApplicationQueryParameters();
        QueryParameter parameter = new QueryParameter("owner.userName",
                                                      Arrays.asList("notexisting1", USERNAME, "notexisting2"));
        List<QueryParameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);
        params.setQueryParameters(parameterList);
        applicationSearchService.refreshIndex();
        List<Integer> appList = applicationSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(1, appList.size());
        applicationSearchService.delete("1");
    }

    @Test
    void testFindByMultipleStatuses() {
        ApplicationES applicationES = createApplication(1);
        applicationSearchService.insert(applicationES);

        ApplicationQueryParameters params = new ApplicationQueryParameters();
        QueryParameter parameter = new QueryParameter("status.value", Arrays.asList(StatusType.PENDING.name(),
                                                                                    StatusType.CANCELLED.name()));
        List<QueryParameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);
        params.setQueryParameters(parameterList);
        applicationSearchService.refreshIndex();
        List<Integer> appList = applicationSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(1, appList.size());
        applicationSearchService.delete("1");
    }

    @Test
    void testFindByDateField() {
        ApplicationES applicationES = createApplication(1);
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();

        ApplicationQueryParameters params = new ApplicationQueryParameters();
        ZonedDateTime testStartTime = ZonedDateTime.parse("2016-07-05T06:10:10.000Z");
        ZonedDateTime testEndTime = ZonedDateTime.parse("2016-07-06T05:10:10.000Z");
        QueryParameter parameter = new QueryParameter("creationTime", testStartTime, testEndTime);
        List<QueryParameter> parameterList = new ArrayList<>();
        parameterList.add(parameter);
        params.setQueryParameters(parameterList);
        List<Integer> appList = applicationSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(1, appList.size());

        testStartTime = ZonedDateTime.parse("2016-07-03T06:10:10.000Z");
        testEndTime = ZonedDateTime.parse("2016-07-04T05:10:10.000Z");
        parameter = new QueryParameter("creationTime", testStartTime, testEndTime);
        parameterList = new ArrayList<>();
        parameterList.add(parameter);
        params.setQueryParameters(parameterList);
        appList = applicationSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(0, appList.size());

        applicationSearchService.delete("1");
    }

    @Test
    void UpdateApplication() {
        ApplicationES applicationES = createApplication(100);
        applicationSearchService.insert(applicationES);

        final String newName = "Päivitetty testi";
        applicationES.setName(newName);

        applicationSearchService.bulkUpdate(Collections.singletonList(applicationES));
        applicationSearchService.refreshIndex();


        ApplicationQueryParameters params = SearchTestUtil.createApplicationQueryParameters("name", newName);
        List<Integer> appList = applicationSearchService.findByField(params, null).getContent();
        assertEquals(1, appList.size());
        applicationSearchService.delete("100");
    }

    @Test
    void testUpdateCustomerWithContacts() {
        // create data
        Integer applicationId = 100;
        ApplicationES applicationES = createApplication(applicationId, createContacts(
                Arrays.asList("kontakti ihminen", "toinen contact")));
        applicationSearchService.insert(applicationES);

        // update data
        Map<CustomerRoleType, CustomerWithContactsES> testUpdateData = Collections.singletonMap(
                CustomerRoleType.APPLICANT, SearchTestUtil.createCustomerWithContacts(createCustomer("anewname"),
                                                                                      createContacts(
                                                                                              Arrays.asList("uusi nimi",
                                                                                                            "joku tyyppi"))));
        applicationSearchService.updateCustomersWithContacts(applicationId, testUpdateData);

        // verify update
        verifyOneApplicationQueryResult("customers.applicant.customer.name", "anewname", applicationId);
        verifyOneApplicationQueryResult("customers.applicant.contacts.name", "tyyppi", applicationId);

        // clean up
        applicationSearchService.delete("100");
    }

    @Test
    void testRecurringApplicationWithinOneCalendarYear() {
        ApplicationES applicationES = createApplication(100);
        RecurringApplication recurringApplication = new RecurringApplication(
                ZonedDateTime.parse("2016-07-05T06:23:04.000Z"), ZonedDateTime.parse("2016-08-05T06:23:04.000Z"),
                RecurringApplication.MAX_END_TIME);
        applicationES.setRecurringApplication(recurringApplication);
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();


        // test period completely outside recurring period, before recurring period
        List<Integer> appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-06-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-06-11T06:23:04.000Z")), null).getContent();
        assertEquals(0, appList.size());
        // test period completely outside recurring period, after recurring period
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-08-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-09-11T06:23:04.000Z")), null).getContent();
        assertEquals(0, appList.size());
        // test period completely within recurring period
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-07-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-07-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        // test period partially within recurring period
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-05-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-07-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());

        // test period partially within recurring period (beginning of test period) and that overlaps with two
        // calendar years.
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-07-04T06:23:04.000Z"),
                                     ZonedDateTime.parse("2017-07-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());

        // test period partially within recurring period (end of test period) and that overlaps with two calendar years
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2015-12-04T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-07-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());

        applicationSearchService.delete("100");
    }

    @Test
    void testRecurringApplicationWithinTwoCalendarYears() {
        ApplicationES applicationES = createApplication(100);
        RecurringApplication recurringApplication = new RecurringApplication(
                ZonedDateTime.parse("2015-11-05T06:23:04.000Z"), ZonedDateTime.parse("2016-04-05T10:23:04.000Z"),
                RecurringApplication.MAX_END_TIME);
        applicationES.setRecurringApplication(recurringApplication);
        applicationSearchService.insert(applicationES);
        applicationSearchService.refreshIndex();

        // test period completely outside recurring period
        List<Integer> appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-06-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-06-11T06:23:04.000Z")), null).getContent();
        assertEquals(0, appList.size());
        // test period completely within recurring period, in the first period
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2015-11-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2015-11-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        // test period completely within recurring period, in the second period
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-01-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-02-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        // test period longer than one year, match in the end of long period
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2010-05-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2016-07-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        // test period longer than one year, match in the beginning of long period
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-03-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2018-07-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2018-03-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2020-07-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());

        applicationSearchService.delete("100");
    }

    @Test
    void testRecurringApplicationBeginEndYears() {
        int withEndYearAppId = 100;
        ApplicationES applicationESWithEndYear = createApplication(withEndYearAppId);
        RecurringApplication recurringApplication = new RecurringApplication(
                ZonedDateTime.parse("2015-11-05T06:23:04.000Z"), ZonedDateTime.parse("2016-04-05T10:23:04.000Z"),
                ZonedDateTime.parse("2020-04-05T10:23:04.000Z"));
        applicationESWithEndYear.setRecurringApplication(recurringApplication);
        applicationSearchService.insert(applicationESWithEndYear);
        applicationSearchService.refreshIndex();

        // find within period, but before begin year
        List<Integer> appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2013-03-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2014-03-11T06:23:04.000Z")), null).getContent();
        assertEquals(0, appList.size());
        // find outside period, on initial year
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2014-03-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2015-03-11T06:07:08.000Z")), null).getContent();
        assertEquals(0, appList.size());
        // find within period, but after end year
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2021-03-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2021-03-11T06:23:04.000Z")), null).getContent();
        assertEquals(0, appList.size());
        // find within period, after initial year
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2019-03-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2019-03-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        // find within period, on the final year
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2020-03-10T06:23:04.000Z"),
                                     ZonedDateTime.parse("2020-03-11T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        // find outside period, on the final year
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2020-04-06T06:23:04.000Z"),
                                     ZonedDateTime.parse("2020-04-07T06:23:04.000Z")), null).getContent();
        assertEquals(0, appList.size());
        // find within period, no end time
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2016-04-04T06:23:04.000Z"), null), null).getContent();
        assertEquals(1, appList.size());
        // find within recurring period, no end time
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2017-05-04T06:23:04.000Z"), null), null).getContent();
        assertEquals(1, appList.size());
        // find outside recurring period, no end time
        appList = applicationSearchService.findByField(
                createRecurringQuery(ZonedDateTime.parse("2021-01-04T06:23:04.000Z"), null), null).getContent();
        assertEquals(0, appList.size());

        // find within period, no start time
        appList = applicationSearchService.findByField(
                createRecurringQuery(null, ZonedDateTime.parse("2015-12-04T06:23:04.000Z")), null).getContent();
        assertEquals(1, appList.size());
        // find outside recurring period, no start time
        appList = applicationSearchService.findByField(
                createRecurringQuery(null, ZonedDateTime.parse("2015-05-04T06:23:04.000Z")), null).getContent();
        assertEquals(0, appList.size());

        applicationSearchService.delete("100");
    }

    private RoleTypedCustomerES createRoleTypedCustomer(String customerName) {
        return new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT,
                                                                SearchTestUtil.createCustomerWithContacts(
                                                                        createCustomer(customerName))));
    }

    private CustomerES createCustomer(String customerName) {
        CustomerES customerES = new CustomerES();
        customerES.setName(customerName);
        return customerES;
    }

    private void verifyOneApplicationQueryResult(String fieldName, String parameter, Integer id) {
        ApplicationQueryParameters params = SearchTestUtil.createApplicationQueryParameters(fieldName, parameter);
        applicationSearchService.refreshIndex();
        List<Integer> appList = applicationSearchService.findByField(params, null).getContent();
        assertNotNull(appList);
        assertEquals(1, appList.size());
        assertEquals(appList.get(0), id);

    }

    private ApplicationES createApplication(int applicationId, List<ContactES> contacts) {
        ApplicationES applicationES = createApplication(applicationId);
        CustomerES customerES = createCustomer("applicant");
        RoleTypedCustomerES roleTypedCustomerES = new RoleTypedCustomerES(
                Collections.singletonMap(CustomerRoleType.APPLICANT,
                                         SearchTestUtil.createCustomerWithContacts(customerES, contacts)));
        applicationES.setCustomers(roleTypedCustomerES);
        return applicationES;
    }

}