package fi.hel.allu.supervision.api.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import fi.hel.allu.servicecore.service.IdentityServiceInterface;

@Service
public class IdentityService implements IdentityServiceInterface {

  @Override
  public String getUsername() {
    User alluUser = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
    return alluUser.getUsername();
  }

}
