package fi.hel.allu.ui.service;

import fi.hel.allu.ui.domain.ProjectJson;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.Assert.*;

public class ProjectServiceTest extends MockServices {
  private static Validator validator;
  @InjectMocks
  protected ProjectService projectService;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initSaveMocks();
    initSearchMocks();
  }

  @Test
  public void testValidationWithValidProject() {
    Set<ConstraintViolation<ProjectJson>> constraintViolations =
        validator.validate(createProjectJson(1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidProject() {
    ProjectJson projectJson = projectService.createProject(createProjectJson(null));
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(100, projectJson.getId().intValue());
    assertEquals("Hanke1, Model", projectJson.getName());
  }

  @Test
  public void createProjectWithId() {
    ProjectJson projectJson = projectService.createProject(createProjectJson(1));
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(1, projectJson.getId().intValue());
    assertEquals("Hanke1, Json", projectJson.getName());
  }

  @Test
  public void updateValidProject() {
    ProjectJson projectJson = createProjectJson(1);
    projectService.updateProject(projectJson);
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(1, projectJson.getId().intValue());
    assertEquals("Hanke1, Json", projectJson.getName());
  }

  @Test
  public void updateProjectWithoutId() {
    ProjectJson projectJson = createProjectJson(null);
    projectService.updateProject(projectJson);
    assertNotNull(projectJson);
    assertNull(projectJson.getId());
    assertEquals("Hanke1, Json", projectJson.getName());
  }

  @Test
  public void testFindById() {
    ProjectJson projectJson = projectService.findProjectById(100);
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(100, projectJson.getId().intValue());
    assertEquals("Hanke1, Model", projectJson.getName());
  }
}
