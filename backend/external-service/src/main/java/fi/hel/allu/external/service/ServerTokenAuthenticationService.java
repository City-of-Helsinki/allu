package fi.hel.allu.external.service;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.security.TokenUtil;
import fi.hel.allu.servicecore.security.UserAuthentication;
import fi.hel.allu.servicecore.service.AuthenticationServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Service
public class ServerTokenAuthenticationService extends AuthenticationServiceInterface {

  public static final String ROLES = "publicAlluRoles";

  @Autowired
  ApplicationProperties applicationProperties;

  @Override
  public Authentication getAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token != null && token.startsWith("Bearer ")) {
      try {
        // TODO: retrieved user should be validated against database to make sure the user is still allowed to access external service
        final User user = TokenUtil.parseUserFromToken(
            new String(java.util.Base64.getEncoder().encode(applicationProperties.getJwtSecret().getBytes()), "UTF-8"),
            ROLES,
            token.replaceFirst("^Bearer ", ""));
        if (user != null) {
          return new UserAuthentication(user);
        }
      } catch (UnsupportedEncodingException e) {
        // this should never happen
        throw new RuntimeException(e);
      }
    }
    return null;
  }

  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    final AntPathMatcher antPathMatcher = new AntPathMatcher();
    return applicationProperties.getAnonymousAccessPaths().stream().anyMatch(p -> antPathMatcher.match(p, path));
  }
}
