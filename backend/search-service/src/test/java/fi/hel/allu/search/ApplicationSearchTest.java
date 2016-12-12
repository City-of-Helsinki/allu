package fi.hel.allu.search;

import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.ApplicationSearchService;
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
  private ApplicationSearchService applicationSearchService;
  private GenericSearchService genericSearchService;


  @Before
  public void setUp() throws Exception {
    ElasticSearchMappingConfig elasticSearchMappingConfig = SearchTestUtil.searchIndexSetup(client);
    genericSearchService = new GenericSearchService(elasticSearchMappingConfig, client);
    applicationSearchService = new ApplicationSearchService(genericSearchService, client);
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

    applicationSearchService.insertApplication(applicationES);
  }

  @Test
  public void testFindByField() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);

    QueryParameters params = SearchTestUtil.createQueryParameters("name", "testi");
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testFindByFieldSorted() {
    ApplicationES applicationES1 = createApplication(1);
    applicationES1.setName("c");
    ApplicationES applicationES2 = createApplication(2);
    applicationES2.setName("a");
    ApplicationES applicationES3 = createApplication(3);
    applicationES3.setName("b");
    applicationSearchService.insertApplication(applicationES1);
    applicationSearchService.insertApplication(applicationES2);
    applicationSearchService.insertApplication(applicationES3);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("handler.userName", Arrays.asList("notexisting1", USERNAME, "notexisting2"));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    params.setSort(new QueryParameters.Sort("name", QueryParameters.Sort.Direction.ASC));
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(3, appList.size());
    assertEquals(2, appList.get(0).intValue());
    assertEquals(3, appList.get(1).intValue());
    assertEquals(1, appList.get(2).intValue());
    applicationSearchService.deleteApplication("1");
    applicationSearchService.deleteApplication("2");
    applicationSearchService.deleteApplication("3");
  }

  @Test
  public void testFindByMultipleTypesSorted() {
    ApplicationES applicationES1 = createApplication(1);
    ApplicationES applicationES2 = createApplication(2);
    ApplicationES applicationES3 = createApplication(3);

    applicationES1.setStatus(new StatusTypeES(StatusType.FINISHED));
    applicationES2.setStatus(new StatusTypeES(StatusType.HANDLING));
    applicationES3.setStatus(new StatusTypeES(StatusType.DECISION));

    applicationSearchService.insertApplication(applicationES1);
    applicationSearchService.insertApplication(applicationES2);
    applicationSearchService.insertApplication(applicationES3);

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
    applicationSearchService.deleteApplication("1");
    applicationSearchService.deleteApplication("2");
    applicationSearchService.deleteApplication("3");
  }

  @Test
  public void testFindByContact() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);

    QueryParameters params = SearchTestUtil.createQueryParameters("contacts.name", "kontakti");
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testFindByMultipleHandlers() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("handler.userName", Arrays.asList("notexisting1", USERNAME, "notexisting2"));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testFindByMultipleStatuses() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter("status.value", Arrays.asList(StatusType.PENDING.name(), StatusType.CANCELLED.name()));
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    List<Integer> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testFindByDateField() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);
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

    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testUpdateApplication() {
    ApplicationES applicationES = createApplication(100);
    applicationSearchService.insertApplication(applicationES);

    final String newName = "Päivitetty testi";
    applicationES.setName(newName);

    applicationSearchService.updateApplications(Collections.singletonList(applicationES));
    applicationSearchService.refreshIndex();

    QueryParameters params = SearchTestUtil.createQueryParameters("name", newName);
    List<Integer> appList = applicationSearchService.findByField(params);
    assertEquals(1, appList.size());
    applicationSearchService.deleteApplication("100");
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
    contacts.add(new ContactES("kontakti ihminen"));
    contacts.add(new ContactES("toinen contact"));
    return contacts;
  }

  public static UserES createUser() {
    return new UserES(USERNAME, "real name");
  }
}
