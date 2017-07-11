package fi.hel.allu.ui.service;

import fi.hel.allu.servicecore.service.IdentityServiceInterface;
import fi.hel.allu.ui.security.AlluUser;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Implementation of identity service using security information provided by Spring.
 */
@Service
public class IdentityService implements IdentityServiceInterface {
  @Override
  public String getUsername() {
    AlluUser alluUser = (AlluUser) SecurityContextHolder.getContext().getAuthentication().getDetails();
    return alluUser.getUsername();
  }
}
