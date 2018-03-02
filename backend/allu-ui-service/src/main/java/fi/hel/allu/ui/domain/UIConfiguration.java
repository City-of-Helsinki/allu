package fi.hel.allu.ui.domain;

import fi.hel.allu.ui.config.Environment;

/**
 * Configuration information used by Allu UI.
 */
public class UIConfiguration {
  public Environment environment;
  public String oauth2AuthorizationEndpointUrl;
  public String versionNumber;

  /**
   * @return Current environment
   */
  public Environment getEnvironment() {
    return environment;
  }

  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  /**
   * @return OAuth2 authorization endpoint URL i.e. the URL (OAuth2 service) where user is redirected for authentication and authorization.
   */
  public String getOauth2AuthorizationEndpointUrl() {
    return oauth2AuthorizationEndpointUrl;
  }

  public void setOauth2AuthorizationEndpointUrl(String oauth2AuthorizationEndpointUrl) {
    this.oauth2AuthorizationEndpointUrl = oauth2AuthorizationEndpointUrl;
  }

  /**
   * @return Version number
   */
  public String getVersionNumber() {
    return versionNumber;
  }

  public void setVersionNumber(String versionNumber) {
    this.versionNumber = versionNumber;
  }
}
