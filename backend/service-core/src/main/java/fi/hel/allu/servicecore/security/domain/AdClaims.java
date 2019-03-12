package fi.hel.allu.servicecore.security.domain;

import java.util.List;

import io.jsonwebtoken.Claims;

public class AdClaims {

  private static final String AD_USER_NAME = "upn";
  private static final String AD_REAL_NAME = "name";
  private static final String AD_EMAIL = "email";
  private static final String AD_GROUP = "groups";
  private Claims claims;

  public AdClaims(Claims claims) {
    this.claims = claims;
  }

  public String getUserName() {
    return claims.get(AD_USER_NAME, String.class);
  }

  public String getRealName() {
    return claims.get(AD_REAL_NAME, String.class);
  }

  public String getEmail() {
    return claims.get(AD_EMAIL, String.class);
  }

  public boolean isInGroup(String groupId) {
    return claims.get(AD_GROUP, List.class).contains(groupId);
  }
}
