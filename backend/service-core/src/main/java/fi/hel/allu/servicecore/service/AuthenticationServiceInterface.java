package fi.hel.allu.servicecore.service;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * Authentication service interface allowing different kinds of authentication implementations.
 */
public abstract class AuthenticationServiceInterface {
  protected static final String AUTH_HEADER_NAME = "Authorization";

  /**
   * Returns authentication information retrieved from the given request.
   *
   * @param request Request having authentication information.
   * @return  authentication information retrieved from the given request.
   */
  abstract public Authentication getAuthentication(HttpServletRequest request);

  /**
   * Returns true, if anonymous access is allowed for given path.
   *
   * @param path  Path to be tested against anonymous access.
   * @return  true, if anonymous access is allowed for given path.
   */
  abstract public boolean isAnonymousAccessAllowedForPath(String path);

  /**
   * Returns true, if given request contains no authentication information.
   *
   * @param request   Request to be tested.
   * @return  true, if given request contains no authentication information.
   */
  public boolean isEmptyAuthentication(HttpServletRequest request) {
    final String token = request.getHeader(AUTH_HEADER_NAME);
    if (token == null || token.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
}
