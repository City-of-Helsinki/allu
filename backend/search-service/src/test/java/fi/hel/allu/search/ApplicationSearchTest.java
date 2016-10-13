package fi.hel.allu.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.search.config.ElasticSearchMappingConfig;
import fi.hel.allu.search.domain.*;
import fi.hel.allu.search.service.ApplicationSearchService;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.IndexNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_INDEX_NAME;
import static fi.hel.allu.search.config.ElasticSearchMappingConfig.APPLICATION_TYPE_NAME;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ApplicationSearchTest {
  @Autowired
  private Client client;
  private ApplicationSearchService applicationSearchService;


  @Before
  public void setUp() throws Exception {

    XContentBuilder mappingBuilder = new ElasticSearchMappingConfig(null).getMappingBuilder();

      try {
        DeleteIndexResponse response = client.admin().indices().delete(new DeleteIndexRequest(APPLICATION_INDEX_NAME)).actionGet();
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

    applicationSearchService = new ApplicationSearchService(null, client, new ObjectMapper());
  }

  @Test
  public void testInsertApplication() {
    ApplicationES applicationES = new ApplicationES();
    applicationES.setType(ApplicationType.OUTDOOREVENT);
    applicationES.setId(1);
    applicationES.setHandler(createUser());
    applicationES.setName("Ensimmäinen testi");
    applicationES.setStatus(StatusType.PENDING);
    applicationES.setApplicationTypeData(createApplicationTypeData());

    applicationSearchService.insertApplication(applicationES);
  }

  @Test
  public void testFindByField() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter();
    parameter.setFieldName("name");
    parameter.setFieldValue("testi");
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    List<ApplicationES> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testFindByContact() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter();
    parameter.setFieldName("contacts.name");
    parameter.setFieldValue("kontakti");
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    List<ApplicationES> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());
    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testFindByDateField() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);

    QueryParameters params = new QueryParameters();
    QueryParameter parameter = new QueryParameter();
    ZonedDateTime testStartTime = ZonedDateTime.parse("2016-07-05T06:10:10.000Z");
    ZonedDateTime testEndTime = ZonedDateTime.parse(  "2016-07-06T05:10:10.000Z");
    parameter.setFieldName("creationTime");
    parameter.setStartDateValue(testStartTime);
    parameter.setEndDateValue(testEndTime);
    List<QueryParameter> parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    List<ApplicationES> appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(1, appList.size());

    testStartTime = ZonedDateTime.parse("2016-07-03T06:10:10.000Z");
    testEndTime = ZonedDateTime.parse(  "2016-07-04T05:10:10.000Z");
    parameter.setFieldName("creationTime");
    parameter.setStartDateValue(testStartTime);
    parameter.setEndDateValue(testEndTime);
    parameterList = new ArrayList<>();
    parameterList.add(parameter);
    params.setQueryParameters(parameterList);
    applicationSearchService.refreshIndex();
    appList = applicationSearchService.findByField(params);
    assertNotNull(appList);
    assertEquals(0, appList.size());

    applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testUpdateApplication() {
    ApplicationES applicationES = createApplication(100);
    applicationSearchService.insertApplication(applicationES);

    applicationES.setName("Päivitetty testi");

    applicationSearchService.updateApplication("100", applicationES);

    ApplicationES updated = applicationSearchService.findById("100");

    applicationSearchService.deleteApplication("100");

    assertNotNull(updated);
    assertEquals("Päivitetty testi", updated.getName());
  }

  @Test
  public void testFindFromAllFields() {
    ApplicationES applicationES = createApplication(1);
    applicationSearchService.insertApplication(applicationES);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      fail();
    }

    List<ApplicationES> appList = applicationSearchService.findFromAllFields("*apah*");
    assertNotNull(appList);
    assertEquals(1, appList.size());
  }


  private ApplicationES createApplication(Integer id) {
    ApplicationES applicationES = new ApplicationES();
    applicationES.setType(ApplicationType.OUTDOOREVENT);
    applicationES.setId(id);
    applicationES.setHandler(createUser());
    applicationES.setName("Mock testi");
    applicationES.setStatus(StatusType.PENDING);
    ZonedDateTime dateTime = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
    applicationES.setCreationTime(dateTime);
    applicationES.setContacts(createContacts());

    applicationES.setApplicationTypeData(createApplicationTypeData());
    return applicationES;
  }

  private List<ESFlatValue> createApplicationTypeData() {
    List<ESFlatValue> esFlatValues = new ArrayList<>();
    ZonedDateTime zonedDateTimeStart = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
    ZonedDateTime zonedDateTimeEnd = ZonedDateTime.parse("2016-07-06T06:23:04.000Z");

    esFlatValues.add(new ESFlatValue(ApplicationType.OUTDOOREVENT.name(), "startTime", zonedDateTimeStart.toString()));
    esFlatValues.add(new ESFlatValue(ApplicationType.OUTDOOREVENT.name(), "endTime", zonedDateTimeEnd.toString()));
    esFlatValues.add(new ESFlatValue(ApplicationType.OUTDOOREVENT.name(), "attendees", 1000L));
    esFlatValues.add(new ESFlatValue(ApplicationType.OUTDOOREVENT.name(), "description", "Ulkoilmatapahtuman selitettä tässä."));
    return esFlatValues;
  }

  private List<ContactES> createContacts() {
    ArrayList<ContactES> contacts = new ArrayList<>();
    contacts.add(new ContactES("kontakti ihminen"));
    contacts.add(new ContactES("toinen contact"));
    return contacts;
  }

  private UserES createUser() {
    return new UserES("user name", "real name");
  }
}
