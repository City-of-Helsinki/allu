package fi.hel.allu.scheduler.service;

import fi.hel.allu.scheduler.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CityDistrictUpdaterService {

  private final RestTemplate restTemplate;
  private final ApplicationProperties applicationProperties;
  private final AuthenticationService authenticationService;

  @Autowired
  public CityDistrictUpdaterService(RestTemplate restTemplate, ApplicationProperties applicationProperties,
                                         AuthenticationService authenticationService) {
    this.restTemplate = restTemplate;
    this.applicationProperties = applicationProperties;
    this.authenticationService = authenticationService;
  }

  public void updateCityDistricts() {
    restTemplate.put(
      applicationProperties.getUpdateCityDistrictsUrl(),
      new HttpEntity<>(authenticationService.createAuthenticationHeader()),
      Void.class);
  }
}
