package fi.hel.allu.security;

import fi.hel.allu.config.SecurityConfig;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Interceptor for enforcing strict use of <code>PreAuthorize</code> annotation in all REST methods.
 */
public class PreAuthorizeEnforcerInterceptor extends HandlerInterceptorAdapter {

    // List of URLs that are not checked against using @PreAuthorize annotation
    private Set<String> skipCheck = new HashSet<String>(Arrays.asList(SecurityConfig.SECURITY_PATHS.LOGIN.toString()));

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // OK status is checked because otherwise this filter would also handle /error and such URLs,
        // which do not need @PreAuthorized
        if (handler instanceof HandlerMethod && (response.getStatus() == HttpStatus.OK.value())) {
            HandlerMethod hm = (HandlerMethod) handler;
            PreAuthorize annotation = AnnotationUtils.findAnnotation(hm.getMethod(), PreAuthorize.class);
            if (skipCheck.stream().filter(p -> p.contains(request.getRequestURI())).count() > 0) {
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
