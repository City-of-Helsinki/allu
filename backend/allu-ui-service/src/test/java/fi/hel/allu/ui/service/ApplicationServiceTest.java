package fi.hel.allu.ui.service;


import fi.hel.allu.ui.domain.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class ApplicationServiceTest extends MockServices {
  private static Validator validator;
  @Mock
  protected LocationService locationService;
  @Mock
  protected PersonService personService;
  @Mock
  protected ProjectService projectService;
  @Mock
  protected CustomerService customerService;
  @Mock
  protected ApplicantService applicantService;
  @InjectMocks
  protected ApplicationService applicationService;
  private ApplicationListJson applicationJsonList;

  public ApplicationServiceTest() {
    applicationJsonList = createMockApplicationListJson();
  }

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
    Mockito.when(locationService.createLocation(Mockito.anyObject())).thenAnswer((Answer<LocationJson>) invocation ->
        createLocationJson(102));
    Mockito.when(personService.createPerson(Mockito.anyObject())).thenAnswer((Answer<PersonJson>) invocation ->
        createPersonJson(200));
    Mockito.when(projectService.createProject(Mockito.anyObject())).thenAnswer((Answer<ProjectJson>) invocation ->
        createProjectJson(100));
    Mockito.when(customerService.createCustomer(Mockito.anyObject())).thenAnswer((Answer<CustomerJson>) invocation ->
        createCustomerJson(101, 200));
    Mockito.when(applicantService.createApplicant(Mockito.anyObject())).thenAnswer((Answer<ApplicantJson>) invocation ->
        createApplicantJson(103, 201));

    Mockito.when(locationService.findLocationById(Mockito.anyInt())).thenAnswer((Answer<LocationJson>) invocation ->
        createLocationJson(102));
    Mockito.when(personService.findPersonById(Mockito.anyInt())).thenAnswer((Answer<PersonJson>) invocation ->
        createPersonJson(200));
    Mockito.when(projectService.findProjectById(Mockito.anyInt())).thenAnswer((Answer<ProjectJson>) invocation ->
        createProjectJson(100));
    Mockito.when(customerService.findCustomerById(Mockito.anyInt())).thenAnswer((Answer<CustomerJson>) invocation ->
        createCustomerJson(101, 200));
    Mockito.when(applicantService.findApplicantById(Mockito.anyInt())).thenAnswer((Answer<ApplicantJson>) invocation ->
        createApplicantJson(103, 201));

  }

  @Test
  public void testCreateWithNullApplicationName() {
    applicationJsonList.getApplicationList().get(0).setName(null);
    Set<ConstraintViolation<ApplicationJson>> constraintViolations =
        validator.validate(applicationJsonList.getApplicationList().get(0));
    assertEquals(1, constraintViolations.size());
    assertEquals("Application name is required", constraintViolations.iterator().next().getMessage());
  }


  @Test
  public void testCreateWithEmptyApplicationType() {
    applicationJsonList.getApplicationList().get(0).setType("");
    Set<ConstraintViolation<ApplicationJson>> constraintViolations =
        validator.validate(applicationJsonList.getApplicationList().get(0));
    assertEquals(1, constraintViolations.size());
    assertEquals("Application type is required", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void testCreateWithValidApplication() {
    ApplicationListJson response = applicationService.createApplication(applicationJsonList);

    assertNotNull(response);
    assertNotNull(response.getApplicationList());
    assertEquals(1, response.getApplicationList().size());
    assertEquals(1, response.getApplicationList().get(0).getId().intValue());
    assertNotNull(response.getApplicationList().get(0).getApplicant());
    assertNotNull(response.getApplicationList().get(0).getCustomer());
    assertNotNull(response.getApplicationList().get(0).getProject());
    assertNotNull(response.getApplicationList().get(0).getLocation());
    assertEquals(100, response.getApplicationList().get(0).getProject().getId().intValue());
    assertEquals(101, response.getApplicationList().get(0).getCustomer().getId().intValue());
    assertEquals(102, response.getApplicationList().get(0).getLocation().getId().intValue());
    assertEquals(103, response.getApplicationList().get(0).getApplicant().getId().intValue());
    assertEquals("Kalle käsittelijä, Json", response.getApplicationList().get(0).getHandler());
    assertNull(response.getApplicationList().get(0).getApplicant().getPerson());
    assertNotNull(response.getApplicationList().get(0).getApplicant().getOrganization());
    assertEquals(201, response.getApplicationList().get(0).getApplicant().getOrganization().getId().intValue());
    assertNull(response.getApplicationList().get(0).getCustomer().getOrganization());
    assertNotNull(response.getApplicationList().get(0).getCustomer().getPerson());
    assertEquals(200, response.getApplicationList().get(0).getCustomer().getPerson().getId().intValue());
    assertNotNull(response.getApplicationList().get(0).getLocation().getGeometry());
  }


  @Test
  public void testUpdateApplication() {
    ApplicationJson applicationJson = createMockApplicationJson(1);
    applicationService.updateApplication(1, applicationJson);
    assertNotNull(applicationJson);
    assertEquals(1, applicationJson.getId().intValue());
    assertEquals("Kalle käsittelijä, Json", applicationJson.getHandler());
  }


  @Test
  public void testFindApplicationById() {
    ApplicationJson response = applicationService.findApplicationById("123");

    assertNotNull(response);
    assertNotNull(response.getCustomer());
    assertNotNull(response.getProject());
    assertNotNull(response.getApplicant());
    assertEquals(100, response.getProject().getId().intValue());
    assertEquals(101, response.getCustomer().getId().intValue());
    assertEquals(102, response.getLocation().getId().intValue());
    assertEquals(103, response.getApplicant().getId().intValue());
    assertNull(response.getCustomer().getOrganization());
    assertNull(response.getApplicant().getPerson());
    assertNotNull(response.getApplicant().getOrganization());
    assertNotNull(response.getCustomer().getPerson());
    assertEquals(201, response.getApplicant().getOrganization().getId().intValue());
    assertEquals(200, response.getCustomer().getPerson().getId().intValue());
  }

  @Test
  public void testFindApplicationByHandler() {
    List<ApplicationJson> response = applicationService.findApplicationByHandler("222");

    assertNotNull(response);
    assertEquals(2, response.size());

    assertNotNull(response.get(0).getCustomer());
    assertNotNull(response.get(0).getProject());
    assertNotNull(response.get(0).getApplicant());
    assertNotNull(response.get(0).getLocation());
    assertEquals(100, response.get(0).getProject().getId().intValue());
    assertEquals(101, response.get(0).getCustomer().getId().intValue());
    assertEquals(102, response.get(0).getLocation().getId().intValue());
    assertEquals(103, response.get(0).getApplicant().getId().intValue());
    assertNull(response.get(0).getCustomer().getOrganization());
    assertNull(response.get(0).getApplicant().getPerson());
    assertNotNull(response.get(0).getApplicant().getOrganization());
    assertNotNull(response.get(0).getCustomer().getPerson());
    assertEquals(201, response.get(0).getApplicant().getOrganization().getId().intValue());
    assertEquals(200, response.get(0).getCustomer().getPerson().getId().intValue());

    assertNotNull(response.get(1));
    assertNotNull(response.get(1).getCustomer());
    assertNotNull(response.get(1).getProject());
    assertNotNull(response.get(1).getApplicant());
    assertNotNull(response.get(1).getLocation());
    assertEquals("MockName2", response.get(1).getName());
  }
}
