package fi.hel.allu.security;

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

    private final TokenAuthenticationService authenticationService;

    public StatelessAuthenticationFilter(TokenAuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Authentication authentication;
        try {
            authentication = authenticationService.getAuthentication(httpRequest);
        }
        catch (JwtException e) {
            logger.info("Unauthorized request to resource {}", httpRequest);
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        catch (Exception e) {
            // Let fail later
            authentication = null;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
        SecurityContextHolder.getContext().setAuthentication(null);
    }
}