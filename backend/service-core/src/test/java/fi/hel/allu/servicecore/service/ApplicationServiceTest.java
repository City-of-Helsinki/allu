package fi.hel.allu.servicecore.service;


import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.CustomerJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.event.ApplicationOwnerChangeEvent;
import fi.hel.allu.servicecore.event.ApplicationUpdateEvent;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import fi.hel.allu.servicecore.mapper.CustomerMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServiceTest extends MockServices {
  private static Validator validator;
  @Mock
  protected CustomerService customerService;
  @Autowired
  protected ApplicationMapper applicationMapper;
  @Mock
  protected ContactService contactService;
  @Mock
  protected UserService userService;
  @Mock
  protected LocationService locationService;
  @Mock
  private CustomerMapper customerMapper;
  @Mock
  private PersonAuditLogService personAuditLogService;
  @Mock
  private PaymentClassServiceImpl paymentClassService;
  @Mock
  private PaymentZoneServiceImpl paymentZoneService;
  @Mock
  private InvoicingPeriodService invoicingPeriodService;
  @Mock
  private InvoiceService invoiceService;
  @Mock
  private ApplicationEventPublisher eventPublisher;

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
    applicationMapper = new ApplicationMapper(customerMapper, userService, locationService);

    initSaveMocks();
    initSearchMocks();
    userService = Mockito.mock(UserService.class);

    Mockito.when(customerService.createCustomer(Mockito.anyObject())).thenAnswer((Answer<CustomerJson>) invocation ->
        createCustomerJson(103));

    Mockito.when(customerService.findCustomerById(Mockito.anyInt())).thenAnswer((Answer<CustomerJson>) invocation ->
        createCustomerJson(103));

    userJson = new UserJson(USER_ID, null, null, null, null, null, true, null, null, null, null);
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);

    applicationService = new ApplicationService(props, restTemplate, applicationMapper, userService,
        personAuditLogService, paymentClassService, paymentZoneService, invoicingPeriodService, invoiceService, eventPublisher);
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
    Application response = applicationService.createApplication(createMockApplicationJson(null));

    assertNotNull(response);
    assertEquals(1, response.getId().intValue());
    assertEquals(103, (int) response.getCustomersWithContacts().get(0).getCustomer().getId());
    assertNotNull(response.getExtension());
    assertNotNull(response.getDecisionTime());
    assertNotNull(response.getExtension());
  }


  @Test
  public void testUpdateApplication() {
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class),
        Mockito.eq(Application.class), Mockito.anyInt(), Mockito.anyInt())).thenAnswer(
            (Answer<ResponseEntity<Application>>) invocation -> new ResponseEntity<>(createMockApplicationModel(),
                HttpStatus.CREATED));

    ApplicationJson applicationJson = createMockApplicationJson(1);
    applicationService.updateApplication(1, applicationJson);
    assertNotNull(applicationJson);
    assertEquals(1, applicationJson.getId().intValue());
    assertEquals(createMockUser().getId(), applicationJson.getOwner().getId());
  }

  @Test
  public void shouldPublishApplicationEventOnUpdate() {
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class),
        Mockito.eq(Application.class), Mockito.anyInt(), Mockito.anyInt())).thenAnswer(
            (Answer<ResponseEntity<Application>>) invocation -> new ResponseEntity<>(createMockApplicationModel(),
                HttpStatus.CREATED));

    ApplicationJson applicationJson = createMockApplicationJson(1);
    applicationService.updateApplication(1, applicationJson);
    verify(eventPublisher, times(1)).publishEvent(any(ApplicationUpdateEvent.class));
  }

  @Test
  public void testUpdateApplicationOwner() {
    ApplicationJson applicationJson = createMockApplicationJson(1);
    applicationService.updateApplicationOwner(2, Collections.singletonList(applicationJson.getId()));
    Mockito.verify(restTemplate, Mockito.times(1)).put(null, Collections.singletonList(applicationJson.getId()), 2);
  }

  @Test
  public void shouldPublishApplicationEventOnOwnerUpdate() {
    ApplicationJson applicationJson = createMockApplicationJson(1);
    applicationService.updateApplicationOwner(2, Collections.singletonList(applicationJson.getId()));
    verify(eventPublisher, times(1)).publishEvent(any(ApplicationOwnerChangeEvent.class));
  }

  @Test
  public void testCreateWithApplicationTags() {
    ApplicationJson applicationJson = createMockApplicationJson(null);
    ApplicationTagJson applicationTag = new ApplicationTagJson(null, ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED, null);
    applicationJson.setApplicationTags(Collections.singletonList(applicationTag));
    applicationService.createApplication(applicationJson);
    ArgumentCaptor<Application> applicationArgumentCaptor = ArgumentCaptor.forClass(Application.class);

    Mockito.verify(restTemplate, Mockito.times(1))
        .postForObject(Mockito.anyString(), applicationArgumentCaptor.capture(), Mockito.eq(Application.class), Mockito.anyInt());
    Application application = applicationArgumentCaptor.getValue();
    assertEquals(1, application.getApplicationTags().size());
    assertEquals(USER_ID, (int) application.getApplicationTags().get(0).getAddedBy());
    assertNotNull(application.getApplicationTags().get(0).getCreationTime());
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
        .postForObject(Mockito.anyString(), applicationArgumentCaptor.capture(), Mockito.eq(Application.class), Mockito.anyInt());
    Application application = applicationArgumentCaptor.getValue();
    assertEquals(2, application.getApplicationTags().size());
    assertEquals(USER_ID, (int) application.getApplicationTags().get(0).getAddedBy());
    assertEquals(1, (int) application.getApplicationTags().get(1).getAddedBy());
  }


  @Test
  public void testRemoveApplicationOwner() {
    ApplicationJson applicationJson = createMockApplicationJson(1);
    ApplicationProperties ap = Mockito.mock(ApplicationProperties.class);
    Mockito.when(ap.getApplicationOwnerRemoveUrl()).thenReturn("asdf");
    applicationService.setApplicationProperties(ap);
    applicationService.removeApplicationOwner(Collections.singletonList(applicationJson.getId()));
    Mockito.verify(restTemplate, Mockito.times(1)).put("asdf", Collections.singletonList(applicationJson.getId()));
  }

  @Test
  public void testFindApplicationById() {
    Application response = applicationService.findApplicationById(123);

    assertNotNull(response);
    assertNotNull(response.getProjectId());
    assertNotNull(response.getCustomersWithContacts().get(0).getCustomer().getId());
    assertNotNull(response.getExtension());
    assertEquals(100, (long) response.getProjectId());
    assertNotNull(response.getCustomersWithContacts().get(0).getCustomer().getId());
    assertEquals(103, (long) response.getCustomersWithContacts().get(0).getCustomer().getId());
  }

  @Test
  public void testFindApplicationsById() {
    List<Application> response = applicationService.findApplicationsById(Collections.singletonList(123));
    assertEquals(2, response.size());
  }

  @Test
  public void shouldPublishApplicationEventOnStatusChange() {
    Mockito.when(restTemplate.exchange(
        anyString(),
        eq(HttpMethod.PUT),
        any(HttpEntity.class),
        eq(Application.class),
        anyInt())).thenReturn(new ResponseEntity<Application>(createMockApplicationModel(), HttpStatus.OK));
    applicationService.changeApplicationStatus(1, StatusType.DECISIONMAKING);
    verify(eventPublisher, times(1)).publishEvent(any(ApplicationUpdateEvent.class));
  }

  @Test
  public void shouldPublishApplicationEventOnSetInvoiceRecipient() {
    Application response = applicationService.createApplication(createMockApplicationJson(1));
    ApplicationProperties ap = Mockito.mock(ApplicationProperties.class);
    Mockito.when(ap.getApplicationInvoiceRecipientUrl()).thenReturn("http://application/invoicerecipient");
    applicationService.setApplicationProperties(ap);
    applicationService.setInvoiceRecipient(response.getId(), 15);
    verify(eventPublisher, times(1)).publishEvent(any(ApplicationUpdateEvent.class));
  }
}
