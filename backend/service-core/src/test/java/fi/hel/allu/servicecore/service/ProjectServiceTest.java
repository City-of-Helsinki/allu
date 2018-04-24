package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.Project;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.util.Collections;
import java.util.Set;

public class ProjectServiceTest extends MockServices {
  private static Validator validator;
  protected ProjectService projectService;
  @Mock
  private CustomerService customerService;
  @Mock
  private ContactService contactService;
  @Mock
  private UserService userService;

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

    ProjectMapper projectMapper = new ProjectMapper(customerService, contactService);
    projectService = new ProjectService(props, restTemplate, projectMapper, userService);

    Mockito.when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(), Mockito.eq(Project[].class)))
        .thenAnswer(invocation -> new Project[] {createMockProjectModel()});
    Mockito.when(customerService.findCustomerById(Mockito.anyInt())).thenAnswer(invocation -> createCustomerJson(103));
    UserJson user = new UserJson();
    user.setId(1);
    Mockito.when(userService.getCurrentUser()).thenReturn(user);
  }

  @Test
  public void testValidationWithValidProject() {
    Set<ConstraintViolation<ProjectJson>> constraintViolations =
        validator.validate(createProjectJson(1));
    Assert.assertEquals(0, constraintViolations.size());
  }

  @Test
  public void createValidProject() {
    ProjectJson projectJson = projectService.insert(createProjectJson(null));
    Assert.assertNotNull(projectJson);
    Assert.assertNotNull(projectJson.getId());
    Assert.assertEquals(100, projectJson.getId().intValue());
    Assert.assertEquals("Hanke1, Model", projectJson.getName());
  }

  @Test
  public void createProjectWithId() {
    ProjectJson projectJson = projectService.insert(createProjectJson(1));
    Assert.assertNotNull(projectJson);
    Assert.assertNotNull(projectJson.getId());
    Assert.assertEquals(100, projectJson.getId().intValue());
    Assert.assertEquals("Hanke1, Model", projectJson.getName());
  }

  @Test
  public void updateValidProject() {
    ProjectJson projectJson = createProjectJson(1);
    projectService.update(projectJson.getId(), projectJson);
    Assert.assertNotNull(projectJson);
    Assert.assertNotNull(projectJson.getId());
    Assert.assertEquals(1, projectJson.getId().intValue());
    Assert.assertEquals("Hanke1, Json", projectJson.getName());
  }

  @Test
  public void testFindById() {
    ProjectJson projectJson = projectService.findByIds(Collections.singletonList(100)).get(0);
    Assert.assertNotNull(projectJson);
    Assert.assertNotNull(projectJson.getId());
    Assert.assertEquals(100, projectJson.getId().intValue());
    Assert.assertEquals("Hanke1, Model", projectJson.getName());
  }
}
