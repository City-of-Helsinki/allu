package fi.hel.allu.supervision.api.config;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fi.hel.allu.servicecore.security.AdAuthenticationProperties;

@Component
public class ApplicationProperties implements AdAuthenticationProperties {

  private final String jwtSecret;
  private final Integer jwtExpirationHours;
  private final String oauth2TokenUrl;
  private final String oauth2ClientId;
  private final String oauth2RedirectUri;
  private final String oauth2JwksUri;
  private final String oauth2ClientSecret;
  private final String alluAdGroupId;
  private final List<String> anonymousAccessPaths;

  @Autowired
  public ApplicationProperties(@Value("${jwt.secret}") @NotEmpty String jwtSecret,
      @Value("${jwt.expiration.hours:12}") @NotNull Integer jwtExpirationHours,
      @Value("${oauth2.url.token}") @NotEmpty String oauth2TokenUrl,
      @Value("${oauth2.clientid}") @NotEmpty String oauth2ClientId,
      @Value("${oauth2.redirect.uri}") @NotEmpty String oauth2RedirectUri,
      @Value("${oauth2.jwks_uri}") @NotEmpty String oauth2JwksUri,
      @Value("${oauth2.clientsecret}") @NotEmpty String oauth2ClientSecret,
      @Value("${ad.allu.group.id}") @NotEmpty String alluAdGroupId,
      @Value("#{'${anonymous.access.paths:}'.split(',')}") @NotNull List<String> anonymousAccessPaths) {
    this.jwtSecret = jwtSecret;
    this.jwtExpirationHours = jwtExpirationHours;
    this.oauth2TokenUrl = oauth2TokenUrl;
    this.oauth2ClientId = oauth2ClientId;
    this.oauth2RedirectUri = oauth2RedirectUri;
    this.oauth2JwksUri = oauth2JwksUri;
    this.oauth2ClientSecret = oauth2ClientSecret;
    this.alluAdGroupId = alluAdGroupId;
    this.anonymousAccessPaths = anonymousAccessPaths;
  }

  @Override
  public String getJwtSecret() {
    return jwtSecret;
  }

  @Override
  public List<String> getAnonymousAccessPaths() {
    return anonymousAccessPaths;
  }

  @Override
  public Integer getJwtExpirationHours() {
    return jwtExpirationHours;
  }

  @Override
  public String getOauth2ClientId() {
    return oauth2ClientId;
  }

  @Override
  public String getOauth2RedirectUri() {
    return oauth2RedirectUri;
  }

  @Override
  public String getOauth2TokenUrl() {
    return oauth2TokenUrl;
  }

  @Override
  public String getOauth2JwksUri() {
    return oauth2JwksUri;
  }

  @Override
  public String getOauth2ClientSecret() {
    return oauth2ClientSecret;
  }

  @Override
  public String getAlluAdGroupId() {
    return alluAdGroupId;
  }
}
