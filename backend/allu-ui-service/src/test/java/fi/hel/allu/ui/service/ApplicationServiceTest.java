package fi.hel.allu.ui.service;


import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
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
  @Autowired
  protected ApplicationMapper applicationMapper;
  @Mock
  protected ContactService contactService;
  @Mock
  protected SearchService searchService;
  @Mock
  protected MetaService metaService;

  private ApplicationService applicationService;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    applicationMapper = new ApplicationMapper();
    applicationService = new ApplicationService(props, restTemplate, locationService, customerService, applicantService, projectService,
        applicationMapper, contactService, searchService, metaService);

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
    Mockito.when(contactService.findContactsForApplication(Mockito.anyInt()))
        .thenAnswer((Answer<List<ContactJson>>) invocation -> createContactList());
    Mockito.when(metaService.findMetadataForApplication(Mockito.any()))
        .thenAnswer((Answer<StructureMetaJson>) invocation -> createMockStructureMetadataJson());
  }

  @Test
  public void testCreateWithNullApplicationName() {
    ApplicationJson applicationJson = createMockApplicationJson(null);
    applicationJson.setName(null);
    Set<ConstraintViolation<ApplicationJson>> constraintViolations =
        validator.validate(applicationJson);
    assertEquals(1, constraintViolations.size());
    assertEquals("Application name is required", constraintViolations.iterator().next().getMessage());
  }


  @Test
  public void testCreateWithEmptyApplicationType() {
    ApplicationJson applicationJson = createMockApplicationJson(null);
    applicationJson.setType(null);
    Set<ConstraintViolation<ApplicationJson>> constraintViolations =
        validator.validate(applicationJson);
    assertEquals(1, constraintViolations.size());
    assertEquals("Application type is required", constraintViolations.iterator().next().getMessage());
  }

  @Test
  public void testCreateWithValidApplication() {
    ApplicationJson response = applicationService.createApplication(createMockApplicationJson(null));

    assertNotNull(response);
    assertEquals(1, response.getId().intValue());
    assertNotNull(response.getApplicant());
    assertNotNull(response.getCustomer());
    assertNotNull(response.getProject());
    assertNotNull(response.getLocation());
    assertEquals(100, response.getProject().getId().intValue());
    assertEquals(101, response.getCustomer().getId().intValue());
    assertEquals(102, response.getLocation().getId().intValue());
    assertEquals(103, response.getApplicant().getId().intValue());
    assertEquals("Mock handler, Model", response.getHandler());
    assertNull(response.getApplicant().getPerson());
    assertNotNull(response.getApplicant().getOrganization());
    assertEquals(201, response.getApplicant().getOrganization().getId().intValue());
    assertNull(response.getCustomer().getOrganization());
    assertNotNull(response.getCustomer().getPerson());
    assertEquals(200, response.getCustomer().getPerson().getId().intValue());
    assertNotNull(response.getLocation().getGeometry());
    assertNotNull(response.getEvent());
    assertNotNull(response.getDecisionTime());
    assertEquals(1050, ((OutdoorEventJson) response.getEvent()).getAttendees());
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
    assertNotNull(response.getEvent());
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
    assertEquals("outdoor event nature, Model", ((OutdoorEventJson)response.getEvent()).getNature());
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
    assertNotNull(response.get(0).getEvent());
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

  @Test
  public void testFindApplicationByLocation() {
    LocationQueryJson query = new LocationQueryJson();
    query.setIntesectingGeometry(polygon(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
    List<ApplicationJson> response = applicationService.findApplicationByLocation(query);

    assertNotNull(response);
    assertEquals(2, response.size());

    assertNotNull(response.get(0).getCustomer());
    assertNotNull(response.get(0).getProject());
    assertNotNull(response.get(0).getApplicant());
    assertNotNull(response.get(0).getLocation());
    assertNotNull(response.get(0).getEvent());
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
