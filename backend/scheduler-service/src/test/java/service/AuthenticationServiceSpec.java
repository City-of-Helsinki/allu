package service;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import fi.hel.allu.scheduler.service.AuthenticationService;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class AuthenticationServiceSpec {

  private static final String ACCESS_TOKEN = "ACCESS";
  @Mock
  private RestTemplate restTemplate;
  @Mock
  private ApplicationProperties applicationProperties;
  private AuthenticationService authenticationService;

  {
    describe("Authentication service", () -> {
      final ResponseEntity<Properties> mockResponse = createMockResponse();

      beforeEach(() -> {
        MockitoAnnotations.initMocks(this);
        authenticationService = new AuthenticationService(restTemplate, applicationProperties);
        Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST),
            Mockito.any(HttpEntity.class), Mockito.eq(Properties.class))).thenReturn(mockResponse);
        Mockito.when(applicationProperties.getServiceAuth()).thenReturn("service_auth");
        Mockito.when(applicationProperties.getTokenRequestUrl()).thenReturn("/token");
      });

      it("should send POST request and parse token from response", () -> {
        authenticationService.requestToken();
        Mockito.verify(restTemplate).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST),
            Mockito.any(HttpEntity.class), Mockito.eq(Properties.class));
        assertEquals(ACCESS_TOKEN, authenticationService.getBearerToken());
      });

    });
  }

  private ResponseEntity<Properties> createMockResponse() {
    Properties props = new Properties();
    props.setProperty("access_token", ACCESS_TOKEN);
    return new ResponseEntity<>(props, HttpStatus.OK);
  }
}
