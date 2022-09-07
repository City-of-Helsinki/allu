package fi.hel.allu.external.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
public class ApplicationProperties {

    private final String jwtSecret;
    private final String serviceAuth;
    private final Integer jwtExpirationTime;

    @Autowired
    public ApplicationProperties(@Value("${jwt.secret}") @NotEmpty String jwtSecret,
                                 @Value("${service.authkey}") @NotEmpty String serviceAuth,
                                 @Value("${jwt.expirationtime}") @NotEmpty Integer jwtExpirationTime) {
        this.jwtSecret = jwtSecret;
        this.serviceAuth = serviceAuth;
        this.jwtExpirationTime = jwtExpirationTime;
    }

    /**
     * Returns JWT secret key used to sign tokens.
     *
     * @return JWT secret key used to sign tokens.
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

}
