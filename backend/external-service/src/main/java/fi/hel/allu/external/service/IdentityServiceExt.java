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
    // This is hard coded user, which will be shown in Allu UI as the user who has for example added attachments, comments, tags or changes
    // to the application
    return "rajapinta";
  }
}
