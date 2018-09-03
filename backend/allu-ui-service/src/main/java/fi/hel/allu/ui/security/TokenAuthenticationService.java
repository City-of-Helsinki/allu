package fi.hel.allu.ui.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.servicecore.security.AdfsTokenAuthenticationService;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.ui.config.ApplicationProperties;

@Service
public class TokenAuthenticationService extends AdfsTokenAuthenticationService {

  @Autowired
  public TokenAuthenticationService(
      ApplicationProperties applicationProperties,
      RestTemplate restTemplate,
      UserService userService) {
    super(applicationProperties, restTemplate, userService);
  }


  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    return getAnonymousAccessPaths().contains(path);
  }

}
