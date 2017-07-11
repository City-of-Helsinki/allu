package fi.hel.allu.ui.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

  private boolean production;
  private String geocodeUrl;
  private String streetSearchUrl;
  private String wfsUsername;
  private String wfsPassword;
  private String oauth2AuthorizationEndpointUrl;
  private String oauth2TokenUrl;
  private String oauth2ClientId;
  private String oauth2RedirectUri;
  private String oauth2Certificate;

  @Autowired
  public ApplicationProperties(@Value("${production}") boolean production,
                               @Value("${wfs.template.street.geocode}") @NotEmpty String geocodeUrl,
                               @Value("${wfs.template.street.search}") @NotEmpty String streetSearchUrl,
                               @Value("${wfs.username}") @NotEmpty String wfsUsername,
                               @Value("${wfs.password}") @NotEmpty String wfsPassword,
                               @Value("${oauth2.url.authorization}") @NotEmpty String oauth2AuthorizationEndpointUrl,
                               @Value("${oauth2.url.token}") @NotEmpty String oauth2TokenUrl,
                               @Value("${oauth2.clientid}") @NotEmpty String oauth2ClientId,
                               @Value("${oauth2.redirect.uri}") @NotEmpty String oauth2RedirectUri,
                               @Value("${oauth2.x509.certificate}") @NotEmpty String oauth2Certificate) {
    this.production = production;
    this.geocodeUrl = geocodeUrl;
    this.streetSearchUrl = streetSearchUrl;
    this.wfsUsername = wfsUsername;
    this.wfsPassword = wfsPassword;
    this.oauth2AuthorizationEndpointUrl = oauth2AuthorizationEndpointUrl;
    this.oauth2TokenUrl = oauth2TokenUrl;
    this.oauth2ClientId = oauth2ClientId;
    this.oauth2RedirectUri = oauth2RedirectUri;
    this.oauth2Certificate = oauth2Certificate;
  }

  /**
   * @return  true, if system is running in production.
   */
  public boolean isProduction() {
    return production;
  }

  /**
   * Returns URL for geocoding a street address.
   * @return  Request URL for geocoding a street address.
   */
  public String getStreetGeocodeUrl() {
    return this.geocodeUrl;
  }

  /**
   * @return  url to search streets.
   */
  public String getStreetSearchUrl() {
    return this.streetSearchUrl;
  }

  /**
   * Returns username for the WFS service.
   *
   * @return username for the WFS service.
   */
  public String getWfsUsername() {
    return wfsUsername;
  }

  /**
   * Returns password for the WFS service.
   *
   * @return password for the WFS service.
   */
  public String getWfsPassword() {
    return wfsPassword;
  }

  /**
   * @return  Returns the beginning of OAuth2 authorization endpoint URL, which should be extended with the proper redirect URI
   *          and client id.
   */
  public String getOauth2AuthorizationEndpointUrl() {
    return oauth2AuthorizationEndpointUrl;
  }

  /**
   * Returns the code for token exchange URI.
   *
   * @return  the code for token exchange URI.
   */
  public String getOauth2TokenUrl() {
    return oauth2TokenUrl;
  }

  /**
   * Returns the OAuth2 client id.
   *
   * @return  the OAuth2 client id.
   */
  public String getOauth2ClientId() {
    return oauth2ClientId;
  }

  /**
   * Returns the OAuth2 redirect uri.
   *
   * @return  the OAuth2 redirect uri.
   */
  public String getOauth2RedirectUri() {
    return oauth2RedirectUri;
  }

  /**
   * Returns the OAuth2 public certificate for verifying token signing.
   *
   * @return  the OAuth2 public certificate for verifying token signing.
   */
  public String getOauth2Certificate() {
    return oauth2Certificate;
  }
}
