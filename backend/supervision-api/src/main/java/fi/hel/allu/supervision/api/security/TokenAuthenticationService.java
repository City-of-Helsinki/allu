package fi.hel.allu.supervision.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.servicecore.security.AdfsTokenAuthenticationService;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.supervision.api.config.ApplicationProperties;

@Service
public class TokenAuthenticationService extends AdfsTokenAuthenticationService {

  @Autowired
  public TokenAuthenticationService(ApplicationProperties properties, RestTemplate restTemplate,
      UserService userService) {
    super(properties, restTemplate, userService);
  }

  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    final AntPathMatcher antPathMatcher = new AntPathMatcher();
    return getAnonymousAccessPaths().stream().anyMatch(p -> antPathMatcher.match(p, path));
  }
}
