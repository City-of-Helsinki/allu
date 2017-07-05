package fi.hel.allu.ui.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.CustomerRoleType;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class DecisionServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @Mock
  private LocationService locationService;
  @Mock
  private ApplicationServiceComposer applicationServiceComposer;

  private DecisionService decisionService;


  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getPdfServiceUrl(Mockito.anyString())).thenReturn("PdfServiceUrl");
    Mockito.when(applicationProperties.getModelServiceUrl(Mockito.anyString())).thenReturn("ModelServiceUrl");

    decisionService = new DecisionService(applicationProperties, restTemplate, locationService, applicationServiceComposer);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateWithEmptyApplication() throws IOException {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);
  }

  @Test
  public void testGenerateShortTermRental() throws IOException {
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.POST), Mockito.any(),
        eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name :
    Mockito.verify(restTemplate).postForObject(eq("PdfServiceUrl"), Mockito.anyObject(),
        eq(byte[].class),
        eq("SHORT_TERM_RENTAL"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(eq("ModelServiceUrl"), eq(HttpMethod.POST), Mockito.any(),
        eq(String.class), Mockito.anyInt());
  }

  @Test
  public void testGenerateEvent() throws IOException {
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.POST), Mockito.any(),
        eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.EVENT);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name:
    Mockito.verify(restTemplate).postForObject(eq("PdfServiceUrl"), Mockito.anyObject(),
        eq(byte[].class), eq("EVENT"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(eq("ModelServiceUrl"), eq(HttpMethod.POST), Mockito.any(),
        eq(String.class), Mockito.anyInt());
  }


  @Test
  public void testGenerateCableReport() throws IOException {
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), eq(HttpMethod.POST), Mockito.any(),
        eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setExtension(new CableReportJson());
    applicationJson.setId(123);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name :
    Mockito.verify(restTemplate).postForObject(eq("PdfServiceUrl"), Mockito.anyObject(),
        eq(byte[].class),
        eq("DUMMY")); // FIXME: "CABLE_REPORT" not implemented yet.
    // - Validity time was stored to model:
    final ArgumentCaptor<ApplicationJson> msgCaptor = ArgumentCaptor.forClass(ApplicationJson.class);
    Mockito.verify(applicationServiceComposer).updateApplication(eq(123), msgCaptor.capture());
    CableReportJson cableReportJson = (CableReportJson) msgCaptor.getValue().getExtension();
    assertNotNull(cableReportJson.getValidityTime());
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(eq("ModelServiceUrl"), eq(HttpMethod.POST), Mockito.any(),
        eq(String.class), Mockito.anyInt());
  }

  @Test
  public void testGetDecision() {
    byte[] mockData = new byte[123];
    for (int i = 0; i < mockData.length; ++i) {
      mockData[i] = (byte) i;
    }
    Mockito.when(restTemplate.getForObject(eq("ModelServiceUrl"), eq(byte[].class), Mockito.anyInt()))
        .thenReturn(mockData);

    byte[] decision = decisionService.getDecision(911);

    assertArrayEquals(mockData, decision);
  }

  private List<CustomerWithContactsJson> createDummyCustomersWithContactsJson() {
    CustomerWithContactsJson customerWithContactsJson = new CustomerWithContactsJson();
    customerWithContactsJson.setCustomer(new CustomerJson());
    PostalAddressJson postalAddressJson = new PostalAddressJson();
    postalAddressJson.setCity("Siti");
    postalAddressJson.setPostalCode("11111");
    postalAddressJson.setStreetAddress("striitti 1");
    customerWithContactsJson.getCustomer().setPostalAddress(postalAddressJson);
    customerWithContactsJson.setRoleType(CustomerRoleType.APPLICANT);
    return Collections.singletonList(customerWithContactsJson);
  }
}
