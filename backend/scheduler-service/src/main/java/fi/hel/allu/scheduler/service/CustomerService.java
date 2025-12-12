package fi.hel.allu.scheduler.service;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
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

  public void checkAndStoreDeletableCustomers() {
    restTemplate.exchange(
      applicationProperties.getCheckAndStoreDeletableCustomersUrl(),
      HttpMethod.POST,
      new HttpEntity<>(
        authenticationService.createAuthenticationHeader()), Void.class
    );
  }
}
