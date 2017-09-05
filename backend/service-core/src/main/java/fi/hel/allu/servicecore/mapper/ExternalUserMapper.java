package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.ExternalUser;
import fi.hel.allu.servicecore.domain.ExternalUserJson;

/**
 * Mapping between different user objects.
 */
public class ExternalUserMapper {
  public static ExternalUserJson mapToExternalUserJson(ExternalUser user) {
    return new ExternalUserJson(
        user.getId(),
        user.getUsername(),
        user.getName(),
        user.getEmailAddress(),
        user.getToken(),
        user.getActive(),
        user.getLastLogin(),
        user.getConnectedCustomers());
  }

  public static ExternalUser mapToModelExternalUser(ExternalUserJson userJson) {
    return new ExternalUser(
        userJson.getId(),
        userJson.getUsername(),
        userJson.getName(),
        userJson.getEmailAddress(),
        userJson.getToken(),
        userJson.getActive(),
        userJson.getLastLogin(),
        userJson.getConnectedCustomers());
  }
}
