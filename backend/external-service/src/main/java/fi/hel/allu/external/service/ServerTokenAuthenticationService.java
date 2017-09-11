package fi.hel.allu.external.service;

import fi.hel.allu.external.config.ApplicationProperties;
import fi.hel.allu.servicecore.security.TokenUtil;
import fi.hel.allu.servicecore.security.UserAuthentication;
import fi.hel.allu.servicecore.service.AuthenticationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;

@Service
public class ServerTokenAuthenticationService extends AuthenticationServiceInterface {

  private final TokenUtil tokenUtil;
  private final ApplicationProperties applicationProperties;
  private final fi.hel.allu.servicecore.config.ApplicationProperties coreApplicationProperties;

  @Autowired
  public ServerTokenAuthenticationService(
      ApplicationProperties applicationProperties,
      fi.hel.allu.servicecore.config.ApplicationProperties coreApplicationProperties) {
    this.applicationProperties = applicationProperties;
    this.tokenUtil = new TokenUtil(applicationProperties.getJwtSecret());
    this.coreApplicationProperties = coreApplicationProperties;
  }

  @Override
  public Authentication getAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token != null && token.startsWith("Bearer ")) {
      // TODO: retrieved user should be validated against database to make sure the user is still allowed to access external service
      final User user = tokenUtil.parseUserFromToken(
          TokenUtil.PROPERTY_ROLE_ALLU_PUBLIC,
          token.replaceFirst("^Bearer ", ""));
      if (user != null) {
        return new UserAuthentication(user);
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
