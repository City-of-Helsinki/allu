package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.domain.DefaultText;
import fi.hel.allu.ui.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Service for manipulating default texts.
 */
@Service
public class DefaultTextService {

  @Autowired
  private ApplicationProperties applicationProperties;
  @Autowired
  private RestTemplate restTemplate;

  /**
   * Get default texts for given application type.
   *
   * @return Default texts for given application type. Never <code>null</code>.
   */
  public List<DefaultText> getDefaultTexts(ApplicationType applicationType) {
    ResponseEntity<DefaultText[]> response =
        restTemplate.getForEntity(applicationProperties.getDefaultTextListUrl(), DefaultText[].class, applicationType);
    return Arrays.asList(response.getBody());
  }

  /**
   * Add a default text for application type.
   *
   * @param defaultText   the new default text to add -- the ID field will be ignored.
   * @return the new default text with database generated ID.
   */
  public DefaultText create(DefaultText defaultText) {
    return restTemplate.postForObject(applicationProperties.getDefaultTextAddUrl(), defaultText, DefaultText.class);
  }

  /**
   * Update default text.
   *
   * @param id              ID of the text to update
   * @param defaultText     the new contents for the info
   * @return the updated default text.
   */
  public DefaultText update(int id, DefaultText defaultText) {
    restTemplate.put(applicationProperties.getDefaultTextUpdateUrl(), defaultText, id);
    ResponseEntity<DefaultText> response = restTemplate.getForEntity(applicationProperties.getDefaultTextByIdUrl(), DefaultText.class, id);
    return response.getBody();
  }

  /**
   * Delete default text.
   *
   * @param id    the ID of the default text to be removed.
   */
  public void delete(int id) {
    restTemplate.delete(applicationProperties.getDefaultTextDeleteUrl(), id);
  }
}
