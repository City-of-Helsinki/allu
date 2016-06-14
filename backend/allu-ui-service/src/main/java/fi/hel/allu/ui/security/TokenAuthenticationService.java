package fi.hel.allu.ui.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

public class TokenAuthenticationService {
  private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

  private static final String AUTH_HEADER_NAME = "Authorization";

  @Autowired
  private TokenHandler tokenHandler;

  public Authentication getAuthentication(HttpServletRequest request) {
    if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
      logger.debug("OPTIONS is always allowed");
      return new UserAuthentication(new User("options", "", Collections.<GrantedAuthority>emptySet()));
    }
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token != null && token.startsWith("Bearer ")) {
      final User user = tokenHandler.parseUserFromToken(token.replaceFirst("^Bearer ", ""));
      if (user != null) {
        return new UserAuthentication(user);
      }
    }
    return null;
  }

  public boolean isEmptyAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token == null || token.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
}
