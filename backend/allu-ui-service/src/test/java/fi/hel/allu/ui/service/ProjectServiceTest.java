package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Project;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.ui.mapper.ProjectMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProjectServiceTest extends MockServices {
  private static Validator validator;
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
    ProjectMapper projectMapper = new ProjectMapper();
    projectService = new ProjectService(props, restTemplate, projectMapper);

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(), Mockito.eq(Project[].class)))
        .thenAnswer((Answer<Project[]>) invocation -> new Project[] {createMockProjectModel()});
  }

  @Test
  public void testValidationWithValidProject() {
    Set<ConstraintViolation<ProjectJson>> constraintViolations =
        validator.validate(createProjectJson(1));
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidProject() {
    ProjectJson projectJson = projectService.insert(createProjectJson(null));
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(100, projectJson.getId().intValue());
    assertEquals("Hanke1, Model", projectJson.getName());
  }

  @Test
  public void createProjectWithId() {
    ProjectJson projectJson = projectService.insert(createProjectJson(1));
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(100, projectJson.getId().intValue());
    assertEquals("Hanke1, Model", projectJson.getName());
  }

  @Test
  public void updateValidProject() {
    ProjectJson projectJson = createProjectJson(1);
    projectService.update(projectJson.getId(), projectJson);
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(1, projectJson.getId().intValue());
    assertEquals("Hanke1, Json", projectJson.getName());
  }

  @Test
  public void testFindById() {
    ProjectJson projectJson = projectService.findByIds(Collections.singletonList(100)).get(0);
    assertNotNull(projectJson);
    assertNotNull(projectJson.getId());
    assertEquals(100, projectJson.getId().intValue());
    assertEquals("Hanke1, Model", projectJson.getName());
  }
}
