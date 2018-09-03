package fi.hel.allu.servicecore.security;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class StatelessAuthenticationFilter extends GenericFilterBean {
  private final Logger logger = LoggerFactory.getLogger(StatelessAuthenticationFilter.class);

  private final AuthenticationServiceInterface authenticationService;

  public StatelessAuthenticationFilter(AuthenticationServiceInterface authenticationService) {
    this.authenticationService = authenticationService;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    Authentication authentication;

    // If empty authentication (no JWT) is used and path is not allowed for anonymous users, we're handling unauthorized case
    if (authenticationService.isEmptyAuthentication(httpRequest) &&
        !authenticationService.isAnonymousAccessAllowedForPath(httpRequest.getRequestURI())) {
      setUnauthorizedResponse(httpRequest, response);
      return;
    }

    try {
      authentication = authenticationService.getAuthentication(httpRequest);
    } catch (JwtException e) {
      setUnauthorizedResponse(httpRequest, response);
      return;
    } catch (Exception e) {
      // Let fail later
      authentication = null;
    }
    try {
      SecurityContextHolder.getContext().setAuthentication(authentication);
      filterChain.doFilter(request, response);
    } finally {
      SecurityContextHolder.getContext().setAuthentication(null);
    }
  }

  private void setUnauthorizedResponse(HttpServletRequest httpRequest, ServletResponse response) throws IOException {
    logger.info("Unauthorized request to resource {}", httpRequest);
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
  }
}
