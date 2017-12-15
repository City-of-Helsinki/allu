package fi.hel.allu.search;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.RecurringApplication;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.ApplicationIndexConductor;
import fi.hel.allu.search.service.ApplicationSearchService;

import org.elasticsearch.client.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ApplicationSearchTest {

  private static final String USERNAME = "someusername";

  @Autowired
  private Client client;
  private ApplicationSearchService applicationSearchService;


  @Before
  public void setUp() throws Exception {
    ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
    applicationSearchService = new ApplicationSearchService(
        elasticSearchMappingConfig,
        client, new ApplicationIndexConductor());
  }

  @Test
  public void testInsertApplication() {
    ApplicationES applicationES = new ApplicationES();
    applicationES.setType(new ApplicationTypeES(ApplicationType.EVENT));
    applicationES.setId(1);
    applicationES.setHandler(createUser());
    applicationES.setName("Ensimmäinen testi");
    applicationES.setStatus(new StatusTypeES(StatusType.PENDING));
    applicationES.setApplicationTypeData(createApplicationTypeData());

    applicationSearchService.insert(applicationES);
  }

  @Test
  public void testFindByField() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insert(applicationES);

    QueryParameters params = SearchTestUtil.createQueryParameters("name", "testi");
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.delete("1");
  }

  @Test
  public void testFindByFieldPartial() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insert(applicationES);
    applicationSearchService.refreshIndex();

    QueryParameters params = SearchTestUtil.createQueryParameters("applicationId", "TP00");
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());

    params = SearchTestUtil.createQueryParameters("applicationId", "TP000001");
    appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());

    applicationSearchService.delete("1");
  }

  @Test
  public void testFindByFieldSorted() {
    ApplicationES applicationES1 = createApplication(1);
    applicationES1.setName("c 3");
    ApplicationES applicationES2 = createApplication(2);
    applicationES2.setName("a 2");
    ApplicationES applicationES3 = createApplication(3);
    applicationES3.setName("b 1");
    applicationSearchService.insert(applicationES1);
    applicationSearchService.insert(applicationES2);
    applicationSearchService.insert(applicationES3);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("handler.userName", Arrays.asList("notexisting1", USERNAME, "notexisting2"));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
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
  public void testFieldMappingsAndSorting() {
    ApplicationES applicationES1 = createApplication(1);

    applicationES1.setName(USERNAME + " " + 1);
    applicationES1.setHandler(new UserES(USERNAME + " " + 1, "not used"));
    CustomerES customerES = new CustomerES();
    customerES.setName(USERNAME + " " + 1);
    RoleTypedCustomerES roleTypedCustomerES =
        new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT, SearchTestUtil.createCustomerWithContacts(customerES)));
    applicationES1.setCustomers(roleTypedCustomerES);

    applicationES1.setLocations(Arrays.asList(
        new LocationES("AEnsimmäinen osoite 9", "00100", "Sinki", 1, "Eka lisätieto"),
        new LocationES("Zviimonen 777", "00100", "Sinki", 5, "Vika lisätieto")));

    ApplicationES applicationES2 = createApplication(2);
    applicationES2.setName(USERNAME + " " + 2);
    applicationES2.setHandler(new UserES(USERNAME + " " + 2, "not used"));
    customerES = new CustomerES();
    customerES.setName(USERNAME + " " + 2);
    roleTypedCustomerES =
        new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT, SearchTestUtil.createCustomerWithContacts(customerES)));
    applicationES2.setCustomers(roleTypedCustomerES);
    applicationES2.setLocations(
        Collections.singletonList(new LocationES("bToinen osoite 1", "00100", "Sinki", 2, "Toka lisätieto")));

    ApplicationES applicationES3 = createApplication(3);
    applicationES3.setName(USERNAME + " " + 3);
    applicationES3.setHandler(new UserES(USERNAME + " " + 3, "not used"));
    customerES = new CustomerES();
    customerES.setName(USERNAME + " " + 3);
    roleTypedCustomerES =
        new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT, SearchTestUtil.createCustomerWithContacts(customerES)));
    applicationES3.setCustomers(roleTypedCustomerES);
    applicationES3.setLocations(Arrays.asList(
        new LocationES("Zviimonen 777", "00100", "Sinki", 3, "Vika lisätieto"),
        new LocationES("Ckolmas osoite 5", "00100", "Sinki", 4, "Kolmoslisätieto")));

    applicationSearchService.insert(applicationES1);
    applicationSearchService.insert(applicationES2);
    applicationSearchService.insert(applicationES3);
    applicationSearchService.refreshIndex();

    QueryParameters params = new QueryParameters();
    QueryParameter nameParameter = new QueryParameter("name", USERNAME);
    QueryParameter handlerNameParameter = new QueryParameter("handler.userName", Arrays.asList(USERNAME));
    QueryParameter customerNameParameter = new QueryParameter("customers.applicant.customer.name", Arrays.asList(USERNAME));

    List<QueryParameter> parameterList = new ArrayList<>(Arrays.asList(nameParameter, handlerNameParameter, customerNameParameter));
    params.setQueryParameters(parameterList);
    params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));
    List<Integer> appList = applicationSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("handler.userName.alphasort", QueryParameters.Sort.Direction.ASC));
    appList = applicationSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("customers.applicant.customer.name.alphasort", QueryParameters.Sort.Direction.ASC));
    appList = applicationSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("locations.streetAddress.alphasort", QueryParameters.Sort.Direction.ASC));
    appList = applicationSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("locations.cityDistrictId", QueryParameters.Sort.Direction.ASC));
    appList = applicationSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);
  }

    @Test
  public void testFindByMultipleTypesSorted() {
    ApplicationES applicationES1 = createApplication(1);
    ApplicationES applicationES2 = createApplication(2);
    ApplicationES applicationES3 = createApplication(3);

    applicationES1.setStatus(new StatusTypeES(StatusType.FINISHED));
    applicationES2.setStatus(new StatusTypeES(StatusType.HANDLING));
    applicationES3.setStatus(new StatusTypeES(StatusType.DECISION));

    applicationSearchService.insert(applicationES1);
    applicationSearchService.insert(applicationES2);
    applicationSearchService.insert(applicationES3);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter(
        "status.value", Arrays.asList(StatusType.FINISHED.name(), StatusType.HANDLING.name(), StatusType.DECISION.name()));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    params.setSort(new QueryParameters.Sort("status.ordinal", QueryParameters.Sort.Direction.ASC));
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
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
  public void testFindByContact() {
    ApplicationES applicationES = createApplication(1);
    CustomerES customerES = new CustomerES();
    RoleTypedCustomerES roleTypedCustomerES =
        new RoleTypedCustomerES(Collections.singletonMap(CustomerRoleType.APPLICANT,
            SearchTestUtil.createCustomerWithContacts(customerES, createContacts())));
    applicationES.setCustomers(roleTypedCustomerES);
    applicationSearchService.insert(applicationES);

    QueryParameters params = SearchTestUtil.createQueryParameters("customers.applicant.contacts.name", "kontakti");
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.delete("1");
  }

  @Test
  public void testFindByMultipleHandlers() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insert(applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("handler.userName", Arrays.asList("notexisting1", USERNAME, "notexisting2"));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.delete("1");
  }

  @Test
  public void testFindByMultipleStatuses() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insert(applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("status.value", Arrays.asList(StatusType.PENDING.name(), StatusType.CANCELLED.name()));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.delete("1");
  }

  @Test
  public void testFindByDateField() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insert(applicationES);
    applicationSearchService.refreshIndex();

    QueryParameters params = new QueryParameters();
    ZonedDateTime testStartTime = ZonedDateTime.parse("2016-07-05T06:10:10.000Z");
    ZonedDateTime testEndTime = ZonedDateTime.parse(  "2016-07-06T05:10:10.000Z");
    QueryParameter parameter = new QueryParameter("creationTime", testStartTime, testEndTime);
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());

    testStartTime = ZonedDateTime.parse("2016-07-03T06:10:10.000Z");
    testEndTime = ZonedDateTime.parse(  "2016-07-04T05:10:10.000Z");
    parameter = new QueryParameter("creationTime", testStartTime, testEndTime);
    parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(0, appList.size());

    applicationSearchService.delete("1");
  }

  @Test
  public void testUpdateApplication() {
    ApplicationES applicationES = createApplication(100);
    applicationSearchService.insert(applicationES);

    final String newName = "Päivitetty testi";
    applicationES.setName(newName);

    applicationSearchService.bulkUpdate(Collections.singletonList(applicationES));
    applicationSearchService.refreshIndex();

    QueryParameters params = SearchTestUtil.createQueryParameters("name", newName);
    List<Integer> appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());
    applicationSearchService.delete("100");
  }

  @Test
  public void testRecurringApplicationWithinOneCalendarYear() {
    ApplicationES applicationES = createApplication(100);
    RecurringApplication recurringApplication = new RecurringApplication(
        ZonedDateTime.parse("2016-07-05T06:23:04.000Z"),
        ZonedDateTime.parse("2016-08-05T06:23:04.000Z"),
        RecurringApplication.MAX_END_TIME);
    applicationES.setRecurringApplication(recurringApplication);
    applicationSearchService.insert(applicationES);
    applicationSearchService.refreshIndex();


    // test period completely outside recurring period, before recurring period
    List<Integer> appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-06-10T06:23:04.000Z"),
        ZonedDateTime.parse("2016-06-11T06:23:04.000Z")
    ));
    assertEquals(0, appList.size());
    // test period completely outside recurring period, after recurring period
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-08-10T06:23:04.000Z"),
        ZonedDateTime.parse("2016-09-11T06:23:04.000Z")
    ));
    assertEquals(0, appList.size());
    // test period completely within recurring period
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-07-10T06:23:04.000Z"),
        ZonedDateTime.parse("2016-07-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    // test period partially within recurring period
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-05-10T06:23:04.000Z"),
        ZonedDateTime.parse("2016-07-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());

    // test period partially within recurring period (beginning of test period) and that overlaps with two calendar years.
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-07-04T06:23:04.000Z"),
        ZonedDateTime.parse("2017-07-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());

    // test period partially within recurring period (end of test period) and that overlaps with two calendar years
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2015-12-04T06:23:04.000Z"),
        ZonedDateTime.parse("2016-07-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());

    applicationSearchService.delete("100");
  }

  @Test
  public void testRecurringApplicationWithinTwoCalendarYears() {
    ApplicationES applicationES = createApplication(100);
    RecurringApplication recurringApplication = new RecurringApplication(
        ZonedDateTime.parse("2015-11-05T06:23:04.000Z"),
        ZonedDateTime.parse("2016-04-05T10:23:04.000Z"),
        RecurringApplication.MAX_END_TIME);
    applicationES.setRecurringApplication(recurringApplication);
    applicationSearchService.insert(applicationES);
    applicationSearchService.refreshIndex();

    // test period completely outside recurring period
    List<Integer> appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-06-10T06:23:04.000Z"),
        ZonedDateTime.parse("2016-06-11T06:23:04.000Z")
    ));
    assertEquals(0, appList.size());
    // test period completely within recurring period, in the first period
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2015-11-10T06:23:04.000Z"),
        ZonedDateTime.parse("2015-11-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    // test period completely within recurring period, in the second period
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-01-10T06:23:04.000Z"),
        ZonedDateTime.parse("2016-02-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    // test period longer than one year, match in the end of long period
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2010-05-10T06:23:04.000Z"),
        ZonedDateTime.parse("2016-07-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    // test period longer than one year, match in the beginning of long period
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-03-10T06:23:04.000Z"),
        ZonedDateTime.parse("2018-07-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2018-03-10T06:23:04.000Z"),
        ZonedDateTime.parse("2020-07-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());

    applicationSearchService.delete("100");
  }

  @Test
  public void testRecurringApplicationBeginEndYears() {
    int withEndYearAppId = 100;
    ApplicationES applicationESWithEndYear = createApplication(withEndYearAppId);
    RecurringApplication recurringApplication = new RecurringApplication(
        ZonedDateTime.parse("2015-11-05T06:23:04.000Z"),
        ZonedDateTime.parse("2016-04-05T10:23:04.000Z"),
        ZonedDateTime.parse("2020-04-05T10:23:04.000Z"));
    applicationESWithEndYear.setRecurringApplication(recurringApplication);
    applicationSearchService.insert(applicationESWithEndYear);
    applicationSearchService.refreshIndex();

    // find within period, but before begin year
    List<Integer> appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2013-03-10T06:23:04.000Z"),
        ZonedDateTime.parse("2014-03-11T06:23:04.000Z")
    ));
    assertEquals(0, appList.size());
    // find outside period, on initial year
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2014-03-10T06:23:04.000Z"),
        ZonedDateTime.parse("2015-03-11T06:07:08.000Z")
    ));
    assertEquals(0, appList.size());
    // find within period, but after end year
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2021-03-10T06:23:04.000Z"),
        ZonedDateTime.parse("2021-03-11T06:23:04.000Z")
    ));
    assertEquals(0, appList.size());
    // find within period, after initial year
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2019-03-10T06:23:04.000Z"),
        ZonedDateTime.parse("2019-03-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    // find within period, on the final year
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2020-03-10T06:23:04.000Z"),
        ZonedDateTime.parse("2020-03-11T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    // find outside period, on the final year
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2020-04-06T06:23:04.000Z"),
        ZonedDateTime.parse("2020-04-07T06:23:04.000Z")
    ));
    assertEquals(0, appList.size());
    // find within period, no end time
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2016-04-04T06:23:04.000Z"),
        null
    ));
    assertEquals(1, appList.size());
    // find within recurring period, no end time
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2017-05-04T06:23:04.000Z"),
        null
    ));
    assertEquals(1, appList.size());
    // find outside recurring period, no end time
    appList = applicationSearchService.findByField(createRecurringQuery(
        ZonedDateTime.parse("2021-01-04T06:23:04.000Z"),
        null
    ));
    assertEquals(0, appList.size());

    // find within period, no start time
    appList = applicationSearchService.findByField(createRecurringQuery(
        null,
        ZonedDateTime.parse("2015-12-04T06:23:04.000Z")
    ));
    assertEquals(1, appList.size());
    // find outside recurring period, no start time
    appList = applicationSearchService.findByField(createRecurringQuery(
        null,
        ZonedDateTime.parse("2015-05-04T06:23:04.000Z")
    ));
    assertEquals(0, appList.size());

    applicationSearchService.delete("100");
  }

  public static QueryParameters createRecurringQuery(ZonedDateTime begin, ZonedDateTime end) {
    QueryParameter recurringQP = new QueryParameter(QueryParameter.FIELD_NAME_RECURRING_APPLICATION, begin, end);
    QueryParameters params = new QueryParameters();
    params.setQueryParameters(Collections.singletonList(recurringQP));
    return params;
  }

  public static ApplicationES createApplication(Integer id) {
    ApplicationES applicationES = new ApplicationES();
    applicationES.setType(new ApplicationTypeES(ApplicationType.EVENT));
    applicationES.setId(id);
    applicationES.setApplicationId("TP000001");
    applicationES.setHandler(createUser());
    applicationES.setName("Mock testi");
    applicationES.setStatus(new StatusTypeES(StatusType.PENDING));
    ZonedDateTime dateTime = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
    applicationES.setCreationTime(dateTime.toInstant().toEpochMilli());

    applicationES.setApplicationTypeData(createApplicationTypeData());
    return applicationES;
  }

  public static List<ESFlatValue> createApplicationTypeData() {
    List<ESFlatValue> esFlatValues = new ArrayList<>();
    ZonedDateTime zonedDateTimeStart = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
    ZonedDateTime zonedDateTimeEnd = ZonedDateTime.parse("2016-07-06T06:23:04.000Z");

    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "startTime", zonedDateTimeStart.toString()));
    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "endTime", zonedDateTimeEnd.toString()));
    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "attendees", 1000L));
    esFlatValues.add(new ESFlatValue(ApplicationKind.OUTDOOREVENT.name(), "description", "Ulkoilmatapahtuman selitettä tässä."));
    return esFlatValues;
  }

  public static List<ContactES> createContacts() {
    ArrayList<ContactES> contacts = new ArrayList<>();
    contacts.add(new ContactES(1, "kontakti ihminen", true));
    contacts.add(new ContactES(2, "toinen contact", true));
    return contacts;
  }

  public static UserES createUser() {
    return new UserES(USERNAME, "real name");
  }
}
