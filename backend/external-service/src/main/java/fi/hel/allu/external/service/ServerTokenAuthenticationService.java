package fi.hel.allu.external.service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ExternalUserJson;
import fi.hel.allu.servicecore.security.TokenUtil;
import fi.hel.allu.servicecore.security.UserAuthentication;
import fi.hel.allu.servicecore.service.AuthenticationServiceInterface;
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

@Service
public class ServerTokenAuthenticationService extends AuthenticationServiceInterface {

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
        try {
          ExternalUserJson externalUser = externalUserService.findUserByUserName(user.getUsername());
          if (externalUser.getToken().equals(parsedToken) && externalUser.getActive()) {
            externalUserService.setLastLogin(externalUser.getId(), ZonedDateTime.now());
            return new UserAuthentication(user);
          } else {
            logger.warn("Attempted login with inactive user or not matching token. Username: {}", user.getUsername());
          }
        } catch (NoSuchEntityException e) {
          logger.error("Login with valid token, but user is missing: {}", user.getUsername());
        }
      }
    }
    return null;
  }

  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    final AntPathMatcher antPathMatcher = new AntPathMatcher();
    return coreApplicationProperties.getAnonymousAccessPaths().stream().anyMatch(p -> antPathMatcher.match(p, path));
  }
}
