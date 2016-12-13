package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationJson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

@RunWith(MockitoJUnitRunner.class)
public class DecisionServiceTest {

  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  @InjectMocks
  private DecisionService decisionService;


  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(applicationProperties.getPdfServiceUrl(Mockito.anyString())).thenReturn("PdfServiceUrl");
    Mockito.when(applicationProperties.getModelServiceUrl(Mockito.anyString())).thenReturn("ModelServiceUrl");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGenerateWithEmptyApplication() throws IOException {
    // Call the method under test
    decisionService.generateDecision(123, new ApplicationJson());
  }

  @Test
  public void testGenerateShortTermRental() throws IOException {
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name :
    Mockito.verify(restTemplate).postForObject(Mockito.eq("PdfServiceUrl"), Mockito.anyObject(),
        Mockito.eq(byte[].class),
        Mockito.eq("SHORT_TERM_RENTAL"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Mockito.eq("ModelServiceUrl"), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt());
  }

  @Test
  public void testGenerateEvent() throws IOException {
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setType(ApplicationType.EVENT);
    // Call the method under test
    decisionService.generateDecision(123, applicationJson);

    // Verify that some important REST calls were made:
    // - PDF creation was executed with the right stylesheet name:
    Mockito.verify(restTemplate).postForObject(Mockito.eq("PdfServiceUrl"), Mockito.anyObject(),
        Mockito.eq(byte[].class), Mockito.eq("EVENT"));
    // - Generated PDF was stored to model:
    Mockito.verify(restTemplate).exchange(Mockito.eq("ModelServiceUrl"), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt());
  }

  @Test
  public void testGetDecision() {
    byte[] mockData = new byte[123];
    for (int i = 0; i < mockData.length; ++i) {
      mockData[i] = (byte) i;
    }
    Mockito.when(restTemplate.getForObject(Mockito.eq("ModelServiceUrl"), Mockito.eq(byte[].class), Mockito.anyInt()))
        .thenReturn(mockData);

    byte[] decision = decisionService.getDecision(911);

    assertArrayEquals(mockData, decision);
  }
}
