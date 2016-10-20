package fi.hel.allu.model.controller;

import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.LocationSearchCriteria;
import fi.hel.allu.model.domain.User;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.geolatte.geom.Geometry;
import org.geolatte.geom.GeometryCollection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.geolatte.geom.builder.DSL.c;
import static org.geolatte.geom.builder.DSL.polygon;
import static org.geolatte.geom.builder.DSL.ring;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationControllerTest {

  @Autowired
  WebTestCommon wtc;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  @Test
  public void testAddApplication() throws Exception {
    Application app = testCommon.dummyApplication("Test Application", "Handlaaja");
    wtc.perform(post("/applications"), app).andExpect(status().isOk());
  }

  @Test
  public void testAddApplicationWithId() throws Exception {
    Application app = testCommon.dummyApplication("Test Application", "Handlaaja");
    app.setId(123);
    wtc.perform(post("/applications"), app).andExpect(status().isBadRequest());
  }

  @Test
  public void testAddApplicationWithBadProject() throws Exception {
    Application app = testCommon.dummyApplication("Test Application", "Handlaaja");
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

  // Helper to insert an application. Returns the result application.
  private Application insertApplication(Application appIn) throws Exception {
    ResultActions resultActions = wtc.perform(post("/applications"), appIn).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  @Test
  public void testFindExisting() throws Exception {
    // Setup: insert an application
    Application appIn = testCommon.dummyApplication("Test Application", "Handler");
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
  public void testFindApplicationByProject() throws Exception {
    // Setup: add some applications for one project:
    final int NUM_FIRST = 5;
    final int NUM_SECOND = 7;
    Application app1 = testCommon.dummyApplication("TestAppOne", "Sinikka");
    for (int i = 0; i < NUM_FIRST; ++i) {
      wtc.perform(post("/applications"), app1).andExpect(status().isOk());
    }
    // Now prepare another application -- will get another project ID:
    Application app2 = testCommon.dummyApplication("TestAppTwo", "Keijo");
    assertNotEquals(app1.getProjectId(), app2.getProjectId());
    for (int i = 0; i < NUM_SECOND; ++i) {
      wtc.perform(post("/applications"), app2).andExpect(status().isOk());
    }
    // Now get applications for the first project:
    ResultActions resultActions = wtc.perform(get(String.format("/applications/byproject/%d", app1.getProjectId())))
        .andExpect(status().isOk());
    Application[] results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(NUM_FIRST, results.length);
    // Try also with nonexistent project id:
    resultActions = wtc
        .perform(get(String.format("/applications/byproject/%d", app1.getProjectId() + app2.getProjectId())))
        .andExpect(status().isOk());
    results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(0, results.length);
  }

  @Test
  public void testUpdateExisting() throws Exception {
    // Setup: insert an application
    Application appInResult = insertApplication(testCommon.dummyApplication("Test Application", "Handler"));
    // Test: try to update the application
    appInResult.setStatus(StatusType.HANDLING);
    ResultActions resultActions = wtc.perform(put(String.format("/applications/%d", appInResult.getId())), appInResult)
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(StatusType.HANDLING, updateResult.getStatus());
  }

  @Test
  public void testUpdateHandler() throws Exception {
    Application appInResult = insertApplication(testCommon.dummyApplication("Test Application", "Handler"));
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
    Application appInResult = insertApplication(testCommon.dummyApplication("Test Application", "Handler"));
    wtc.perform(put(String.format("/applications/handler/remove")), Collections.singletonList(appInResult.getId()))
        .andExpect(status().isOk());
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertNull(updateResult.getHandler());
  }

  @Test
  public void updateNonexistent() throws Exception {
    Application app = testCommon.dummyApplication("Test Application", "Hanskaaja");
    wtc.perform(put("/applications/314159"), app).andExpect(status().isNotFound());
  }

  @Test
  public void testFindIntersecting() throws Exception {
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
    Application appInResult = insertApplication(testCommon.dummyApplication("Test Application", "Handler"));
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
    ResultActions ra = createLocationTestApplication(testAppParams[0], "Syrjäkuja 5", "Hämärähomma", 1);
    Application app = wtc.parseObjectFromResult(ra, Application.class);
    assertNotNull(app.getLocationId());
    // Test: delete the application's location and verify that it gets deleted.
    Integer appId = app.getId();
    wtc.perform(delete(String.format("/applications/%d/location", appId))).andExpect(status().isOk());
    ra = wtc.perform(get(String.format("/applications/%d", appId))).andExpect(status().isOk());
    app = wtc.parseObjectFromResult(ra, Application.class);
    assertNull(app.getLocationId());
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

  private ResultActions createLocationTestApplication(TestAppParam tap, String streetAddress, String applicationName, int count)
      throws Exception {
    Integer locationId = testCommon.insertLocation(streetAddress,
        new GeometryCollection(new Geometry[] { tap.geometry }));
    Application app = testCommon.dummyApplication(applicationName, "locationUserName" + count);
    app.setLocationId(locationId);
    app.setStartTime(tap.startTime);
    app.setEndTime(tap.endTime);
    return wtc.perform(post("/applications"), app).andExpect(status().isOk());
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
