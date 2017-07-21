package fi.hel.allu.ui.service;

import fi.hel.allu.servicecore.service.IdentityServiceInterface;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

/**
 * Implementation of identity service using security information provided by Spring.
 */
@Service
public class IdentityService implements IdentityServiceInterface {
  @Override
  public String getUsername() {
    User alluUser = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
    return alluUser.getUsername();
  }
}
