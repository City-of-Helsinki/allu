package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.pdf.domain.CableInfoTexts;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
  @Mock
  private CustomerService customerService;
  @Mock
  private ContactService contactService;
  @Mock
  private ChargeBasisService chargeBasisService;

  private DecisionService decisionService;


  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getPdfServiceUrl(Mockito.anyString())).thenReturn("PdfServiceUrl");
    Mockito.when(applicationProperties.getModelServiceUrl(Mockito.anyString())).thenReturn("ModelServiceUrl");

    decisionService = new DecisionService(applicationProperties, restTemplate, locationService,
        applicationServiceComposer, customerService, contactService, chargeBasisService);
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
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Matchers.eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Matchers.eq(HttpMethod.POST), Mockito.any(),
        Matchers.eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name :
    Mockito.verify(restTemplate).postForObject(Matchers.eq("PdfServiceUrl"), Mockito.anyObject(),
        Matchers.eq(byte[].class),
        Matchers.eq("SHORT_TERM_RENTAL"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Matchers.eq("ModelServiceUrl"), Matchers.eq(HttpMethod.POST), Mockito.any(),
        Matchers.eq(String.class), Mockito.anyInt());
  }

  @Test
  public void testGenerateEvent() throws IOException {
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Matchers.eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Matchers.eq(HttpMethod.POST), Mockito.any(),
        Matchers.eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.EVENT);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name:
    Mockito.verify(restTemplate).postForObject(Matchers.eq("PdfServiceUrl"), Mockito.anyObject(),
        Matchers.eq(byte[].class), Matchers.eq("EVENT"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Matchers.eq("ModelServiceUrl"), Matchers.eq(HttpMethod.POST), Mockito.any(),
        Matchers.eq(String.class), Mockito.anyInt());
  }


  @Test
  public void testGenerateCableReport() throws IOException {
    final int MAP_EXCTRACT_COUNT = 93;
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Matchers.eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Matchers.eq(HttpMethod.POST), Mockito.any(),
        Matchers.eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    CableReportJson cableReportJsonIn = new CableReportJson();
    cableReportJsonIn.setInfoEntries(Arrays.asList(
        newInfoEntry(DefaultTextType.ELECTRICITY, "Sähköä"),
        newInfoEntry(DefaultTextType.GAS, "Kaasua"),
        newInfoEntry(DefaultTextType.ELECTRICITY, "Lisää sähköä")));
    cableReportJsonIn.setMapExtractCount(MAP_EXCTRACT_COUNT);
    applicationJson.setExtension(cableReportJsonIn);
    applicationJson.setId(123);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name :
    final ArgumentCaptor<DecisionJson> jsonCaptor = ArgumentCaptor.forClass(DecisionJson.class);
    Mockito.verify(restTemplate).postForObject(Matchers.eq("PdfServiceUrl"), jsonCaptor.capture(),
        Matchers.eq(byte[].class),
        Matchers.eq("CABLE_REPORT"));
    // - Sent JSON object contains field cableInfoEntries
    DecisionJson decisionJson = jsonCaptor.getValue();
    List<CableInfoTexts> infoEntries = decisionJson.getCableInfoEntries();
    Assert.assertNotNull(infoEntries);
    Assert.assertEquals(3, infoEntries.size());
    Assert.assertEquals(MAP_EXCTRACT_COUNT, decisionJson.getMapExtractCount());
    // - Validity time was stored to model:
    final ArgumentCaptor<ApplicationJson> msgCaptor = ArgumentCaptor.forClass(ApplicationJson.class);
    Mockito.verify(applicationServiceComposer).updateApplication(Matchers.eq(123), msgCaptor.capture());
    CableReportJson cableReportJsonOut = (CableReportJson) msgCaptor.getValue().getExtension();
    Assert.assertNotNull(cableReportJsonOut.getValidityTime());
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Matchers.eq("ModelServiceUrl"), Matchers.eq(HttpMethod.POST), Mockito.any(),
        Matchers.eq(String.class), Mockito.anyInt());
  }

  private CableInfoEntryJson newInfoEntry(DefaultTextType type, String additionalInfo) {
    CableInfoEntryJson cie = new CableInfoEntryJson();
    cie.setType(type);
    cie.setAdditionalInfo(additionalInfo);
    return cie;
  }

  @Test
  public void testGetDecision() {
    byte[] mockData = new byte[123];
    for (int i = 0; i < mockData.length; ++i) {
      mockData[i] = (byte) i;
    }
    Mockito.when(restTemplate.getForObject(Matchers.eq("ModelServiceUrl"), Matchers.eq(byte[].class), Mockito.anyInt()))
        .thenReturn(mockData);

    byte[] decision = decisionService.getDecision(911);

    Assert.assertArrayEquals(mockData, decision);
  }

  @Test
  public void testPostalAddress() {
    PostalAddressJson postalAddressJson = new PostalAddressJson("Aapakatu 12", "123456", "Aapala");
    Assert.assertEquals("Aapakatu 12, 123456 Aapala", decisionService.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson("", null, "Apaa");
    Assert.assertEquals("Apaa", decisionService.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson("Syrjäpolku 3", null, null);
    Assert.assertEquals("Syrjäpolku 3", decisionService.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson("Yypöntie 1", null, "Ypäjä");
    Assert.assertEquals("Yypöntie 1, Ypäjä", decisionService.postalAddress(postalAddressJson));
    postalAddressJson = new PostalAddressJson(null, null, null);
    Assert.assertEquals("", decisionService.postalAddress(postalAddressJson));
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
