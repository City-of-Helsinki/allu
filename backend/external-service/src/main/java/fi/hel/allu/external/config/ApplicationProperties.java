package fi.hel.allu.external.config;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {

  private String jwtSecret;

  @Autowired
  public ApplicationProperties(@Value("${jwt.secret}") @NotEmpty String jwtSecret) {
    this.jwtSecret = jwtSecret;
  }

  /**
   * Returns JWT secret key used to sign tokens.
   *
   * @return  JWT secret key used to sign tokens.
   */
  public String getJwtSecret() {
    return jwtSecret;
  }
}
