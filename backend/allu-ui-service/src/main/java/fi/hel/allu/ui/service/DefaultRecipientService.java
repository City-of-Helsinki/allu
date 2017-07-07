package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.DefaultRecipient;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.DefaultRecipientJson;
import fi.hel.allu.ui.mapper.DefaultRecipientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for manipulating default recipients
 */
@Service
public class DefaultRecipientService {

  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private DefaultRecipientMapper mapper;

  @Autowired
  public DefaultRecipientService(ApplicationProperties applicationProperties, RestTemplate restTemplate, DefaultRecipientMapper mapper) {
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
    this.mapper = mapper;
  }

  /**
   * Get default recipients.
   *
   * @return List of all default recipients
   */
  public List<DefaultRecipientJson> getDefaultRecipients() {
    ResponseEntity<DefaultRecipient[]> response =
            restTemplate.getForEntity(applicationProperties.getDefaultRecipientListUrl(), DefaultRecipient[].class);
    return Arrays.stream(response.getBody()).map(dr -> mapper.createJson(dr)).collect(Collectors.toList());
  }

  /**
   * Create a new default recipient. Id must be null.
   *
   * @param recipientJson the new default recipient to add.
   * @return the new default recipient with database generated ID.
   */
  public DefaultRecipientJson create(DefaultRecipientJson recipientJson) {
    DefaultRecipient model = restTemplate.postForObject(
            applicationProperties.getDefaultRecipientAddUrl(),
            mapper.createModel(recipientJson),
            DefaultRecipient.class);

    return mapper.createJson(model);
  }

  /**
   * Update default recipient text.
   *
   * @param id ID of the recipient to update
   * @param recipientJson the new contents for the info
   * @return the updated recipient.
   */
  public DefaultRecipientJson update(int id, DefaultRecipientJson recipientJson) {
    ResponseEntity<DefaultRecipient> response = restTemplate.exchange(
            applicationProperties.getDefaultRecipientUpdateUrl(),
            HttpMethod.PUT,
            new HttpEntity<>(mapper.createModel(recipientJson)),
            DefaultRecipient.class,
            id);

    return mapper.createJson(response.getBody());
  }

  /**
   * Delete default recipient.
   *
   * @param id the ID of the default recipient to be removed.
   */
  public void delete(int id) {
    restTemplate.delete(applicationProperties.getDefaultRecipientDeleteUrl(), id);
  }
}
