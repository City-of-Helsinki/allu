package fi.hel.allu.ui.service;

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

  @Test
  public void testGenerateDecision() throws IOException {
    // Setup mocks
    byte[] mockData = new byte[123];
    Mockito.when(restTemplate.postForObject(Mockito.anyString(), Mockito.anyObject(), Mockito.eq(byte[].class),
        Mockito.anyString())).thenReturn(mockData);
    ResponseEntity<String> mockResponse = new ResponseEntity<>(HttpStatus.CREATED);
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST), Mockito.any(),
        Mockito.eq(String.class), Mockito.anyInt())).thenReturn(mockResponse);

    // Call the method under test
    decisionService.generateDecision(123, new ApplicationJson());

    // Verify that some important REST calls were made:
    // - PDF creation was executed:
    Mockito.verify(restTemplate).postForObject(Mockito.eq("PdfServiceUrl"), Mockito.anyObject(),
        Mockito.eq(byte[].class),
        Mockito.anyString());
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
