package fi.hel.allu.model;

import fi.hel.allu.model.dao.PersonDao;
import fi.hel.allu.model.dao.ProjectDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Person;
import fi.hel.allu.model.domain.Project;
import org.javatuples.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import java.time.ZonedDateTime;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = App.class)
@WebAppConfiguration
public class ApplicationControllerTest {

  @Autowired
  WebTestCommon wtc;

  @Autowired
  ProjectDao projectDao;

  @Autowired
  PersonDao personDao;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  @Test
  public void testAddApplication() throws Exception {
    Application app = prepareApplication("Test Application", "Handlaaja");
    wtc.perform(post("/applications"), app).andExpect(status().isOk());
  }

  @Test
  public void testAddApplicationWithId() throws Exception {
    Application app = prepareApplication("Test Application", "Handlaaja");
    app.setId(123);
    wtc.perform(post("/applications"), app).andExpect(status().isBadRequest());
  }

  @Test
  public void testAddApplicationWithBadCustomer() throws Exception {
    Application app = prepareApplication("Test Application", "Handlaaja");
    app.setCustomerId(app.getCustomerId() + 1);
    wtc.perform(post("/applications"), app).andExpect(status().isBadRequest());
  }

  @Test
  public void testAddApplicationWithBadProject() throws Exception {
    Application app = prepareApplication("Test Application", "Handlaaja");
    app.setProjectId(app.getProjectId() + 1);
    wtc.perform(post("/applications"), app).andExpect(status().isBadRequest());
  }

  @Test
  public void testFindNonexistent() throws Exception {
    wtc.perform(get("/applications/123")).andExpect(status().isNotFound());
  }

  @Test
  public void testFindExisting() throws Exception {
    // Setup: insert an application
    Application appIn = prepareApplication("Test Application", "Handler");
    appIn.setStatus("Test Application status");
    ResultActions resultActions = wtc.perform(post("/applications"), appIn).andExpect(status().isOk());
    Application appInResult = wtc.parseObjectFromResult(resultActions, Application.class);
    // Test: try to read the same application back
    resultActions = wtc.perform(get(String.format("/applications/%d", appInResult.getId())))
        .andExpect(status().isOk());
    Application appOut = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals(appIn.getStatus(), appOut.getStatus());
  }

  @Test
  public void testFindApplicationByHandler() throws Exception {
    // Setup: insert some applications with handler "Henkka"
    final int NUM_HENKKA = 5;
    final int NUM_TIMPPA = 7;
    Application app = prepareApplication("TestApplication", "Henkka");
    for (int i = 0; i < NUM_HENKKA; ++i) {
      wtc.perform(post("/applications"), app).andExpect(status().isOk());
    }
    // Now insert some with handler "Timppa"
    app.setHandler("Timppa");
    for (int i = 0; i < NUM_TIMPPA; ++i) {
      wtc.perform(post("/applications"), app).andExpect(status().isOk());
    }
    // Now get all Henkka's applications:
    ResultActions resultActions = wtc.perform(get("/applications/byhandler/Henkka")).andExpect(status().isOk());
    Application[] results = wtc.parseObjectFromResult(resultActions, Application[].class);
    assertEquals(NUM_HENKKA, results.length);
    // Try also with nonexistent handler:
    wtc.perform(get("/applications/byhandler/Jimi")).andExpect(status().isNotFound());
  }

  @Test
  public void testFindApplicationByProject() throws Exception {
    // Setup: add some applications for one project:
    final int NUM_FIRST = 5;
    final int NUM_SECOND = 7;
    Application app1 = prepareApplication("TestAppOne", "Sinikka");
    for (int i = 0; i < NUM_FIRST; ++i) {
      wtc.perform(post("/applications"), app1).andExpect(status().isOk());
    }
    // Now prepare another application -- will get another project ID:
    Application app2 = prepareApplication("TestAppTwo", "Keijo");
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
    wtc.perform(get(String.format("/applications/byproject/%d", app1.getProjectId() + app2.getProjectId())))
        .andExpect(status().isNotFound());
  }

  @Test
  public void testUpdateExisting() throws Exception {
    // Setup: insert an application
    Application appIn = prepareApplication("Test Application", "Handler");
    ResultActions resultActions = wtc.perform(post("/applications"), appIn).andExpect(status().isOk());
    Application appInResult = wtc.parseObjectFromResult(resultActions, Application.class);
    // Test: try to update the application
    appInResult.setStatus("draft");
    resultActions = wtc.perform(put(String.format("/applications/%d", appInResult.getId())), appInResult)
        .andExpect(status().isOk());
    Application updateResult = wtc.parseObjectFromResult(resultActions, Application.class);
    assertEquals("draft", updateResult.getStatus());
  }

  @Test
  public void updateNonexistent() throws Exception {
    Application app = prepareApplication("Test Application", "Hanskaaja");
    wtc.perform(put("/applications/314159"), app).andExpect(status().isNotFound());
  }

  // Create and prepare an application for insertion:
  // - Create dummy person and project to get valid ids
  // - Set some values for the application
  private Application prepareApplication(String name, String handler) throws Exception {
    Pair<Integer, Integer> personAndProject = addPersonAndProject();
    Application app = new Application();
    app.setCustomerId(personAndProject.getValue0());
    app.setProjectId(personAndProject.getValue1());
    app.setCreationTime(ZonedDateTime.now());
    app.setName(name);
    app.setHandler(handler);
    return app;
  }

  private Pair<Integer, Integer> addPersonAndProject() throws Exception {
    Person person = new Person();
    person.setName("Pentti");
    person.setSsn("121212-xxxx");
    person.setEmail("pena@dev.null");
    Person insertedPerson = personDao.insert(person);
    Project project = new Project();
    project.setName("Viemärityö");
    project.setOwnerId(insertedPerson.getId());
    project.setContactId(insertedPerson.getId());
    project.setStartDate(Calendar.getInstance().getTime());
    Project insertedProject = projectDao.insert(project);
    return new Pair<>(insertedPerson.getId(), insertedProject.getId());
  }

}
