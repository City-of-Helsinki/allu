package fi.hel.allu.external.config;

import java.util.Collections;
import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

  private final String jwtSecret;
  private final String serviceAuth;
  private final Integer jwtExpirationTime;
  private final List<String> v1ExcludedApplicationKinds;

  @Autowired
  public ApplicationProperties(@Value("${jwt.secret}") @NotEmpty String jwtSecret,
      @Value("${service.authkey}") @NotEmpty String serviceAuth,
      @Value("${jwt.expirationtime}") @NotEmpty Integer jwtExpirationTime,
      @Value("#{'${v1.api.kinds.excluded:}'.split(',')}") List<String> v1ExcludedApplicationKinds) {
    this.jwtSecret = jwtSecret;
    this.serviceAuth = serviceAuth;
    this.jwtExpirationTime = jwtExpirationTime;
    this.v1ExcludedApplicationKinds = v1ExcludedApplicationKinds;
  }

  /**
   * Returns JWT secret key used to sign tokens.
   *
   * @return  JWT secret key used to sign tokens.
   */
  public String getJwtSecret() {
    return jwtSecret;
  }

  /**
   * Return the auth token for the service user
   *
   * @return auth token
   */
  public String getServiceAuth() {
    return serviceAuth;
  }

  /**
   * Gets JWT expiration time in minutes
   */
  public Integer getJwtExpirationTime() {
    return jwtExpirationTime;
  }

  public List<String> getV1ExcludedApplicationKinds() {
    return v1ExcludedApplicationKinds != null ? v1ExcludedApplicationKinds : Collections.emptyList();
  }

}
