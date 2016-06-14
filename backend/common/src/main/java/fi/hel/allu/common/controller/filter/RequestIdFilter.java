package fi.hel.allu.common.controller.filter;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.MDC;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter for adding request ID to all REST calls automatically. The id can be used for collecting logged messages
 * related to certain request from log files. It is also usable for relating error messages shown to user with logged
 * errors.
 */
@Configuration
public class RequestIdFilter {

  public static final String REQUEST_ID_KEY = "requestId";

  @Bean
  public FilterRegistrationBean requestIdFilterRegistrationBean() {
    Filter logFilter = new RequestIdFilterBean();
    FilterRegistrationBean regBean = new FilterRegistrationBean();
    regBean.setFilter(logFilter);
    // filtering all URLs
    regBean.addUrlPatterns("/*");
    // trying to filter everything here before any other filter
    regBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return regBean;
  }

  private class RequestIdFilterBean extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
      String requestId = RandomStringUtils.randomAlphanumeric(6);
      MDC.put(REQUEST_ID_KEY, requestId);
      try {
        request.setAttribute(REQUEST_ID_KEY, requestId);
        filterChain.doFilter(request, response);
      } finally {
        MDC.remove(REQUEST_ID_KEY);
      }
    }
  }
}
