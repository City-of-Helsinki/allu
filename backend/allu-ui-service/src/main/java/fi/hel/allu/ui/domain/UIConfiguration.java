package fi.hel.allu.ui.domain;

/**
 * Configuration information used by Allu UI.
 */
public class UIConfiguration {
  public Boolean production;
  public String oauth2AuthorizationEndpointUrl;
  public String versionNumber;

  /**
   * @return true, if executing in production system.
   */
  public Boolean getProduction() {
    return production;
  }

  public void setProduction(Boolean production) {
    this.production = production;
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
