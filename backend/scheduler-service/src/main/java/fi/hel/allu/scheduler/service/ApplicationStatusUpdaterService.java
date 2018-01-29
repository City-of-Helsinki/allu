package fi.hel.allu.scheduler.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.scheduler.config.ApplicationProperties;

/**
 * Updates finished status for finished applications.
 */
@Service
public class ApplicationStatusUpdaterService {

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AuthenticationService authenticationService;

  @Autowired
  public ApplicationStatusUpdaterService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
      AuthenticationService authenticationService) {
    // Needed for PATCH support
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    restTemplate.setRequestFactory(requestFactory);
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.authenticationService = authenticationService;
  }

  public void updateApplicationStatuses() {
    restTemplate.exchange(
        applicationProperties.getUpdateFinishedApplicationsUrl(), HttpMethod.PATCH,
        new HttpEntity<>(null, authenticationService.createAuthenticationHeader()), Void.class).getBody();
  }

  public void archiveApplications(List<Integer> applicationIds) {
    restTemplate.exchange(
        applicationProperties.getArchiveApplicationsUrl(), HttpMethod.PATCH,
        new HttpEntity<>(applicationIds, authenticationService.createAuthenticationHeader()), Void.class).getBody();
  }
}
