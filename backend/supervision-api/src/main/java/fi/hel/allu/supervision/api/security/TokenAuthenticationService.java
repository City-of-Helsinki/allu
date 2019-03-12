package fi.hel.allu.supervision.api.security;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.security.AadService;
import fi.hel.allu.servicecore.security.AdTokenAuthenticationService;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.config.ApplicationProperties;
import io.jsonwebtoken.JwtException;

@Service
public class TokenAuthenticationService extends AdTokenAuthenticationService {

  @Autowired
  public TokenAuthenticationService(ApplicationProperties properties, UserService userService, AadService aadService) {
    super(properties, userService, aadService);
  }

  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    final AntPathMatcher antPathMatcher = new AntPathMatcher();
    return getAnonymousAccessPaths().stream().anyMatch(p -> antPathMatcher.match(p, path));
  }

  public String loginWithAdToken(String adToken) {
    try {
      Optional<UserJson> user = authenticateWithAdToken(adToken);
      return user.map(u -> loginUser(u)).orElseThrow(() -> new BadCredentialsException("No user found with AD token"));
    } catch (JwtException ex) {
      throw new BadCredentialsException("Invalid token", ex);
    }
  }

  private String loginUser(UserJson user) {
    if (user.isActive()) {
      getUserService().setLastLogin(user.getId(), ZonedDateTime.now());
      return createTokenForUser(user);
    } else {
      throw new LockedException("Account locked, user " + user.getUserName());
    }
  }
}
