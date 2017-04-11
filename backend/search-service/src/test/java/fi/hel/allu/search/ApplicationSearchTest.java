package fi.hel.allu.search;

import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.GenericSearchService;
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
  private GenericSearchService genericSearchService;


  @Before
  public void setUp() throws Exception {
    ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
    genericSearchService = new GenericSearchService(
        elasticSearchMappingConfig,
        client,
        ElasticSearchMappingConfig.APPLICATION_INDEX_NAME,
        ElasticSearchMappingConfig.APPLICATION_TYPE_NAME);
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

    genericSearchService.insert(applicationES.getId().toString(), applicationES);
  }

  @Test
  public void testFindByField() {
    ApplicationES applicationES = createApplication(1);
    genericSearchService.insert(applicationES.getId().toString(), applicationES);

    QueryParameters params = SearchTestUtil.createQueryParameters("name", "testi");
    genericSearchService.refreshIndex();
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    genericSearchService.delete("1");
  }

  @Test
  public void testFindByFieldPartial() {
    ApplicationES applicationES = createApplication(1);
    genericSearchService.insert(applicationES.getId().toString(), applicationES);

    QueryParameters params = SearchTestUtil.createQueryParameters("applicationId", "TP00");
    genericSearchService.refreshIndex();
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    genericSearchService.delete("1");
  }

  @Test
  public void testFindByFieldSorted() {
    ApplicationES applicationES1 = createApplication(1);
    applicationES1.setName("c 3");
    ApplicationES applicationES2 = createApplication(2);
    applicationES2.setName("a 2");
    ApplicationES applicationES3 = createApplication(3);
    applicationES3.setName("b 1");
    genericSearchService.insert(applicationES1.getId().toString(), applicationES1);
    genericSearchService.insert(applicationES2.getId().toString(), applicationES2);
    genericSearchService.insert(applicationES3.getId().toString(), applicationES3);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("handler.userName", Arrays.asList("notexisting1", USERNAME, "notexisting2"));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));
    genericSearchService.refreshIndex();
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(3, appList.size());
    assertEquals(2, appList.get(0).intValue());
    assertEquals(3, appList.get(1).intValue());
    assertEquals(1, appList.get(2).intValue());
    genericSearchService.delete("1");
    genericSearchService.delete("2");
    genericSearchService.delete("3");
  }

  @Test
  public void testFieldMappingsAndSorting() {
    ApplicationES applicationES1 = createApplication(1);

    applicationES1.setName(USERNAME + " " + 1);
    applicationES1.setHandler(new UserES(USERNAME + " " + 1, "not used"));
    applicationES1.setApplicant(new ApplicantES());
    applicationES1.getApplicant().setName(USERNAME + " " + 1);
    applicationES1.setLocations(Arrays.asList(
        new LocationES("AEnsimmäinen osoite 9", "00100", "Sinki", 1),
        new LocationES("Zviimonen 777", "00100", "Sinki", 5)));

    ApplicationES applicationES2 = createApplication(2);
    applicationES2.setName(USERNAME + " " + 2);
    applicationES2.setHandler(new UserES(USERNAME + " " + 2, "not used"));
    applicationES2.setApplicant(new ApplicantES());
    applicationES2.getApplicant().setName(USERNAME + " " + 2);
    applicationES2.setLocations(Collections.singletonList(new LocationES("bToinen osoite 1", "00100", "Sinki", 2)));

    ApplicationES applicationES3 = createApplication(3);
    applicationES3.setName(USERNAME + " " + 3);
    applicationES3.setHandler(new UserES(USERNAME + " " + 3, "not used"));
    applicationES3.setApplicant(new ApplicantES());
    applicationES3.getApplicant().setName(USERNAME + " " + 3);
    applicationES3.setLocations(Arrays.asList(
        new LocationES("Zviimonen 777", "00100", "Sinki", 3),
        new LocationES("Ckolmas osoite 5", "00100", "Sinki", 4)));

    genericSearchService.insert(applicationES1.getId().toString(), applicationES1);
    genericSearchService.insert(applicationES2.getId().toString(), applicationES2);
    genericSearchService.insert(applicationES3.getId().toString(), applicationES3);
    genericSearchService.refreshIndex();

    QueryParameters params = new QueryParameters();
    QueryParameter nameParameter = new QueryParameter("name", USERNAME);
    QueryParameter handlerNameParameter = new QueryParameter("handler.userName", Arrays.asList(USERNAME));
    QueryParameter applicantNameParameter = new QueryParameter("applicant.name", Arrays.asList(USERNAME));

    List<QueryParameter> parameterList = new ArrayList<>(Arrays.asList(nameParameter, handlerNameParameter, applicantNameParameter));
    params.setQueryParameters(parameterList);
    params.setSort(new QueryParameters.Sort("name.alphasort", QueryParameters.Sort.Direction.ASC));
    List<Integer> appList = genericSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("handler.userName.alphasort", QueryParameters.Sort.Direction.ASC));
    appList = genericSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("applicant.name.alphasort", QueryParameters.Sort.Direction.ASC));
    appList = genericSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("locations.streetAddress.alphasort", QueryParameters.Sort.Direction.ASC));
    appList = genericSearchService.findByField(params);
    assertEquals(3, appList.size());
    assertEquals(Arrays.asList(1, 2, 3), appList);

    params.setSort(new QueryParameters.Sort("locations.cityDistrictId", QueryParameters.Sort.Direction.ASC));
    appList = genericSearchService.findByField(params);
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

    genericSearchService.insert(applicationES1.getId().toString(), applicationES1);
    genericSearchService.insert(applicationES2.getId().toString(), applicationES2);
    genericSearchService.insert(applicationES3.getId().toString(), applicationES3);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter(
        "status.value", Arrays.asList(StatusType.FINISHED.name(), StatusType.HANDLING.name(), StatusType.DECISION.name()));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    params.setSort(new QueryParameters.Sort("status.ordinal", QueryParameters.Sort.Direction.ASC));
    genericSearchService.refreshIndex();
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(3, appList.size());
    // results should be sorted by the enumeration order of StatusType
    assertEquals(2, appList.get(0).intValue());
    assertEquals(3, appList.get(1).intValue());
    assertEquals(1, appList.get(2).intValue());
    genericSearchService.delete("1");
    genericSearchService.delete("2");
    genericSearchService.delete("3");
  }

  @Test
  public void testFindByContact() {
    ApplicationES applicationES = createApplication(1);
    genericSearchService.insert(applicationES.getId().toString(), applicationES);

    QueryParameters params = SearchTestUtil.createQueryParameters("contacts.name", "kontakti");
    genericSearchService.refreshIndex();
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    genericSearchService.delete("1");
  }

  @Test
  public void testFindByMultipleHandlers() {
    ApplicationES applicationES = createApplication(1);
    genericSearchService.insert(applicationES.getId().toString(), applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("handler.userName", Arrays.asList("notexisting1", USERNAME, "notexisting2"));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    genericSearchService.refreshIndex();
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    genericSearchService.delete("1");
  }

  @Test
  public void testFindByMultipleStatuses() {
    ApplicationES applicationES = createApplication(1);
    genericSearchService.insert(applicationES.getId().toString(), applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("status.value", Arrays.asList(StatusType.PENDING.name(), StatusType.CANCELLED.name()));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    genericSearchService.refreshIndex();
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    genericSearchService.delete("1");
  }

  @Test
  public void testFindByDateField() {
    ApplicationES applicationES = createApplication(1);
    genericSearchService.insert(applicationES.getId().toString(), applicationES);
    genericSearchService.refreshIndex();

    QueryParameters params = new QueryParameters();
    ZonedDateTime testStartTime = ZonedDateTime.parse("2016-07-05T06:10:10.000Z");
    ZonedDateTime testEndTime = ZonedDateTime.parse(  "2016-07-06T05:10:10.000Z");
    QueryParameter parameter = new QueryParameter("creationTime", testStartTime, testEndTime);
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    List<Integer> appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());

    testStartTime = ZonedDateTime.parse("2016-07-03T06:10:10.000Z");
    testEndTime = ZonedDateTime.parse(  "2016-07-04T05:10:10.000Z");
    parameter = new QueryParameter("creationTime", testStartTime, testEndTime);
    parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    appList = genericSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(0, appList.size());

    genericSearchService.delete("1");
  }

  @Test
  public void testUpdateApplication() {
    ApplicationES applicationES = createApplication(100);
    genericSearchService.insert(applicationES.getId().toString(), applicationES);

    final String newName = "Päivitetty testi";
    applicationES.setName(newName);

    genericSearchService.bulkUpdate(Collections.singletonMap(applicationES.getId().toString(), applicationES));
    genericSearchService.refreshIndex();

    QueryParameters params = SearchTestUtil.createQueryParameters("name", newName);
    List<Integer> appList = genericSearchService.findByField(params);
    assertEquals(1, appList.size());
    genericSearchService.delete("100");
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
    applicationES.setCreationTime(dateTime);
    applicationES.setContacts(createContacts());

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
    contacts.add(new ContactES(1, "kontakti ihminen"));
    contacts.add(new ContactES(2, "toinen contact"));
    return contacts;
  }

  public static UserES createUser() {
    return new UserES(USERNAME, "real name");
  }
}
