package fi.hel.allu.servicecore.service;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.common.types.DefaultTextType;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.pdf.domain.CableInfoTexts;
import fi.hel.allu.pdf.domain.ChargeInfoTexts;
import fi.hel.allu.pdf.domain.DecisionJson;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.AnonymizedDecisionJsonMapper;
import fi.hel.allu.servicecore.mapper.CustomerAnonymizer;
import fi.hel.allu.servicecore.mapper.DecisionJsonMapper;

import static org.mockito.Mockito.atLeast;

@RunWith(MockitoJUnitRunner.class)
public class DecisionServiceTest {

  private static final String STORE_DECISION_URL = "StoreDecisionUrl";
  private static final String DECISION_URL = "DecisionUrl";
  private static final String ANONYMIZED_DECISION_URL = "anonymizedDecisionUrl";
  private static final String GENERATE_PDF_URL = "GeneratePdfUrl";
  private static final byte[] MOCK_PDF_DATA = StringUtils.repeat("MockPdfData", 100).getBytes();
  private static final byte[] MOCK_DECISION_DATA = StringUtils.repeat("MockDecision", 100).getBytes();

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
  @Mock
  private MetaService metaService;

  private DecisionService decisionService;


  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getGeneratePdfUrl()).thenReturn(GENERATE_PDF_URL);
    Mockito.when(applicationProperties.getStoreDecisionUrl()).thenReturn(STORE_DECISION_URL);
    Mockito.when(applicationProperties.getAnonymizedDecisionUrl()).thenReturn(ANONYMIZED_DECISION_URL);
    Mockito.when(applicationProperties.getDecisionUrl()).thenReturn(DECISION_URL);

    final UserJson owner = new UserJson();
    owner.setId(1);
    owner.setRealName("Task owner");
    ApplicationJson application = new ApplicationJson();
    application.setDecisionTime(ZonedDateTime.now());
    Mockito.when(applicationServiceComposer.findApplicationById(Mockito.anyInt())).thenReturn(application);

    DecisionJsonMapper decisionMapper = new DecisionJsonMapper(locationService, customerService, contactService, chargeBasisService, metaService);
    AnonymizedDecisionJsonMapper anonymizedDecisionMapper = new AnonymizedDecisionJsonMapper(locationService, customerService, contactService, chargeBasisService, metaService,
        new CustomerAnonymizer());
    decisionService = new DecisionService(applicationProperties, restTemplate,
        applicationServiceComposer, decisionMapper, anonymizedDecisionMapper);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateWithEmptyApplication() throws IOException {
    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);
  }

  private void setupRestMocks() {
    // Setup mocks
    Mockito.when(restTemplate.postForObject(Mockito.eq(GENERATE_PDF_URL), Mockito.any(), Mockito.eq(byte[].class),
        Mockito.anyString())).thenReturn(MOCK_PDF_DATA);
    Mockito.when(restTemplate.exchange(Mockito.eq(STORE_DECISION_URL), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt())).thenReturn(new ResponseEntity<>(HttpStatus.CREATED));
    Mockito.when(restTemplate.exchange(Mockito.eq(ANONYMIZED_DECISION_URL), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
    Mockito.when(restTemplate.getForObject(Mockito.eq(DECISION_URL), Mockito.eq(byte[].class), Mockito.anyInt()))
        .thenReturn(MOCK_DECISION_DATA);
  }

  @Test
  public void testGenerateShortTermRental() throws IOException {
    setupRestMocks();

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setId(123);
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name :
    Mockito.verify(restTemplate, atLeast(1)).postForObject(Mockito.eq(GENERATE_PDF_URL), Mockito.any(),
        Mockito.eq(byte[].class),
        Mockito.eq("SHORT_TERM_RENTAL"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Mockito.eq(STORE_DECISION_URL), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt());
  }

  @Test
  public void testGenerateEvent() throws IOException {
    setupRestMocks();

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setId(123);
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.EVENT);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name:
    Mockito.verify(restTemplate, atLeast(1)).postForObject(Mockito.eq(GENERATE_PDF_URL), Mockito.any(),
        Mockito.eq(byte[].class), Mockito.eq("EVENT"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Mockito.eq(STORE_DECISION_URL), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt());
  }

  @Test
  public void testChargeInfoGeneration() {
    setupRestMocks();
    final List<ChargeBasisEntry> ENTRIES = Arrays.asList(
        new ChargeBasisEntry("TAG1", null, false, ChargeBasisType.CALCULATED, ChargeBasisUnit.DAY, 14.0, "Two weeks",
            null, 10000, 140000),
        new ChargeBasisEntry("TAG2", null, false, ChargeBasisType.CALCULATED, ChargeBasisUnit.YEAR, 1.0, "One year",
            null, 999900, 999900),
        new ChargeBasisEntry(null, "TAG1", true, ChargeBasisType.DISCOUNT, ChargeBasisUnit.PERCENT, -10.0,
            "10% discount", null, 0, 0),
        new ChargeBasisEntry("TAG3", null, false, ChargeBasisType.CALCULATED, ChargeBasisUnit.SQUARE_METER, 1.0,
            "One sqm", null, 100, 100));
    Mockito.when(chargeBasisService.getInvoicableChargeBasis(Mockito.anyInt())).thenReturn(ENTRIES);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    applicationJson.setId(123);
    // Call the method under test
    decisionService.getDecisionPreview(applicationJson);

    // - PDF creation was executed with the right stylesheet name :
    final ArgumentCaptor<DecisionJson> jsonCaptor = ArgumentCaptor.forClass(DecisionJson.class);
    Mockito.verify(restTemplate).postForObject(Mockito.eq(GENERATE_PDF_URL), jsonCaptor.capture(),
        Mockito.eq(byte[].class), Mockito.eq("SHORT_TERM_RENTAL"));
    // - Sent JSON object contains chargeInfoEntries:
    DecisionJson decisionJson = jsonCaptor.getValue();
    List<ChargeInfoTexts> items = decisionJson.getChargeInfoEntries();

    // Some sanity checks about ordering:
    Assert.assertEquals(ENTRIES.get(0).getText(), items.get(0).getText());
    Assert.assertEquals(ENTRIES.get(2).getText(), items.get(1).getText());
    Assert.assertEquals(ENTRIES.get(1).getText(), items.get(2).getText());
    Assert.assertEquals(ENTRIES.get(3).getText(), items.get(3).getText());

    Assert.assertEquals(0, items.get(0).getLevel());
    Assert.assertEquals(1, items.get(1).getLevel());

  }

  @Test
  public void testAreaGeneration() {
    setupRestMocks();

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setCustomersWithContacts(createDummyCustomersWithContactsJson());
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    applicationJson.setId(123);

    LocationJson loc = new LocationJson();
    loc.setArea(500.0);
    loc.setAreaOverride(1000.0);
    applicationJson.setLocations(Collections.singletonList(loc));
    // Call the method under test
    decisionService.getDecisionPreview(applicationJson);

    // - PDF creation was executed with the right stylesheet name :
    final ArgumentCaptor<DecisionJson> jsonCaptor = ArgumentCaptor.forClass(DecisionJson.class);
    Mockito.verify(restTemplate).postForObject(Mockito.eq(GENERATE_PDF_URL), jsonCaptor.capture(),
        Mockito.eq(byte[].class), Mockito.eq("SHORT_TERM_RENTAL"));
    // - Sent JSON object contains chargeInfoEntries:
    DecisionJson decisionJson = jsonCaptor.getValue();
    String siteArea = decisionJson.getSiteArea();

    Assert.assertEquals(String.format("%.0f", loc.getAreaOverride()), siteArea);
  }

  @Test
  public void testGenerateCableReport() throws IOException {
    final int MAP_EXCTRACT_COUNT = 93;

    setupRestMocks();

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
    Mockito.verify(restTemplate, atLeast(1)).postForObject(Mockito.eq(GENERATE_PDF_URL), jsonCaptor.capture(),
        Mockito.eq(byte[].class),
        Mockito.eq("CABLE_REPORT"));
    // - Sent JSON object contains field cableInfoEntries
    DecisionJson decisionJson = jsonCaptor.getValue();
    List<CableInfoTexts> infoEntries = decisionJson.getCableInfoEntries();
    Assert.assertNotNull(infoEntries);
    Assert.assertEquals(3, infoEntries.size());
    Assert.assertEquals(MAP_EXCTRACT_COUNT, decisionJson.getMapExtractCount());
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Mockito.eq(STORE_DECISION_URL), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt());
  }

  private CableInfoEntryJson newInfoEntry(DefaultTextType type, String additionalInfo) {
    CableInfoEntryJson cie = new CableInfoEntryJson();
    cie.setType(type);
    cie.setAdditionalInfo(additionalInfo);
    return cie;
  }

  @Test
  public void testGetDecision() {
    setupRestMocks();

    byte[] decision = decisionService.getDecision(911);

    Assert.assertArrayEquals(MOCK_DECISION_DATA, decision);
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
