package fi.hel.allu.external.service;

import fi.hel.allu.servicecore.service.IdentityServiceInterface;
import org.springframework.stereotype.Service;

/**
 * Implementation of identity service using security information provided by Spring.
 */
@Service
public class IdentityServiceExt implements IdentityServiceInterface {

  @Override
  public String getUsername() {
    // TODO: fix when proper authentication is added to external-service
    return "admin";
  }
}
