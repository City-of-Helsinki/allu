package fi.hel.allu.model.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Project;
import fi.hel.allu.model.testUtils.WebTestCommon;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
public class ProjectControllerTest {

  @Autowired
  WebTestCommon wtc;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  private ResultActions addProject(Project project) throws Exception {
    return wtc.perform(post("/projects"), project);
  }

  private Project addProjectGetResult(Project project) throws Exception {
    ResultActions resultActions = addProject(project).andExpect(status().isOk());
    return wtc.parseObjectFromResult(resultActions, Project.class);
  }

  // Date type is really troublesome... TODO: change Project class to use
  // LocalDate.
  private Date date(int year, int month, int day) {
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(year, month - 1, day); // January -> 0
    return cal.getTime();
  }

  private Project createProject(Integer projectID, String projectName, Date startDate) {
    Project p = new Project();
    p.setId(projectID);
    p.setName(projectName);
    p.setStartDate(startDate);
    return p;
  }

  @Test
  public void testAddProject() throws Exception {
    addProject(createProject(null, "TestProject", date(2016, 11, 12)))
      .andExpect(status().isOk());
  }

  @Test
  public void testAddProjectWithId() throws Exception {
    Project p = createProject(9999, "TestProject", date(2016, 11, 12));
    wtc.perform(post("/projects"), p).andExpect(status().isBadRequest());
  }

  @Test
  public void testGetNonExistent() throws Exception {
    wtc.perform(get("/projects/1")).andExpect(status().isNotFound());
  }

  @Test
  public void testGetExisting() throws Exception {
    // Setup: add a project
    Project p = createProject(null, "TestProject", date(2016, 11, 12));
    Project result = addProjectGetResult(p);

    // Now check TestProject got there.
    wtc.perform(get(String.format("/projects/%d", result.getId()))).andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(result.getId())))
        .andExpect(jsonPath("$.name", is("TestProject")));
  }

  @Test
  public void testUpdateExisting() throws Exception {

  }

  @Test
  public void testUpdateNonexistent() throws Exception {

  }
}
