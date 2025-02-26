package fi.hel.allu.model.controller;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

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

import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationControllerTest {

  private User testUser;

  @Autowired
  WebTestCommon wtc;

  @Autowired
  TestCommon testCommon;

  @Autowired
  LocationService locationService;

  @Before
  public void setup() throws Exception {
    wtc.setupNoDelete();
    testUser = testCommon.insertUser("testUser");
  }

  @Test
  public void testAddApplication() throws Exception {
    Application app = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
    wtc.perform(post("/applications?userId=" + testUser.getId()), app).andExpect(status().isOk());
  }

  @Test
  public void testAddApplicationWithId() throws Exception {
    Application app = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
    app.setId(123);
    wtc.perform(post("/applications?userId=\" + testUser.getId()"), app).andExpect(status().isBadRequest());
  }

  @Test
  public void testAddApplicationWithBadProject() throws Exception {
    Application app = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
    app.setProjectId(app.getProjectId() + 1);
    wtc.perform(post("/applications?userId=\" + testUser.getId()"), app).andExpect(status().isBadRequest());
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
    Application appIn = testCommon.dummyOutdoorApplication("Test Application", "Owner");
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
    Application appIn = testCommon.dummyBridgeBannerApplication("Test Application", "Owner");
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
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Owner"));
    // Test: try to update the application
    appInResult.setStatus(StatusType.HANDLING);
    appInResult.setName("updatedname");
    ResultActions resultActions = wtc.perform(put(String.format("/applications/%d?userId=" + testUser.getId() , appInResult.getId())), appInResult)
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(StatusType.PENDING, updateResult.getStatus());
    assertEquals(appInResult.getName(), updateResult.getName());
  }

  @Test
  public void testUpdateOwner() throws Exception {
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Owner"));
    User changedUser = testCommon.insertUser("changed");
    appInResult.setOwner(changedUser.getId());
    wtc.perform(put(String.format("/applications/owner/%d", appInResult.getOwner())), Collections.singletonList(appInResult.getId()))
        .andExpect(status().isOk());
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(changedUser.getId(), updateResult.getOwner());
  }

  @Test
  public void testRemoveOwner() throws Exception {
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Owner"));
    wtc.perform(put(String.format("/applications/owner/remove")), Collections.singletonList(appInResult.getId()))
        .andExpect(status().isOk());
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertNull(updateResult.getOwner());
  }

  @Test
  public void testReplaceDistributionList() throws Exception {
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Owner"));
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
    wtc.perform(put("/applications/314159?userId=" + testUser.getId()), app).andExpect(status().isNotFound());
  }

  /**
   * Test that reading an application's attachment list works
   *
   * @throws Exception
   */
  @Test
  public void testFindAttachments() throws Exception {
    // Setup: insert an application
    Application appInResult = insertApplication(testCommon.dummyOutdoorApplication("Test Application", "Owner"));
    // Test: read the application's attachment list
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d/attachments", appInResult.getId())))
        .andExpect(status().isOk());
    AttachmentInfo[] results = wtc.parseObjectFromResult(resultActions, AttachmentInfo[].class);
    assertEquals(0, results.length);
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
    newApplication.setName("test outdoor event");
    newApplication.setCustomersWithContacts(
        Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, eventCustomer, Collections.emptyList())));
    // TODO: remove these two lines setStartTime and setEndTime, because they should get set automatically from location
    newApplication.setStartTime(ZonedDateTime.parse("2017-02-01T00:00:01+02:00[Europe/Helsinki]"));
    newApplication.setEndTime(ZonedDateTime.parse("2017-02-08T23:59:59+02:00[Europe/Helsinki]"));
    newApplication.setRecurringEndTime(ZonedDateTime.parse("2017-02-08T23:59:59+02:00[Europe/Helsinki]"));
    newApplication.setMetadataVersion(1);
    newApplication.setKindsWithSpecifiers(
        Collections.singletonMap(ApplicationKind.OUTDOOREVENT, Collections.emptyList()));
    newApplication.setNotBillable(false);
    Event event = new Event();
    event.setDescription("Eventti");
    event.setEcoCompass(true);
    event.setNature(EventNature.CLOSED);
    event.setSurfaceHardness(SurfaceHardness.HARD);
    event.setEventStartTime(newApplication.getStartTime());
    event.setEventEndTime(newApplication.getEndTime());
    newApplication.setExtension(event);
    Geometry geometry = polygon(3879,
        ring(c(25492000, 6675000), c(25492500, 6675000), c(25492100, 6675100), c(25492000, 6675000)));
    Application application =
        insertApplicationWithGeometry(
            newApplication,
            new GeometryCollection(new Geometry[] { geometry }),
            "Mannerheimintie 1",
            ZonedDateTime.parse("2017-02-01T00:00:01+02:00[Europe/Helsinki]"),
            ZonedDateTime.parse("2017-02-08T23:59:59+02:00[Europe/Helsinki]"));

    // read application back from database and check the calculated price
    ResultActions ra = wtc.perform(get(String.format("/applications/%d", application.getId()))).andExpect(status().isOk());
    application = wtc.parseObjectFromResult(ra, Application.class);
    int expectedPrice = 280000; // 8 days, 500 EUR per day with eco compass discount
    assertEquals(expectedPrice, (int) application.getCalculatedPrice());
  }

  // Helper to insert an application. Returns the result application.
  private Application insertApplication(Application appIn) throws Exception {
    Integer userId = testCommon.insertUser("dummyUser" + System.currentTimeMillis()).getId();
    ControllerHelper.addDummyCustomer(wtc, appIn, userId, testCommon.getCountryIdOfFinland());
    ResultActions resultActions = wtc.perform(post("/applications?userId=" + testUser.getId()), appIn).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private Application insertApplicationWithGeometry(
      Application application,
      GeometryCollection geometryCollection,
      String streetAddress,
      ZonedDateTime startTime,
      ZonedDateTime endTime)
      throws Exception {
    application.setLocations(Collections.singletonList(testCommon.createLocation(streetAddress, geometryCollection, startTime, endTime)));
    Application insertedApp = insertApplication(application);
    return insertedApp;
  }

  @Test
  public void testGetAnonymizableApplications_noData() throws Exception {
    ResultActions resultActions = wtc.perform(get("/applications/anonymizable"))
      .andExpect(status().isOk());
    String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    assertEquals("[]", jsonResponse);
  }

  @Test
  public void checkApplicationAnonymizabilityWithValid() throws Exception {
    Application app1 = testCommon.dummyOutdoorApplication("Test Application", "Owner");
    Application app1Result = insertApplication(app1);
    Application app2 = testCommon.dummyOutdoorApplication("Test Application2", "Owner2");
    Application app2Result = insertApplication(app2);

    wtc.perform(patch("/applications/resetanonymizable").content("[" + app1Result.getId() + "," + app2Result.getId() + "]").contentType("application/json")).andExpect(status().isOk()).andReturn();

    ResultActions resultActions = wtc.perform(post("/applications/checkanonymizability").content("[" + app1Result.getId() + "," + app2Result.getId() + "]").contentType("application/json"))
      .andExpect(status().isOk());
    String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    assertEquals("[]", jsonResponse);
  }

  @Test
  public void checkApplicationAnonymizabilityWithInvalid() throws Exception {
    Application app1 = testCommon.dummyOutdoorApplication("Test Application", "Owner");
    Application app1Result = insertApplication(app1);
    Application app2 = testCommon.dummyOutdoorApplication("Test Application2", "Owner2");
    Application app2Result = insertApplication(app2);

    wtc.perform(patch("/applications/resetanonymizable").content("[" + app1Result.getId() + "," + app2Result.getId() + "]").contentType("application/json")).andExpect(status().isOk()).andReturn();

    ResultActions resultActions = wtc.perform(post("/applications/checkanonymizability").content("[" + app2Result.getId() + "," + (app1Result.getId() + app2Result.getId()) + "]").contentType("application/json"))
      .andExpect(status().isOk());
    String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
    assertEquals("[" + (app1Result.getId() + app2Result.getId()) + "]", jsonResponse);
  }

}
