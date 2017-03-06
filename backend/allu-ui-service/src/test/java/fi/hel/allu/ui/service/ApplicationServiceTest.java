package fi.hel.allu.ui.service;


import fi.hel.allu.common.types.ApplicationTagType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoiceRow;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.geolatte.geom.builder.DSL.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServiceTest extends MockServices {
  private static Validator validator;
  @Mock
  protected LocationService locationService;
  @Mock
  protected ApplicantService applicantService;
  @Autowired
  protected ApplicationMapper applicationMapper;
  @Mock
  protected ContactService contactService;
  @Mock
  protected MetaService metaService;
  @Mock
  protected UserService userService;

  private ApplicationService applicationService;

  private UserJson userJson;
  private static final int USER_ID = 123;

  @BeforeClass
  public static void setUpBeforeClass() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Before
  public void setUp() {
    applicationMapper = new ApplicationMapper();

    initSaveMocks();
    initSearchMocks();
    userService = Mockito.mock(UserService.class);

    Mockito.when(locationService.createLocation(Mockito.anyInt(), Mockito.anyObject())).thenAnswer((Answer<LocationJson>) invocation ->
        createLocationJson(102));
    Mockito.when(applicantService.createApplicant(Mockito.anyObject())).thenAnswer((Answer<ApplicantJson>) invocation ->
        createApplicantJson(103, 201));

    Mockito.when(locationService.findLocationById(Mockito.anyInt())).thenAnswer((Answer<LocationJson>) invocation ->
        createLocationJson(102));
    Mockito.when(applicantService.findApplicantById(Mockito.anyInt())).thenAnswer((Answer<ApplicantJson>) invocation ->
        createApplicantJson(103, 201));
    Mockito.when(contactService.findContactsForApplication(Mockito.anyInt()))
        .thenAnswer((Answer<List<ContactJson>>) invocation -> createContactList());

    Mockito.when(metaService.findMetadataForApplication(Mockito.any()))
        .thenAnswer((Answer<StructureMetaJson>) invocation -> createMockStructureMetadataJson());
    Mockito.when(metaService.findMetadataForApplication(Mockito.any(), Mockito.anyInt()))
        .thenAnswer((Answer<StructureMetaJson>) invocation -> createMockStructureMetadataJson());

    userJson = new UserJson(USER_ID, null, null, null, null, true, null, null);
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);

    applicationService = new ApplicationService(
        props, restTemplate, locationService, applicantService, applicationMapper, contactService, metaService, userService);
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
    assertNotNull(response.getLocations());
    assertEquals(1, response.getLocations().size());
    assertEquals(102, response.getLocations().get(0).getId().intValue());
    assertNotNull(response.getApplicant());
    assertEquals(103, response.getApplicant().getId().intValue());
    assertNotNull(response.getLocations().get(0).getGeometry());
    assertNotNull(response.getExtension());
    assertNotNull(response.getDecisionTime());
    assertEquals(1050, ((EventJson) response.getExtension()).getAttendees());
  }


  @Test
  public void testUpdateApplication() {
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class),
        Mockito.eq(Application.class), Mockito.anyInt())).thenAnswer(
            (Answer<ResponseEntity<Application>>) invocation -> new ResponseEntity<>(createMockApplicationModel(),
                HttpStatus.CREATED));

    ApplicationJson applicationJson = createMockApplicationJson(1);
    applicationService.updateApplication(1, applicationJson);
    assertNotNull(applicationJson);
    assertEquals(1, applicationJson.getId().intValue());
    assertEquals(createMockUser().getId(), applicationJson.getHandler().getId());
  }

  @Test
  public void testUpdateApplicationHandler() {
    ApplicationJson applicationJson = createMockApplicationJson(1);
    applicationService.updateApplicationHandler(2, Collections.singletonList(applicationJson.getId()));
    Mockito.verify(restTemplate, Mockito.times(1)).put(null, Collections.singletonList(applicationJson.getId()), 2);
  }

  @Test
  public void testCreateWithApplicationTags() {
    ApplicationJson applicationJson = createMockApplicationJson(null);
    ApplicationTagJson applicationTag = new ApplicationTagJson(null, ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED, null);
    applicationJson.setApplicationTags(Collections.singletonList(applicationTag));
    applicationService.createApplication(applicationJson);
    ArgumentCaptor<Application> applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);

    Mockito.verify(restTemplate, Mockito.times(1))
        .postForObject(Mockito.anyString(), applicationArgumentCaptor.capture(), Mockito.eq(Application.class));
    Application application = applicationArgumentCaptor.getValue();
    Assert.assertEquals(1, application.getApplicationTags().size());
    Assert.assertEquals(USER_ID, (int) application.getApplicationTags().get(0).getAddedBy());
    Assert.assertNotNull(application.getApplicationTags().get(0).getCreationTime());
  }

  @Test
  public void testUpdateWithApplicationTags() {
    ApplicationJson applicationJson = createMockApplicationJson(null);
    ApplicationTagJson applicationTag1 = new ApplicationTagJson(null, ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED, null);
    ApplicationTagJson applicationTag2 = new ApplicationTagJson(1, ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED, ZonedDateTime.now());
    applicationJson.setApplicationTags(Arrays.asList(applicationTag1, applicationTag2));
    applicationService.createApplication(applicationJson);
    ArgumentCaptor<Application> applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);

    Mockito.verify(restTemplate, Mockito.times(1))
        .postForObject(Mockito.anyString(), applicationArgumentCaptor.capture(), Mockito.eq(Application.class));
    Application application = applicationArgumentCaptor.getValue();
    Assert.assertEquals(2, application.getApplicationTags().size());
    Assert.assertEquals(USER_ID, (int) application.getApplicationTags().get(0).getAddedBy());
    Assert.assertEquals(1, (int) application.getApplicationTags().get(1).getAddedBy());
  }


  @Test
  public void testRemoveApplicationHandler() {
    ApplicationJson applicationJson = createMockApplicationJson(1);
    ApplicationProperties ap = Mockito.mock(ApplicationProperties.class);
    Mockito.when(ap.getApplicationHandlerRemoveUrl()).thenReturn("asdf");
    applicationService.setApplicationProperties(ap);
    applicationService.removeApplicationHandler(Collections.singletonList(applicationJson.getId()));
    Mockito.verify(restTemplate, Mockito.times(1)).put("asdf", Collections.singletonList(applicationJson.getId()));
  }

  @Test
  public void testFindApplicationById() {
    Application response = applicationService.findApplicationById(123);

    assertNotNull(response);
    assertNotNull(response.getProjectId());
    assertNotNull(response.getApplicantId());
    assertNotNull(response.getExtension());
    assertEquals(100, (long) response.getProjectId());
    assertNotNull(response.getApplicantId());
    assertEquals(103, (long) response.getApplicantId());
  }

  @Test
  public void testFindApplicationsById() {
    List<Application> response = applicationService.findApplicationsById(Collections.singletonList(123));
    assertEquals(2, response.size());
  }

  @Test
  public void testFindApplicationByLocation() {
    LocationQueryJson query = new LocationQueryJson();
    query.setIntersectingGeometry(polygon(3879, ring(c(0, 0), c(0, 1), c(1, 1), c(1, 0), c(0, 0))));
    List<Application> response = applicationService.findApplicationByLocation(query);

    assertNotNull(response);
    assertEquals(2, response.size());

    assertNotNull(response.get(0).getProjectId());
    assertNotNull(response.get(0).getApplicantId());
    assertNotNull(response.get(0).getExtension());
    assertEquals(100, (long) response.get(0).getProjectId());
    assertNotNull(response.get(0).getApplicantId());
    assertEquals(103, (long) response.get(0).getApplicantId());
    assertNotNull(response.get(1));
    assertNotNull(response.get(1).getProjectId());
    assertNotNull(response.get(1).getApplicantId());
    assertEquals("MockName2", response.get(1).getName());
  }

  @Test
  public void testGetInvoiceRows() {
    InvoiceRow row = new InvoiceRow();
    row.setRowText("Row row row your boat");
    Mockito.when(restTemplate.getForEntity(Mockito.anyString(), Mockito.eq(InvoiceRow[].class), Mockito.anyInt()))
        .then(invocation -> new ResponseEntity<>(new InvoiceRow[] { row }, HttpStatus.OK));

    List<InvoiceRow> result = applicationService.getInvoiceRows(99);

    assertEquals(1, result.size());
    assertEquals("Row row row your boat", result.get(0).getRowText());
  }
}
