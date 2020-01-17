package fi.hel.allu.ui.config;

import java.util.List;

import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.hel.allu.servicecore.security.AdAuthenticationProperties;

@Component
public class ApplicationProperties implements AdAuthenticationProperties {

  @Value("${environment}")
  private Environment environment;
  @Value("${version.number}")
  private String versionNumber;
  @NotEmpty
  @Value("${wfs.template.street.geocode}")
  private String geocodeUrl;
  @NotEmpty
  @Value("${wfs.template.street.search}")
  private String streetSearchUrl;
  @NotEmpty
  @Value("${wfs.username}")
  private String wfsUsername;
  @NotEmpty
  @Value("${wfs.password}")
  private String wfsPassword;
  @NotEmpty
  @Value("${jwt.secret}")
  private String jwtSecret;
  @NotNull
  @Value("${jwt.expiration.hours:12}")
  private Integer jwtExpirationHours;
  @NotEmpty
  @Value("${jwt.secret.external.service}")
  private String jwtSecretExternalService;
  @NotEmpty
  @Value("${oauth2.url.authorization}")
  private String oauth2AuthorizationEndpointUrl;
  @NotEmpty
  @Value("${oauth2.url.token}")
  private String oauth2TokenUrl;
  @NotEmpty
  @Value("${oauth2.clientid}")
  private String oauth2ClientId;
  @NotEmpty
  @Value("${oauth2.redirect.uri}")
  private String oauth2RedirectUri;
  @NotEmpty
  @Value("${oauth2.jwks_uri}")
  private String oauth2JwksUri;
  @NotEmpty
  @Value("${oauth2.clientsecret}")
  private String oauth2ClientSecret;
  @NotEmpty
  @Value("${ad.allu.group.id}")
  private String alluAdGroupId;
  @NotNull
  @Value("#{'${anonymous.access.paths:}'.split(',')}")
  private List<String> anonymousAccessPaths;
  @NotEmpty
  @Value("${wfs.userAreas.url}")
  private String wfsUserAreasUrl;

  /**
   * @return Current environment
   */
  public Environment getEnvironment() {
    return environment;
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
   * Returns JWT secret key used to sign tokens.
   *
   * @return  JWT secret key used to sign tokens.
   */
  @Override
  public String getJwtSecret() {
    return jwtSecret;
  }

  /**
   * Returns the expiration time of JWT.
   *
   * @return  the expiration time of JWT.
   */
  @Override
  public Integer getJwtExpirationHours() {
    return jwtExpirationHours;
  }

  /**
   * Returns JWT secret key used to sign tokens used in external-service.
   *
   * @return  JWT secret key used to sign tokens used in external-service.
   */
  public String getJwtSecretExternalService() {
    return jwtSecretExternalService;
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
  @Override
  public String getOauth2TokenUrl() {
    return oauth2TokenUrl;
  }

  /**
   * Returns the OAuth2 client id.
   *
   * @return  the OAuth2 client id.
   */
  @Override
  public String getOauth2ClientId() {
    return oauth2ClientId;
  }

  /**
   * Returns the OAuth2 redirect uri.
   *
   * @return  the OAuth2 redirect uri.
   */
  @Override
  public String getOauth2RedirectUri() {
    return oauth2RedirectUri;
  }

  @Override
  public String getOauth2JwksUri() {
    return oauth2JwksUri;
  }

  @Override
  public String getOauth2ClientSecret() {
    return oauth2ClientSecret;
  }

  /**
   * Get list of (url) paths allowed to be accessed by anonymous users. Controller methods bound to these won't be checked against normal
   * security measures.
   *
   * @return list of (url) paths allowed to be accessed by anonymous users.
   */
  public List<String> getAnonymousAccessPaths() {
    return anonymousAccessPaths;
  }

  /**
   * Get version number
   */
  public String getVersionNumber() {
    return versionNumber;
  }

  @Override
  public String getAlluAdGroupId() {
    return alluAdGroupId;
  }

  public String getWfsUserAreasUrl() {
    return wfsUserAreasUrl;
  }
}
