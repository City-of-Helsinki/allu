package fi.hel.allu.model.controller;

import fi.hel.allu.common.types.ApplicantType;
import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Applicant;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.model.domain.ShortTermRental;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ProjectControllerTest {

  private ZoneId zoneId = ZoneId.of( "Europe/Helsinki" );

  @Autowired
  WebTestCommon wtc;

  @Autowired
  TestCommon testCommon;

  @Before
  public void setup() throws Exception {
    wtc.setup();
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
    int projectId1 = testCommon.insertProject();
    Application app1 = testCommon.dummyOutdoorApplication("TestAppOne", "Sinikka");
    app1.setProjectId(projectId1);
    for (int i = 0; i < NUM_FIRST; ++i) {
      wtc.perform(post("/applications"), app1).andExpect(status().isOk());
    }
    // Now prepare another application -- will get another project ID:
    int projectId2 = testCommon.insertProject();
    Application app2 = testCommon.dummyOutdoorApplication("TestAppTwo", "Keijo");
    app2.setProjectId(projectId2);
    assertNotEquals(app1.getProjectId(), app2.getProjectId());
    for (int i = 0; i < NUM_SECOND; ++i) {
      wtc.perform(post("/applications"), app2).andExpect(status().isOk());
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
  public void testUpdateExisting() throws Exception {
    Project originalProject = createDummyProject();
    originalProject.setStartTime(ZonedDateTime.parse("2016-11-11T08:00:00+02:00[Europe/Helsinki]"));
    Project addedProject = addProjectGetResult(originalProject);
    assertEquals(originalProject.getStartTime(), addedProject.getStartTime().withZoneSameInstant(zoneId));
    ZonedDateTime updatedStartTime = ZonedDateTime.parse("2001-11-11T08:00:00+02:00[Europe/Helsinki]");
    addedProject.setStartTime(updatedStartTime);
    Project updatedProject = updateProjectGetResult(addedProject);
    assertEquals(updatedStartTime, updatedProject.getStartTime().withZoneSameInstant(zoneId));
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
    Project updatedProject = updateProjectGetResult(addedProject);
    List<Project> children = getProjectChildren(addedParent.getId());
    assertEquals(1, children.size());
  }

  @Test
  public void testUpdateProjectInformation() throws Exception {
    Project originalProject = createDummyProject();
    Project addedProject = addProjectGetResult(originalProject);
    Project parentProject = createDummyProject();
    Project addedParent = addProjectGetResult(parentProject);
    addedProject.setParentId(addedParent.getId());
    Project updatedProject = updateProjectGetResult(addedProject);
    ResultActions resultActions = wtc.perform(
        put("/projects/update"), Collections.singletonList(updatedProject.getId())).andExpect(status().isOk());
    Project[] updatedProjects = wtc.parseObjectFromResult(resultActions, Project[].class);
    Assert.assertEquals(2, updatedProjects.length);
    HashSet<Project> updatedProjectsSet = new HashSet<>(Arrays.asList(updatedProjects));
    Assert.assertTrue(updatedProjectsSet.contains(addedProject));
    Assert.assertTrue(updatedProjectsSet.contains(addedParent));
  }

  @Test
  public void testUpdateProjectApplications() throws Exception {
    Applicant applicant = addPersonApplicantToDatabase("nimi", "nimi@foo.fi");
    ZonedDateTime startTime = ZonedDateTime.parse("2016-11-11T08:00:00+02:00[Europe/Helsinki]");
    ZonedDateTime endTime = ZonedDateTime.parse("2016-11-20T08:00:00+02:00[Europe/Helsinki]");
    Application newApplication = createApplication(applicant, startTime, endTime);
    Application addedApplication = addApplicationToDatabase(newApplication);
    Project project1 = createDummyProject();
    project1 = addProjectGetResult(project1);
    Project project2 = createDummyProject();
    project2 = addProjectGetResult(project2);
    // add application to the project
    ResultActions resultActions = wtc.perform(
        put("/projects/" + project1.getId() + "/applications"),
        Collections.singletonList(addedApplication.getId())).andExpect(status().isOk());
    Project updatedProject1 = wtc.parseObjectFromResult(resultActions, Project.class);
    assertEquals(startTime, updatedProject1.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedProject1.getEndTime().withZoneSameInstant(zoneId));
    // add application to another project
    resultActions = wtc.perform(
        put("/projects/" + project2.getId() + "/applications"),
        Collections.singletonList(addedApplication.getId())).andExpect(status().isOk());
    Project updatedProject2 = wtc.parseObjectFromResult(resultActions, Project.class);
    assertEquals(startTime, updatedProject2.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedProject2.getEndTime().withZoneSameInstant(zoneId));
    // remove application from the other project
    resultActions = wtc.perform(
        put("/projects/" + project2.getId() + "/applications"),
        Collections.emptyList()).andExpect(status().isOk());
    updatedProject2 = wtc.parseObjectFromResult(resultActions, Project.class);
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
    Applicant applicant = addPersonApplicantToDatabase("nimi", "nimi@foo.fi");
    ZonedDateTime startTime = ZonedDateTime.parse("2016-11-11T08:00:00+02:00[Europe/Helsinki]");
    ZonedDateTime endTime = ZonedDateTime.parse("2016-11-20T08:00:00+02:00[Europe/Helsinki]");
    Application newApplication1 = createApplication(applicant, startTime, endTime);
    Application addedApplication1 = addApplicationToDatabase(newApplication1);

    Project project = createDummyProject();
    project = addProjectGetResult(project);

    ResultActions resultActions = wtc.perform(
        put("/projects/" + project.getId() + "/applications"),
        Collections.singletonList(addedApplication1.getId())).andExpect(status().isOk());
    Project updatedProject = wtc.parseObjectFromResult(resultActions, Project.class);
    assertEquals(startTime, updatedProject.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedProject.getEndTime().withZoneSameInstant(zoneId));

    // add parent to the project and check that it will get the same validity time
    Project projectParent = createDummyProject();
    projectParent = addProjectGetResult(projectParent);
    wtc.perform(
        put("/projects/" + project.getId() + "/parentProject/" + projectParent.getId())).andExpect(status().isOk());

    Project updatedParentProject = wtc.parseObjectFromResult(resultActions, Project.class);
    assertEquals(startTime, updatedParentProject.getStartTime().withZoneSameInstant(zoneId));
    assertEquals(endTime, updatedParentProject.getEndTime().withZoneSameInstant(zoneId));

    // add application to the parent project
    Application parentApplication = createApplication(applicant, startTime.minusDays(1), endTime.plusDays(1));
    Application addedParentApplication = addApplicationToDatabase(parentApplication);
    resultActions = wtc.perform(
        put("/projects/" + projectParent.getId() + "/applications"),
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
    Application newApplication2 = createApplication(applicant, startTime.minusDays(2), endTime.minusDays(1));
    Application addedApplication2 = addApplicationToDatabase(newApplication2);
    wtc.perform(
        put("/projects/" + project.getId() + "/applications"),
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
  }

  @Test
  public void testCircularReference() throws Exception {
    Project originalProject = createDummyProject();
    Project addedProject = addProjectGetResult(originalProject);
    Project parentProject = createDummyProject();
    Project addedParent = addProjectGetResult(parentProject);
    addedProject.setParentId(addedParent.getId());
    addedParent.setParentId(addedProject.getId());
    updateProjectGetResult(addedProject);
    wtc.perform(put("/projects/" + addedParent.getId()), addedParent).andExpect(status().is4xxClientError());
  }

  private ResultActions addProject(Project project) throws Exception {
    return wtc.perform(post("/projects"), project);
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
    ResultActions resultActions = wtc.perform(put("/projects/" + project.getId()), project).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Project.class);
  }

  private List<Project> getProjectChildren(int projectId) throws Exception {
    ResultActions resultActions = wtc.perform(get("/projects/" + projectId + "/children")).andExpect(status().isOk());;
    return Arrays.asList(wtc.parseObjectFromResult(resultActions, Project[].class));
  }

  private Project createProject(Integer projectID, String projectName, ZonedDateTime startDate) {
    Project p = new Project();
    p.setId(projectID);
    p.setName(projectName);
    p.setStartTime(startDate);
    return p;
  }

  private Project createDummyProject() {
    Project project = new Project();
    project.setContactName("kontakti");
    project.setAdditionalInfo("lisätietoja");
    project.setName("das projekt");
    return project;
  }

  // Helper to add person
  private Applicant addPersonApplicantToDatabase(String name, String email) throws Exception {
    Applicant applicant = new Applicant();
    applicant.setName(name);
    applicant.setType(ApplicantType.PERSON);
    applicant.setEmail(email);
    ResultActions resultActions = wtc.perform(post("/applicants"), applicant).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Applicant.class);
  }

  private Application createApplication(Applicant applicant, ZonedDateTime startTime, ZonedDateTime endTime) {
    ShortTermRental shortTermRental = new ShortTermRental();
    shortTermRental.setDescription("desc");

    Application application = new Application();
    application.setApplicantId(applicant.getId());
    application.setStartTime(startTime);
    application.setEndTime(endTime);
    application.setExtension(shortTermRental);
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(ApplicationKind.OTHER_SHORT_TERM_RENTAL);
    application.setMetadataVersion(1);
    application.setName("short term test");
    return application;
  }

  private Application addApplicationToDatabase(Application application) throws Exception {
    ResultActions resultActions = wtc.perform(post("/applications"), application).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Application.class);
  }
}
