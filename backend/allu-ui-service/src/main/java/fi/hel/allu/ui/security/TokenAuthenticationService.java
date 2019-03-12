package fi.hel.allu.ui.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.servicecore.security.AadService;
import fi.hel.allu.servicecore.security.AdTokenAuthenticationService;
import fi.hel.allu.servicecore.service.UserService;
import fi.hel.allu.ui.config.ApplicationProperties;

@Service
public class TokenAuthenticationService extends AdTokenAuthenticationService {

  @Autowired
  public TokenAuthenticationService(ApplicationProperties applicationProperties, UserService userService,
      AadService aadService) {
    super(applicationProperties, userService, aadService);
  }


  @Override
  public boolean isAnonymousAccessAllowedForPath(String path) {
    return getAnonymousAccessPaths().contains(path);
  }

}
