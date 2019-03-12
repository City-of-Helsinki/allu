package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.UIConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Controller for providing environment dependent configuration information to user interface.
 */
@RestController
@RequestMapping("/uiconfig")
public class ConfigController {

  private final UIConfiguration uiConfiguration;

  @Autowired
  public ConfigController(ApplicationProperties applicationProperties) {
    uiConfiguration = new UIConfiguration();
    uiConfiguration.setEnvironment(applicationProperties.getEnvironment());
    uiConfiguration.setOauth2AuthorizationEndpointUrl(createOAuthAuthorizationEndpointUrl(applicationProperties));
    uiConfiguration.setVersionNumber(applicationProperties.getVersionNumber());
  }

  /**
   * Returns the configuration for UI.
   * <p>Note that this method is accessible to anonymous and unauthorized users!
   *
   * @return the configuration for UI.
   */
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<UIConfiguration> getConfiguration() {
    return new ResponseEntity<>(this.uiConfiguration, HttpStatus.OK);
  }

  private String createOAuthAuthorizationEndpointUrl(ApplicationProperties applicationProperties) {
    try {
      return applicationProperties.getOauth2AuthorizationEndpointUrl() +
          "&client_id=" + applicationProperties.getOauth2ClientId() +
          "&redirect_uri=" + URLEncoder.encode(applicationProperties.getOauth2RedirectUri(), "UTF-8") +
          "&resource=" + applicationProperties.getOauth2ClientId();
    } catch (UnsupportedEncodingException e) {
      // should never happen...
      throw new RuntimeException(e);
    }
  }
}
