package fi.hel.allu.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.OutdoorEventES;
import fi.hel.allu.search.domain.QueryParameter;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationSearchService;
import org.elasticsearch.client.Client;
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

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppTestConfig.class)
public class ApplicationSearchTest {
  @Autowired
  private Client client;
  private ApplicationSearchService applicationSearchService;

  @Before
  public void setUp() {
    applicationSearchService = new ApplicationSearchService(client, new ObjectMapper());
  }

  @Test
  public void testInsertApplication() {
    try {
      applicationSearchService.deleteIndex();
    } catch (Exception ex) {
      if (ex.getCause() instanceof  IndexNotFoundException) {
        //This is ok, continue
      }
    }

    ApplicationES applicationES = new ApplicationES();
    applicationES.setType(ApplicationType.OUTDOOREVENT);
    applicationES.setId(1);
    applicationES.setHandler("Test");
    applicationES.setName("Ensimmäinen testi");
    applicationES.setStatus(StatusType.PENDING);

    OutdoorEventES outdoorEventES = new OutdoorEventES();
    outdoorEventES.setStartTime(ZonedDateTime.now());
    outdoorEventES.setAttendees(1000);
    outdoorEventES.setDescription("Ulkoilmatapahtuman selitettä tässä.");
    outdoorEventES.setUrl("http://www.event.com");
    outdoorEventES.setNature("Tiukka luonne");

    applicationES.setApplicationTypeData(outdoorEventES);
    applicationSearchService.insertApplication(applicationES);

   // applicationSearchService.deleteApplication("1");
  }

  @Test
  public void testFindByField() {
    try {
      applicationSearchService.deleteIndex();
    } catch (Exception ex) {
      if (ex.getCause() instanceof  IndexNotFoundException) {
        //This is ok, continue
      }
    }
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
  public void testUpdateApplication() {
    try {
      applicationSearchService.deleteIndex();
    } catch (Exception ex) {
      if (ex.getCause() instanceof  IndexNotFoundException) {
        //This is ok, continue
      }
    }

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
    try {
      applicationSearchService.deleteIndex();
    } catch (Exception ex) {
      if (ex.getCause() instanceof  IndexNotFoundException) {
        //This is ok, continue
      }
    }
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
    applicationES.setHandler("Käsittelijä");
    applicationES.setName("Mock testi");
    applicationES.setStatus(StatusType.PENDING);
    ZonedDateTime dateTime = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
    applicationES.setCreationTime(dateTime);

    OutdoorEventES outdoorEventES = new OutdoorEventES();
    outdoorEventES.setAttendees(1000);
    outdoorEventES.setDescription("Ulkoilmatapahtuman kuvaus tässä.");
    outdoorEventES.setUrl("http://www.vincit.fi/");
    outdoorEventES.setNature("Tiukka luonne");
    ZonedDateTime zonedDateTime2 = ZonedDateTime.parse("2016-07-05T06:23:04.000Z");
    ZonedDateTime zonedDateTime3 = ZonedDateTime.parse("2016-07-06T06:23:04.000Z");
    outdoorEventES.setStartTime(zonedDateTime2);
    outdoorEventES.setEndTime(zonedDateTime3);

    applicationES.setApplicationTypeData(outdoorEventES);

    return applicationES;
  }
}
