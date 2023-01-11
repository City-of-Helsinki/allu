package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.Project;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.mapper.ChangeHistoryMapper;
import fi.hel.allu.servicecore.mapper.ProjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest extends MockServices {
  private static Validator validator;

  @Mock
  private CustomerService customerService;
  @Mock
  private ContactService contactService;
  @Mock
  private UserService userService;
  @Mock
  ApplicationProperties applicationProperties;
  @Mock
  private ChangeHistoryMapper changeHistoryMapper;

  @InjectMocks
  protected ProjectService projectService;

  @BeforeAll
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @BeforeEach
  public void setUp() {
    initSaveMocks();
    initSearchMocks();
    ProjectMapper projectMapper = new ProjectMapper(customerService, contactService, userService);
    projectService = new ProjectService(TestProperties.getProperties(), restTemplate, projectMapper, userService, changeHistoryMapper);

    when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyList(), Mockito.eq(Project[].class)))
        .thenReturn(new Project[] {createMockProjectModel()});
    when(customerService.findCustomerById(Mockito.anyInt())).thenAnswer(invocation -> createCustomerJson(103));
    UserJson user = new UserJson();
    user.setId(1);
    when(userService.getCurrentUser()).thenReturn(user);
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