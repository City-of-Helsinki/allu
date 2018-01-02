package fi.hel.allu.scheduler.service;

import fi.hel.allu.scheduler.config.ApplicationProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;

/**
 * Service for for authenticating to external-service.
 */
@Service
public class AuthenticationService {
  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private String bearerToken;

  @Autowired
  public AuthenticationService(RestTemplate restTemplate, ApplicationProperties applicationProperties) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
  }

  /**
   * Request authentication token from remote server. Uses the pre-set service
   * authentication (basic auth) token to get a JWT token.
   */
  public void requestToken() {
    HttpHeaders requestHeaders = new HttpHeaders();
    requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    requestHeaders.set(HttpHeaders.AUTHORIZATION, "Basic " + applicationProperties.getServiceAuth());
    HttpEntity<String> requestEntity = new HttpEntity<>("grant_type=client_credentials", requestHeaders);
    ResponseEntity<Properties> responseEntity = restTemplate.exchange(applicationProperties.getTokenRequestUrl(),
        HttpMethod.POST, requestEntity, Properties.class);
    bearerToken = responseEntity.getBody().getProperty("access_token", null);
  }

  /**
   * Get the JWT token that was read from the server in {@link requestToken}.
   *
   * @return JWT token previously read from server.
   */
  public String getBearerToken() {
    return bearerToken;
  }

  public HttpHeaders createAuthenticationHeader() {
    requestToken();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + getBearerToken());
    return headers;
  }

}
