package fi.hel.allu.scheduler.service;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerService {

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AuthenticationService authenticationService;

  @Autowired
  public CustomerService(
    RestTemplate restTemplate,
    ApplicationProperties applicationProperties,
    AuthenticationService authenticationService
  ) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.authenticationService = authenticationService;
  }

  /**
   * Calls the model-service endpoint that scans the database for customers
   * eligible for permanent deletion and stores them into a staging table.
   *
   * Throws an exception if the remote service responds with a non-successful
   * HTTP status or if the request fails.
   */
  public void checkAndStoreDeletableCustomers() {
    restTemplate.exchange(
      applicationProperties.getCheckAndStoreDeletableCustomersUrl(),
      HttpMethod.POST,
      new HttpEntity<>(authenticationService.createAuthenticationHeader()),
      Void.class
    );
  }
}
