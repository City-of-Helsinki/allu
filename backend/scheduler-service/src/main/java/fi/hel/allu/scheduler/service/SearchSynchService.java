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
public class SearchSynchService {
  private static final Logger logger = LoggerFactory.getLogger(SearchSynchService.class);

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AuthenticationService authenticationService;

  @Autowired
  public SearchSynchService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
      AuthenticationService authenticationService) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.authenticationService = authenticationService;
  }

  /**
   * Start syncing data from model service to search service
   */
  public void startSearchSync() {
    logger.info("Start Search sync");
    restTemplate.exchange(applicationProperties.getStartSearchSyncUrl(), HttpMethod.POST,
        new HttpEntity<>(authenticationService.createAuthenticationHeader()), Void.class);
  }

}
