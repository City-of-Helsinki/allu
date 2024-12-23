package fi.hel.allu.scheduler.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.scheduler.config.ApplicationProperties;

/**
 * Updates application statuses
 * - finished status for finished applications.
 * - terminated status for applications which has reached termination date
 */
@Service
public class ApplicationStatusUpdaterService {

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AuthenticationService authenticationService;
  private final Logger logger = LoggerFactory.getLogger(ApplicationStatusUpdaterService.class);

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
    logger.info("Updating statuses for finished applications");
    restTemplate.exchange(
        applicationProperties.getUpdateFinishedApplicationsUrl(), HttpMethod.PATCH,
        new HttpEntity<>(null, authenticationService.createAuthenticationHeader()), Void.class).getBody();
  }

  public void archiveApplications(List<Integer> applicationIds) {
    restTemplate.exchange(
        applicationProperties.getArchiveApplicationsUrl(), HttpMethod.PATCH,
        new HttpEntity<>(applicationIds, authenticationService.createAuthenticationHeader()), Void.class).getBody();
  }

  public void archiveApplicationStatusesForTerminated() {
    logger.info("Archiving terminated applications");
    restTemplate.exchange(
        applicationProperties.getUpdateTerminatedApplicationsUrl(), HttpMethod.PATCH,
        new HttpEntity<>(null, authenticationService.createAuthenticationHeader()), Void.class).getBody();
  }

  public void checkAnonymizableApplications() {
    logger.info("Checking for anonymizable applications");
    restTemplate.exchange(
      applicationProperties.getCheckAnonymizableApplicationsUrl(), HttpMethod.PATCH,
      new HttpEntity<>(null, authenticationService.createAuthenticationHeader()), Void.class).getBody();
  }
}
