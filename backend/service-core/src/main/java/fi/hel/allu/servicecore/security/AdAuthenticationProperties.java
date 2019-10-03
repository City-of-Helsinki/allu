package fi.hel.allu.servicecore.security;

import java.util.List;

public interface AdAuthenticationProperties {

  String getJwtSecret();

  List<String> getAnonymousAccessPaths();

  Integer getJwtExpirationHours();

  String getOauth2ClientId();

  String getOauth2RedirectUri();

  String getOauth2TokenUrl();

  String getOauth2JwksUri();

  String getOauth2ClientSecret();

  String getAlluAdGroupId();
}
