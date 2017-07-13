package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationControllerTest {

  @Autowired
  WebTestCommon wtc;

  @Autowired
  TestCommon testCommon;

  @Autowired
  LocationService locationService;

  @Before
  public void setup() throws Exception {
    wtc.setupNoDelete();
  }

  @Test
  public void testAddApplication() throws Exception {
    Application app = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
    wtc.perform(post("/applications"), app).andExpect(status().isOk());
  }

  @Test
  public void testAddApplicationWithId() throws Exception {
    Application app = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
    app.setId(123);
    wtc.perform(post("/applications"), app).andExpect(status().isBadRequest());
  }

  @Test
  public void testAddApplicationWithBadProject() throws Exception {
    Application app = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
    app.setProjectId(app.getProjectId() + 1);
    wtc.perform(post("/applications"), app).andExpect(status().isBadRequest());
  }

  @Test
  public void testFindNonexistent() throws Exception {
    wtc.perform(get("/applications/123")).andExpect(status().isNotFound());

    ResultActions resultActions = wtc.perform(post("/applications/find"), Collections.singletonList(123))
        .andExpect(status().isOk());
    Application[] appsOut = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(0, appsOut.length);
  }

  @Test
  public void testFindExistingOutdoor() throws Exception {
    // Setup: insert an application
    Application appIn = testCommon.dummyOutdoorApplication("Test Application", "Handler");
    Application appInResult = insertApplication(appIn);
    // Test: try to read the same application back
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application appOut = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(StatusType.PENDING, appOut.getStatus());
    // Test reading the application back with the interface supporting multiple ids
    resultActions = wtc.perform(post("/applications/find"), Collections.singletonList(appInResult.getId()))
        .andExpect(status().isOk());
    Application[] appsOut = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(StatusType.PENDING, appsOut[0].getStatus());
  }

  @Test
  public void testFindExistingShortTimeRental() throws Exception {
    // Setup: insert an application
    Application appIn = testCommon.dummyBridgeBannerApplication("Test Application", "Handler");
    Application appInResult = insertApplication(appIn);
    // Test: try to read the same application back
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application appOut = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(StatusType.PENDING, appOut.getStatus());
    assertNotNull(appOut.getExtension());
  }

  @Test
  public void testUpdateExisting() throws Exception {
    // Setup: insert an application
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Handler"));
    // Test: try to update the application
    appInResult.setStatus(StatusType.HANDLING);
    appInResult.setName("updatedname");
    ResultActions resultActions = wtc.perform(put(String.format("/applications/%d", appInResult.getId())), appInResult)
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(StatusType.PENDING, updateResult.getStatus());
    assertEquals(appInResult.getName(), updateResult.getName());
  }

  @Test
  public void testUpdateHandler() throws Exception {
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Handler"));
    User changedUser = testCommon.insertUser("changed");
    appInResult.setHandler(changedUser.getId());
    wtc.perform(put(String.format("/applications/handler/%d", appInResult.getHandler())), Collections.singletonList(appInResult.getId()))
        .andExpect(status().isOk());
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(changedUser.getId(), updateResult.getHandler());
  }

  @Test
  public void testRemoveHandler() throws Exception {
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Handler"));
    wtc.perform(put(String.format("/applications/handler/remove")), Collections.singletonList(appInResult.getId()))
        .andExpect(status().isOk());
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertNull(updateResult.getHandler());
  }

  @Test
  public void testReplaceDistributionList() throws Exception {
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Handler"));
    final String testEmail = "testi@testi.fi";
    DistributionEntry distributionEntry = new DistributionEntry();
    distributionEntry.setEmail(testEmail);
    distributionEntry.setDistributionType(DistributionType.EMAIL);
    wtc.perform(post(
        String.format("/applications/%d/decision-distribution-list", appInResult.getId())),
        Collections.singletonList(distributionEntry))
        .andExpect(status().isOk());
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(1, updateResult.getDecisionDistributionList().size());
    assertEquals(testEmail, updateResult.getDecisionDistributionList().get(0).getEmail());
  }

  @Test
  public void updateNonexistent() throws Exception {
    Application app = testCommon.dummyOutdoorApplication("Test Application", "Hanskaaja");
    wtc.perform(put("/applications/314159"), app).andExpect(status().isNotFound());
  }

  @Test
  public void testFindIntersecting() throws Exception {
    testCommon.deleteAllData();
    createLocationTestApplications();
    LocationSearchCriteria lsc = new LocationSearchCriteria();
    lsc.setIntersects(bigArea);
    ResultActions resultActions = wtc.perform(post("/applications/search"), lsc).andExpect(status().isOk());
    Application[] results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(3, results.length);
  }

  @Test
  public void testFindIntersectingWithTime() throws Exception {
    createLocationTestApplications();
    LocationSearchCriteria lsc = new LocationSearchCriteria();
    lsc.setIntersects(bigArea);
    lsc.setAfter(ZonedDateTime.parse("2016-11-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2016-11-03T08:00:00+02:00[Europe/Helsinki]"));
    ResultActions resultActions = wtc.perform(post("/applications/search"), lsc).andExpect(status().isOk());
    Application[] results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(1, results.length);
    assertEquals("Small application 1", results[0].getName());
  }

  /**
   * Test that reading an application's attachment list works
   *
   * @throws Exception
   */
  @Test
  public void testFindAttachments() throws Exception {
    // Setup: insert an application
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Handler"));
    // Test: read the application's attachment list
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d/attachments", appInResult.getId())))
        .andExpect(status().isOk());
    AttachmentInfo[] results = wtc.parseObjectFromResult(resultActions, AttachmentInfo[].class);
    assertEquals(0, results.length);
  }

  /**
   * Test that location can be deleted from application
   *
   * @throws Exception
   */
  @Test
  public void testDeleteApplicationLocation() throws Exception {
    // Setup: create application with location
    Application app = createLocationTestApplication(testAppParams[0], "Syrjäkuja 5", "Hämärähomma", 1);
    ResultActions ra = wtc.perform(get(String.format("/locations/application/%d", app.getId()))).andExpect(status().isOk());
    Location[] locations = wtc.parseObjectFromResult(ra, Location[].class);
    assertEquals(1, locations.length);
    // Test: delete the application's location and verify that it gets deleted.
    Integer appId = app.getId();
    wtc.perform(delete(String.format("/locations/application/%d", appId))).andExpect(status().isOk());
    ra = wtc.perform(get(String.format("/locations/application/%d", app.getId()))).andExpect(status().isOk());
    locations = wtc.parseObjectFromResult(ra, Location[].class);
    assertEquals(0, locations.length);
  }

  /**
   * Test that application price calculation works.
   */
  @Test
  public void testCalculateEventApplicationPrice() throws Exception {
    // Setup: create application with location and enough information for calculating price
    Customer eventCustomer = testCommon.insertPerson();
    Application newApplication = new Application();
    newApplication.setType(ApplicationType.EVENT);
    newApplication.setKind(ApplicationKind.OUTDOOREVENT);
    newApplication.setName("test outdoor event");
    newApplication.setCustomersWithContacts(
        Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, eventCustomer, Collections.emptyList())));
    // TODO: remove these two lines setStartTime and setEndTime, because they should get set automatically from location
    newApplication.setStartTime(ZonedDateTime.parse("2017-02-01T00:00:01+02:00[Europe/Helsinki]"));
    newApplication.setEndTime(ZonedDateTime.parse("2017-02-08T00:00:01+02:00[Europe/Helsinki]"));
    newApplication.setRecurringEndTime(ZonedDateTime.parse("2017-02-08T00:00:01+02:00[Europe/Helsinki]"));
    newApplication.setMetadataVersion(1);
    Event event = new Event();
    event.setDescription("Eventti");
    event.setEcoCompass(true);
    event.setNature(EventNature.CLOSED);
    newApplication.setExtension(event);
    Geometry geometry = polygon(3879,
        ring(c(25492000, 6675000), c(25492500, 6675000), c(25492100, 6675100), c(25492000, 6675000)));
    Application application =
        insertApplicationWithGeometry(
            newApplication,
            new GeometryCollection(new Geometry[] { geometry }),
            "Mannerheimintie 1",
            ZonedDateTime.parse("2017-02-01T00:00:01+02:00[Europe/Helsinki]"),
            ZonedDateTime.parse("2017-02-08T00:00:01+02:00[Europe/Helsinki]"));

    // read application back from database and check the calculated price
    ResultActions ra = wtc.perform(get(String.format("/applications/%d", application.getId()))).andExpect(status().isOk());
    application = wtc.parseObjectFromResult(ra, Application.class);
    int expectedPrice = 28140000;
    assertEquals(expectedPrice, (int) application.getCalculatedPrice());
  }

  @Test
  public void testRecurringWithinCalendarYear() throws Exception {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    newApplication.setStartTime(ZonedDateTime.parse("2015-06-03T10:15:30+02:00"));
    newApplication.setEndTime(ZonedDateTime.parse("2015-08-03T10:15:30+02:00"));
    newApplication.setRecurringEndTime(ZonedDateTime.parse("2020-08-03T10:15:30+02:00"));
    Geometry geometry = polygon(3879, ring(c(25480000, 6672000), c(25491000, 6672000), c(25485000, 6670000), c(25480000, 6672000)));
    GeometryCollection geometryCollection = new GeometryCollection(new Geometry[] { geometry });
    insertApplicationWithGeometry(
        newApplication,
        geometryCollection,
        "katu 1",
        newApplication.getStartTime(),
        newApplication.getEndTime());

    LocationSearchCriteria lsc = new LocationSearchCriteria();
    // test period completely outside recurring period, before recurring period
    lsc.setIntersects(geometry);
    lsc.setAfter(ZonedDateTime.parse("2015-01-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-05-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 0);
    // test period completely outside recurring period, after recurring period
    lsc = new LocationSearchCriteria();
    lsc.setIntersects(geometry);
    lsc.setAfter(ZonedDateTime.parse("2015-09-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-10-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 0);
    // test period completely within recurring period
    lsc.setAfter(ZonedDateTime.parse("2015-07-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-07-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period partially within recurring period
    lsc.setAfter(ZonedDateTime.parse("2015-08-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-09-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period partially within recurring period (beginning of test period) and that overlaps with two calendar years.
    lsc.setAfter(ZonedDateTime.parse("2014-01-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-06-04T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period partially within recurring period (end of test period) and that overlaps with two calendar years
    lsc.setAfter(ZonedDateTime.parse("2015-08-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2017-06-04T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period within recurring period, year after first year
    lsc.setAfter(ZonedDateTime.parse("2016-07-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2016-07-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period partially within recurring period, year after first year
    lsc.setAfter(ZonedDateTime.parse("2016-07-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2016-07-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test open period, no end defined
    lsc.setAfter(ZonedDateTime.parse("2015-04-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(null);
    testRecurring(lsc, 1);
    lsc.setAfter(ZonedDateTime.parse("2016-04-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(null);
    testRecurring(lsc, 1);
    lsc.setAfter(ZonedDateTime.parse("2020-10-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(null);
    testRecurring(lsc, 0);
    // test open period, no begin defined
    lsc.setAfter(null);
    lsc.setBefore(ZonedDateTime.parse("2015-04-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 0);
    lsc.setAfter(null);
    lsc.setBefore(ZonedDateTime.parse("2015-07-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    lsc.setAfter(null);
    lsc.setBefore(ZonedDateTime.parse("2030-04-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
  }

  @Test
  public void testRecurringWithinTwoCalendarYears() throws Exception {

    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    newApplication.setStartTime(ZonedDateTime.parse("2015-06-03T10:15:30+02:00"));
    newApplication.setEndTime(ZonedDateTime.parse("2016-03-03T10:15:30+02:00"));
    newApplication.setRecurringEndTime(ZonedDateTime.parse("2020-03-03T10:15:30+02:00"));
    Geometry geometry = polygon(3879, ring(c(25480000, 6672000), c(25491000, 6672000), c(25485000, 6670000), c(25480000, 6672000)));
    GeometryCollection geometryCollection = new GeometryCollection(new Geometry[] { geometry });
    insertApplicationWithGeometry(
        newApplication,
        geometryCollection,
        "katu 1",
        newApplication.getStartTime(),
        newApplication.getEndTime());

    LocationSearchCriteria lsc = new LocationSearchCriteria();
    lsc.setIntersects(geometry);
    // test period completely outside recurring period
    lsc.setAfter(ZonedDateTime.parse("2015-01-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-05-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 0);
    // test period completely within recurring period, in the first period
    lsc.setAfter(ZonedDateTime.parse("2015-06-04T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-07-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period completely within recurring period, in the second period
    lsc.setAfter(ZonedDateTime.parse("2016-01-04T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2016-02-03T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period longer than one year, match in the end of long period
    lsc.setAfter(ZonedDateTime.parse("2012-01-04T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2015-06-04T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
    // test period longer than one year, match in the beginning of long period
    lsc.setAfter(ZonedDateTime.parse("2020-03-02T08:00:00+02:00[Europe/Helsinki]"));
    lsc.setBefore(ZonedDateTime.parse("2025-03-02T08:00:00+02:00[Europe/Helsinki]"));
    testRecurring(lsc, 1);
  }

  private void testRecurring(LocationSearchCriteria lsc, int matchCount) throws Exception {
    ResultActions resultActions = wtc.perform(post("/applications/search"), lsc).andExpect(status().isOk());
    Application[] results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(matchCount, results.length);
  }

  private static Geometry bigArea = polygon(3879, ring(c(25490000, 6670000), c(25500000, 6670000), c(25500000, 6675000),
      c(25490000, 6675000), c(25490000, 6670000)));

  private static class TestAppParam {
    public Geometry geometry;
    public ZonedDateTime startTime;
    public ZonedDateTime endTime;

    public TestAppParam(Geometry geo, ZonedDateTime st, ZonedDateTime et) {
      geometry = geo;
      startTime = st;
      endTime = et;
    }
  }

  private static TestAppParam T(Geometry geo, ZonedDateTime st, ZonedDateTime et) {
    return new TestAppParam(geo, st, et);
  }

  private static TestAppParam[] testAppParams = {
      // completely inside, Dec. 2016:
      T(polygon(3879,
          ring(c(25492000, 6675000), c(25492500, 6675000), c(25492100, 6675100), c(25492000, 6675000))),
        ZonedDateTime.parse("2016-12-03T10:15:30+02:00[Europe/Helsinki]"),
        ZonedDateTime.parse("2016-12-08T10:15:30+02:00[Europe/Helsinki]")
        ),
      // partially inside: Feb..Dec 2016
      T(polygon(3879,
          ring(c(25480000, 6672000), c(25491000, 6672000), c(25485000, 6670000), c(25480000, 6672000))),
        ZonedDateTime.parse("2016-02-03T10:15:30+02:00[Europe/Helsinki]"),
        ZonedDateTime.parse("2016-12-08T10:15:30+02:00[Europe/Helsinki]")
        ),
      // completely outside, Dec. 2016:
      T(polygon(3879,
          ring(c(25480000, 6672000), c(25485000, 6672000), c(25485000, 6670000), c(25480000, 6672000))),
        ZonedDateTime.parse("2016-12-03T10:15:30+02:00[Europe/Helsinki]"),
        ZonedDateTime.parse("2016-12-08T10:15:30+02:00[Europe/Helsinki]")
        ),
      // completely inside again, Mar 2017:
      T(polygon(3879, ring(c(25495000, 6671000), c(25496000, 6671000), c(25495100, 6671500), c(25495000, 6671000))),
          ZonedDateTime.parse("2017-03-03T10:15:30+02:00[Europe/Helsinki]"),
          ZonedDateTime.parse("2017-03-08T10:15:30+02:00[Europe/Helsinki]")) };

  // Helper to insert an application. Returns the result application.
  private Application insertApplication(Application appIn) throws Exception {
    Integer userId = testCommon.insertUser("dummyUser" + System.currentTimeMillis()).getId();
    ControllerHelper.addDummyCustomer(wtc, appIn, userId);
    ResultActions resultActions = wtc.perform(post("/applications"), appIn).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private Application createLocationTestApplication(
      TestAppParam tap,
      String streetAddress,
      String applicationName,
      int count)
      throws Exception {
    Application app = testCommon.dummyOutdoorApplication(applicationName, "locationUserName" + count);
    return insertApplicationWithGeometry(
        app, new GeometryCollection(new Geometry[] { tap.geometry }), streetAddress, tap.startTime, tap.endTime);
  }

  private Application insertApplicationWithGeometry(
      Application application,
      GeometryCollection geometryCollection,
      String streetAddress,
      ZonedDateTime startTime,
      ZonedDateTime endTime)
      throws Exception {
    Application insertedApp = insertApplication(application);
    testCommon.insertLocation(streetAddress, geometryCollection, insertedApp.getId(), startTime, endTime);
    return insertedApp;
  }

  private void createLocationTestApplications() throws Exception {
    // Create a test application for each of the small areas
    for (int i = 0; i < testAppParams.length; ++i) {
      createLocationTestApplication(
          testAppParams[i],
          String.format("Smallstreet %d", i),
          String.format("Small application %d", i),
          i);
    }
  }


}
