package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ChargeBasisService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  @Autowired
  public ChargeBasisService(ApplicationProperties applicationProperties, RestTemplate restTemplate) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  /**
   * Get the charge basis entries for an application
   *
   * @param applicationId the application ID
   * @return the charge basis entries for the application
   */
  public List<ChargeBasisEntry> getChargeBasis(int applicationId) {
    ResponseEntity<ChargeBasisEntry[]> restResult = restTemplate.getForEntity(applicationProperties.getChargeBasisUrl(),
        ChargeBasisEntry[].class, applicationId);
    return Arrays.asList(restResult.getBody());
  }

  /**
   * Set the manual charge basis entries for an application
   *
   * @param applicationId the application ID
   * @param chargeBasisEntries the charge basis entries to store. Only entries
   *          that are marked as manually set will be used
   * @return the new charge basis entries for the application
   */
  public List<ChargeBasisEntry> setChargeBasis(int applicationId, List<ChargeBasisEntry> chargeBasisEntries) {
    HttpEntity<List<ChargeBasisEntry>> requestEntity = new HttpEntity<>(chargeBasisEntries);
    ResponseEntity<ChargeBasisEntry[]> restResult = restTemplate.exchange(applicationProperties.setChargeBasisUrl(),
        HttpMethod.PUT, requestEntity, ChargeBasisEntry[].class, applicationId);
    return Arrays.asList(restResult.getBody());
  }
}
