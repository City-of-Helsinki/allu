package fi.hel.allu.servicecore.security;

import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor for enforcing strict use of <code>PreAuthorize</code> annotation in all REST methods.
 */
@Component
public class PreAuthorizeEnforcerInterceptor extends HandlerInterceptorAdapter {

  @Autowired
  ApplicationProperties applicationProperties;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    // OK status is checked because otherwise this filter would also handle /error and such URLs,
    // which do not need @PreAuthorized
    if (handler instanceof HandlerMethod && (response.getStatus() == HttpStatus.OK.value())) {
      HandlerMethod hm = (HandlerMethod) handler;
      PreAuthorize annotation = AnnotationUtils.findAnnotation(hm.getMethod(), PreAuthorize.class);
      if (applicationProperties.getAnonymousAccessPaths().contains(request.getRequestURI())) {
        // no checking for certain uris
      } else if (annotation == null) {
        // if @PreAuthorize does not exist in method, method execution is not allowed at all
        throw new RuntimeException("Method " + hm.getMethod().getName() + " for path " + request.getRequestURI() +
            " has no @PreAuthorize, access not allowed.");
      }

    }
    return true;
  }
}
