package fi.hel.allu.servicecore.security;

import java.util.List;

public interface AdfsAuthenticationProperties {

  String getOauth2Certificate();

  String getJwtSecret();

  List<String> getAnonymousAccessPaths();

  Integer getJwtExpirationHours();

  String getOauth2ClientId();

  String getOauth2RedirectUri();

  String getOauth2TokenUrl();

}
