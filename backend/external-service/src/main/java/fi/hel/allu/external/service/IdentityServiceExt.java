package fi.hel.allu.external.service;

import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.user.Constants;
import fi.hel.allu.servicecore.service.IdentityServiceInterface;

/**
 * Implementation of identity service using security information provided by Spring.
 */
@Service
public class IdentityServiceExt implements IdentityServiceInterface {

  @Override
  public String getUsername() {
    return Constants.EXTERNAL_USER_USERNAME;
  }
}
