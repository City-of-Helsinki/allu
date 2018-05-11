package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.CustomerType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.geolatte.geom.builder.DSL;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.geolatte.geom.builder.DSL.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ProjectControllerTest {

  private final int KRUUNUNHAKA_CITY_DISTRICT_ID = 2;
  private final int KLUUVI_CITY_DISTRICT_ID = 3;
  private final int HERTTONIEMI_CITY_DISTRICT_ID = 44;
  private static int projectNbr = 0;

  private final ZoneId zoneId = ZoneId.of( "Europe/Helsinki" );
  private Customer testCustomer;
  private Contact testContact;
  private User testUser;

  @Autowired
  WebTestCommon wtc;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    wtc.setup();
    testCustomer = testCommon.insertPerson();
    testContact = testCommon.insertContact(testCustomer.getId());
    testUser = testCommon.insertUser("Project User");
  }

  @Test
  public void testAddProject() throws Exception {
    addProject(createProject(null, "TestProject", ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]")))
      .andExpect(status().isOk());
  }

  @Test
  public void testAddProjectWithId() throws Exception {
    Project p = createProject(9999, "TestProject", ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
    wtc.perform(post("/projects"), p).andExpect(status().isBadRequest());
  }

  @Test
  public void testGetNonExistent() throws Exception {
    wtc.perform(get("/projects/1")).andExpect(status().isNotFound());
  }

  @Test
  public void testGetExisting() throws Exception {
    // Setup: add a project
    Project p = createProject(null, "TestProject", ZonedDateTime.parse("2016-11-12T08:00:00+02:00[Europe/Helsinki]"));
    Project result = addProjectGetResult(p);

    // Now check TestProject got there.
    wtc.perform(get(String.format("/projects/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId())))
        .andExpect(jsonPath("$.name", is("TestProject")));
  }

  @Test
  public void testFindApplicationByProject() throws Exception {
    // Setup: add some applications for one project:
    final int NUM_FIRST = 5;
    final int NUM_SECOND = 7;
    int projectId1 = testCommon.insertProject("p1");
    Application app1 = testCommon.dummyOutdoorApplication("TestAppOne", "Sinikka");
    app1.setProjectId(projectId1);
    for (int i = 0; i < NUM_FIRST; ++i) {
      wtc.perform(post("/applications?userId=" + testUser.getId()), app1).andExpect(status().isOk());
    }
    // Now prepare another application -- will get another project ID:
    int projectId2 = testCommon.insertProject("p2");
    Application app2 = testCommon.dummyOutdoorApplication("TestAppTwo", "Keijo");
    app2.setProjectId(projectId2);
    assertNotEquals(app1.getProjectId(), app2.getProjectId());
    for (int i = 0; i < NUM_SECOND; ++i) {
      wtc.perform(post("/applications?userId=" + testUser.getId()), app2).andExpect(status().isOk());
    }
    // Now get applications for the first project:
    ResultActions resultActions = wtc.perform(
        get(String.format("/projects/%d/applications", app1.getProjectId()))).andExpect(status().isOk());
    Application[] results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(NUM_FIRST, results.length);
    // Try also with nonexistent project id:
    resultActions = wtc
        .perform(get(String.format("/projects/%d/applications", app1.getProjectId() + app2.getProjectId())))
        .andExpect(status().isOk());
    results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(0, results.length);
  }

  @Test
  public void testUpdateExistingPreservesDates() throws Exception {
    Project originalProject = createDummyProject();
    originalProject.setStartTime(ZonedDateTime.parse("2016-11-11T08:00:00+02:00[Europe/Helsinki]"));
    Project addedProject = addProjectGetResult(originalProject);
    assertEquals(originalProject.getStartTime(), addedProject.getStartTime().withZoneSameInstant(zoneId));
    ZonedDateTime updatedStartTime = ZonedDateTime.parse("2001-11-11T08:00:00+02:00[Europe/Helsinki]");
    addedProject.setStartTime(updatedStartTime);
    Project updatedProject = updateProjectGetResult(addedProject);
    assertEquals(originalProject.getStartTime(), updatedProject.getStartTime().withZoneSameInstant(zoneId));
  }

  @Test
  public void testUpdateNonexistent() throws Exception {
    Project dummyProject = createDummyProject();
    dummyProject.setId(1234);
    wtc.perform(put("/projects/" + 1234), dummyProject).andExpect(status().is4xxClientError());
  }


  @Test
  public void testUpdateExistingWithParent() throws Exception {
    Project originalProject = createDummyProject();
    Project addedProject = addProjectGetResult(originalProject);
    Project parentProject = createDummyProject();
    Project addedParent = addProjectGetResult(parentProject);
    addedProject.setParentId(addedParent.getId());
    updateProjectParentGetResult(addedProject.getId(), addedParent.getId());
    List<Project> children = getProjectChildren(addedParent.getId());
    assertEquals(1, children.size());
  }

  @Test
  public void testUpdateProjectInformation() throws Exception {
    Project parentProject = createDummyProject();
    Project addedParent = addProjectGetResult(parentProject);
    Project originalProject = createDummyProject();
    originalProject.setParentId(addedParent.getId());
    Project addedProject = addProjectGetResult(originalProject);
    Project updatedProject = updateProjectGetResult(addedProject);
    ResultActions resultActions = wtc.perform(
        put("/projects/update?userId=" + testUser.getId()), Collections.singletonList(updatedProject.getId())).andExpect(status().isOk());
    Project[] updatedProjects = wtc.parseObjectFromResult(resultActions, Project[].class);
    Assert.assertEquals(2, updatedProjects.length);
    HashSet<Project> updatedProjectsSet = new HashSet<>(Arrays.asList(updatedProjects));
    Assert.assertTrue(updatedProjectsSet.contains(addedProject));
    Assert.assertTrue(updatedProjectsSet.contains(addedParent));
  }

  @Test
  public void testUpdateProjectApplications() throws Exception {
    Customer customer = addPersonCustomerToDatabase("nimi", "nimi@foo.fi");
    ZonedDateTime startTime = ZonedDateTime.parse("2016-11-11T08:00:00+02:00[Europe/Helsinki]");
    ZonedDateTime endTime = ZonedDateTime.parse("2016-11-20T08:00:00+02:00[Europe/Helsinki]");

    Application newApplication = createApplication(customer);
    addCityDistrictToApplication(newApplication, KLUUVI_CITY_DISTRICT_ID, startTime, endTime);
    Application addedApplication = addApplicationToDatabase(newApplication);

    Project project1 = createDummyProject();
    project1 = addProjectGetResult(project1);
    Project project2 = createDummyProject();
    project2 = addProjectGetResult(project2);

    // add application to the project
    List<Integer> addedApplications = addApplicationsGetResult(project1.getId(), Arrays.asList(addedApplication.getId()));
    assertEquals(1, addedApplications.size());

    Project updatedProject1 = getProject(project1.getId());
    assertEquals(startTime, updatedProject1.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedProject1.getEndTime().withZoneSameInstant(zoneId));

    // add application to another project
    addApplicationsGetResult(project2.getId(), Arrays.asList(addedApplication.getId()));

    Project updatedProject2 = getProject(project2.getId());
    assertEquals(startTime, updatedProject2.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedProject2.getEndTime().withZoneSameInstant(zoneId));
    // remove application from the other project
    removeApplicationFromProject(addedApplication.getId());

    updatedProject2 = getProject(updatedProject2.getId());
    Application applicationIdDb = getApplication(addedApplication.getId());
    updatedProject1 = getProject(updatedProject1.getId());
    assertNull(applicationIdDb.getProjectId());
    // since none of the projects have applications, they should have null as start and end times
    assertEquals(null, updatedProject1.getStartTime());
    assertEquals(null, updatedProject1.getEndTime());
    assertEquals(null, updatedProject2.getStartTime());
    assertEquals(null, updatedProject2.getEndTime());
  }

  @Test
  public void testUpdateProjectWithParentAndApplications() throws Exception {
    Customer customer = addPersonCustomerToDatabase("nimi", "nimi@foo.fi");
    ZonedDateTime startTime = ZonedDateTime.parse("2016-11-11T08:00:00+02:00[Europe/Helsinki]");
    ZonedDateTime endTime = ZonedDateTime.parse("2016-11-20T08:00:00+02:00[Europe/Helsinki]");
    Application newApplication1 = createApplication(customer);
    addCityDistrictToApplication(newApplication1, KLUUVI_CITY_DISTRICT_ID, startTime, endTime);
    Application addedApplication1 = addApplicationToDatabase(newApplication1);

    Project project = createDummyProject();
    project = addProjectGetResult(project);

    List<Integer> addedApplications = addApplicationsGetResult(project.getId(), Arrays.asList(addedApplication1.getId()));
    assertEquals(1, addedApplications.size());

    ResultActions resultActions = wtc.perform(get("/projects/" + project.getId()));
    Project updatedProject = wtc.parseObjectFromResult(resultActions, Project.class);
    assertEquals(startTime, updatedProject.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedProject.getEndTime().withZoneSameInstant(zoneId));

    // add parent to the project and check that it will get the same validity time
    Project projectParent = createDummyProject();
    projectParent = addProjectGetResult(projectParent);
    wtc.perform(
      put("/projects/" + project.getId() + "/parentProject/" + projectParent.getId() + "?userId=" + testUser.getId()))
        .andExpect(status().isOk());

    Project updatedParentProject = wtc.parseObjectFromResult(resultActions, Project.class);
    assertEquals(startTime, updatedParentProject.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedParentProject.getEndTime().withZoneSameInstant(zoneId));

    // add application to the parent project
    Application parentApplication = createApplication(customer);
    // Polygon that's mostly in Herttoniemi:
    DSL.Polygon2DToken herttoniemi_polygon = polygon(
        ring(c(25502404.097045037895441, 6675352.16425826959312), c(25502772.543876249343157, 6675370.854831204749644),
            c(25502767.204117525368929, 6675095.857257002033293), c(25502393.421108055859804, 6675101.196953509002924),
            c(25502404.097045037895441, 6675352.16425826959312)));
    addCityDistrictToApplication(parentApplication, herttoniemi_polygon, startTime.minusDays(1), endTime.plusDays(1));
    Application addedParentApplication = addApplicationToDatabase(parentApplication);

    wtc.perform(
        put("/projects/" + projectParent.getId() + "/applications?userId=" + testUser.getId()),
        Collections.singletonList(addedParentApplication.getId())).andExpect(status().isOk());

    // check that both parent and child have been updated correctly
    project = getProject(project.getId());
    projectParent = getProject(projectParent.getId());
    // child start time is later than parent and child has end time earlier than parent
    assertEquals(startTime, project.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, project.getEndTime().withZoneSameInstant(zoneId));
    assertEquals(startTime.minusDays(1), projectParent.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime.plusDays(1), projectParent.getEndTime().withZoneSameInstant(zoneId));

    // add another application to child
    Application newApplication2 = createApplication(customer);
    addCityDistrictToApplication(newApplication2, KRUUNUNHAKA_CITY_DISTRICT_ID, startTime.minusDays(2), endTime.minusDays(1));
    Application addedApplication2 = addApplicationToDatabase(newApplication2);

    wtc.perform(
        put("/projects/" + project.getId() + "/applications?userId=" + testUser.getId()),
        Arrays.asList(addedApplication1.getId(), addedApplication2.getId())).andExpect(status().isOk());

    // check that both parent and child have been updated correctly
    project = getProject(project.getId());
    projectParent = getProject(projectParent.getId());
    // newApplication2.startTime, because it's earliest
    assertEquals(startTime.minusDays(2), project.getStartTime().withZoneSameInstant(zoneId));
    // newApplication1.endTime, because it's latest
    assertEquals(endTime, project.getEndTime().withZoneSameInstant(zoneId));
    // newApplication2.startTime, because it's earliest
    assertEquals(startTime.minusDays(2), projectParent.getStartTime().withZoneSameInstant(zoneId));
    // parentApplication.endTime, because it's latest
    assertEquals(endTime.plusDays(1), projectParent.getEndTime().withZoneSameInstant(zoneId));
    // city districts of the child project
    assertEquals(2, project.getCityDistricts().length);
    assertTrue(Arrays.asList(
        project.getCityDistricts()).containsAll(Arrays.asList(KRUUNUNHAKA_CITY_DISTRICT_ID, KLUUVI_CITY_DISTRICT_ID)));
    // city districts of the parent project
    assertEquals(3, projectParent.getCityDistricts().length);
    assertTrue(Arrays.asList(
        projectParent.getCityDistricts()).containsAll(
            Arrays.asList(KRUUNUNHAKA_CITY_DISTRICT_ID, HERTTONIEMI_CITY_DISTRICT_ID, KLUUVI_CITY_DISTRICT_ID)));
  }

  @Test
  public void testCircularReference() throws Exception {
    Project parentProject = createDummyProject();
    Project addedParent = addProjectGetResult(parentProject);

    Project originalProject = createDummyProject();
    originalProject.setParentId(addedParent.getId());
    Project addedProject = addProjectGetResult(originalProject);


    String url = String.format("/projects/%d/parentProject/%d", addedParent.getId(), addedProject.getId());
    wtc.perform(put(url)).andExpect(status().is4xxClientError());
  }

  private ResultActions addProject(Project project) throws Exception {
    ProjectChange pc = new ProjectChange(testUser.getId(), project);
    return wtc.perform(post("/projects"), pc);
  }

  private Project getProject(int projectId) throws Exception {
    ResultActions resultActions = wtc.perform(get("/projects/" + projectId)).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Project.class);
  }

  private Application getApplication(int applicationId) throws Exception {
    ResultActions resultActions = wtc.perform(get("/applications/" + applicationId)).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private Project addProjectGetResult(Project project) throws Exception {
    ResultActions resultActions = addProject(project).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Project.class);
  }

  private Project updateProjectGetResult(Project project) throws Exception {
    ProjectChange pc = new ProjectChange(testUser.getId(), project);
    ResultActions resultActions = wtc.perform(put("/projects/" + project.getId()), pc).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Project.class);
  }

  private Project updateProjectParentGetResult(Integer projectId, Integer parentId) throws Exception {
    String url = String.format("/projects/%d/parentProject/%d?userId=%d", projectId, parentId, testUser.getId());
    ResultActions resultActions = wtc.perform(put(url)).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Project.class);
  }

  private List<Project> getProjectChildren(int projectId) throws Exception {
    ResultActions resultActions = wtc.perform(get("/projects/" + projectId + "/children")).andExpect(status().isOk());
    return Arrays.asList(wtc.parseObjectFromResult(resultActions, Project[].class));
  }

  private List<Integer> addApplicationsGetResult(Integer id, List<Integer> applicationIds) throws Exception {
    ResultActions resultActions = wtc.perform(
        put("/projects/" + id + "/applications?userId=" + testUser.getId()),
        applicationIds).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, List.class);
  }

  private void removeApplicationFromProject(Integer id) throws Exception {
    wtc.perform(delete("/projects/applications/" + id + "?userId=" + testUser.getId())).andExpect(status().isOk());
  }

  private Project createProject(Integer projectID, String projectName, ZonedDateTime startDate) {
    Project p = new Project();
    p.setId(projectID);
    p.setName(projectName);
    p.setStartTime(startDate);
    p.setCustomerId(testCustomer.getId());
    p.setContactId(testContact.getId());
    p.setIdentifier("project" + (projectNbr++));
    return p;
  }

  private Project createDummyProject() {
    Project project = new Project();
    project.setCustomerId(testCustomer.getId());
    project.setContactId(testContact.getId());
    project.setAdditionalInfo("lisätietoja");
    project.setName("das projekt");
    project.setIdentifier("project" +  + (projectNbr++));
    return project;
  }

  // Helper to add person
  private Customer addPersonCustomerToDatabase(String name, String email) throws Exception {
    Integer userId = testCommon.insertUser("dummyUser").getId();
    Customer customer = new Customer();
    customer.setName(name);
    customer.setType(CustomerType.PERSON);
    customer.setEmail(email);
    customer.setCountryId(testCommon.getCountryIdOfFinland());
    CustomerChange customerChange = new CustomerChange(userId, customer);
    ResultActions resultActions = wtc.perform(post("/customers"), customerChange).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Customer.class);
  }

  private Application createApplication(Customer customer) {
    ShortTermRental shortTermRental = new ShortTermRental();
    shortTermRental.setDescription("desc");
    Application application = new Application();
    application.setStartTime(ZonedDateTime.parse("2015-01-03T10:15:30+02:00"));
    application.setEndTime(ZonedDateTime.parse("2015-02-03T10:15:30+02:00"));
    application.setRecurringEndTime(application.getEndTime());
    application.setCustomersWithContacts(
        Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, Collections.emptyList())));
    application.setExtension(shortTermRental);
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.OTHER, Collections.emptyList()));
    application.setMetadataVersion(1);
    application.setName("short term test");
    application.setNotBillable(false);
    return application;
  }

  private Application addApplicationToDatabase(Application application) throws Exception {
    ResultActions resultActions = wtc.perform(post("/applications?userId=" + testUser.getId()), application).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }

  private void addCityDistrictToApplication(
      Application application,
      DSL.Polygon2DToken polygon,
      ZonedDateTime startTime,
      ZonedDateTime endTime) throws Exception {
    Location location = newLocationWithDefaults(startTime, endTime);
    location.setApplicationId(application.getId());
    location.setGeometry(geometrycollection(3879, polygon));
    application.setLocations(Collections.singletonList(location));
  }

  private void addCityDistrictToApplication(
      Application application,
      int districtOverride,
      ZonedDateTime startTime,
      ZonedDateTime endTime) throws Exception {
    Location location = newLocationWithDefaults(startTime, endTime);
    location.setApplicationId(application.getId());
    location.setCityDistrictIdOverride(districtOverride);
    application.setLocations(Collections.singletonList(location));
  }

  private Location newLocationWithDefaults(ZonedDateTime startTime, ZonedDateTime endTime) {
    Location location = new Location();
    location.setUnderpass(false);
    location.setStartTime(startTime);
    location.setEndTime(endTime);
    return location;
  }
}
