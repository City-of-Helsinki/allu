package fi.hel.allu.external.service;

import fi.hel.allu.common.domain.types.ExternalRoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.model.domain.user.ExternalUser;
import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.security.AuthenticationServiceInterface;
import fi.hel.allu.servicecore.security.TokenUtil;
import fi.hel.allu.servicecore.security.UserAuthentication;
import fi.hel.allu.servicecore.service.ExternalUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class ServerTokenAuthenticationService extends AuthenticationServiceInterface {

  private static final int SERVICE_TOKEN_EXPIRATION_HOURS = 2400;
  private static final String SERVICE_USER = "service_user";
  private static final Logger logger = LoggerFactory.getLogger(ServerTokenAuthenticationService.class);

  private final TokenUtil tokenUtil;
  private final fi.hel.allu.servicecore.config.ApplicationProperties coreApplicationProperties;
  private final ExternalUserService externalUserService;


  @Autowired
  public ServerTokenAuthenticationService(
      ExternalUserService externalUserService,
      ApplicationProperties applicationProperties,
      fi.hel.allu.servicecore.config.ApplicationProperties coreApplicationProperties) {
    this.externalUserService = externalUserService;
    this.tokenUtil = new TokenUtil(applicationProperties.getJwtSecret());
    this.coreApplicationProperties = coreApplicationProperties;
  }

  @Override
  public Authentication getAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token != null && token.startsWith("Bearer ")) {
      final String parsedToken = token.replaceFirst("^Bearer ", "");
      final User user = tokenUtil.parseUserFromToken(
          TokenUtil.PROPERTY_ROLE_ALLU_PUBLIC,
          parsedToken);
      if (user != null) {
        if (user.getAuthorities().stream()
            .anyMatch(a -> ExternalRoleType.ROLE_SERVICE.toString().equals(a.getAuthority()))) {
          logger.debug("Login as a service");
          return new UserAuthentication(user);
        }
        try {
          ExternalUserJson externalUser = externalUserService.findUserByUserName(user.getUsername());
          if (accountNotExpired(externalUser)) {
            externalUserService.setLastLogin(externalUser.getId(), ZonedDateTime.now());
            return new UserAuthentication(user);
          } else {
            logger.warn("Attempted login with inactive user. Username: {}", user.getUsername());
          }
        } catch (NoSuchEntityException e) {
          logger.error("Login with valid token, but user is missing: {}", user.getUsername());
        }
      }
    }
    return null;
  }

  private boolean accountNotExpired(ExternalUserJson externalUser) {
    return externalUser.getActive() && externalUser.getExpirationTime().isAfter(ZonedDateTime.now());
  }

  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    final AntPathMatcher antPathMatcher = new AntPathMatcher();
    return coreApplicationProperties.getAnonymousAccessPaths().stream().anyMatch(p -> antPathMatcher.match(p, path));
  }

  public Properties createServiceToken() {
    ZonedDateTime dateTimeToConvert = ZonedDateTime.now().plusHours(SERVICE_TOKEN_EXPIRATION_HOURS);
    Map<String, Object> propertyNameToValue = new HashMap<>();
    propertyNameToValue.put(TokenUtil.PROPERTY_ROLE_ALLU_PUBLIC,
        Collections.singletonList(ExternalRoleType.ROLE_SERVICE));
    propertyNameToValue.put(TokenUtil.PROPERTY_EMAIL, "none@allu.invalid");
    String token = tokenUtil.createToken(dateTimeToConvert, SERVICE_USER, propertyNameToValue);
    Properties p = new Properties();
    p.put("access_token", token);
    p.put("token_type", "Bearer");
    p.put("expires_in", Integer.toString(3600 * SERVICE_TOKEN_EXPIRATION_HOURS));
    return p;
  }
}
