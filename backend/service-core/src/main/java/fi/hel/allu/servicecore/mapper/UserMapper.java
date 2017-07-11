package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.User;
import fi.hel.allu.servicecore.domain.UserJson;

/**
 * Mapping between different user objects.
 */
public class UserMapper {
  public static UserJson mapToUserJson(User user) {
    return new UserJson(
        user.getId(),
        user.getUserName(),
        user.getRealName(),
        user.getEmailAddress(),
        user.getTitle(),
        user.isActive(),
        user.getLastLogin(),
        user.getAllowedApplicationTypes(),
        user.getAssignedRoles(),
        user.getCityDistrictIds());
  }

  public static User mapToModelUser(UserJson userJson) {
    return new User(
        userJson.getId(),
        userJson.getUserName(),
        userJson.getRealName(),
        userJson.getEmailAddress(),
        userJson.getTitle(),
        userJson.isActive(),
        userJson.getLastLogin(),
        userJson.getAllowedApplicationTypes(),
        userJson.getAssignedRoles(),
        userJson.getCityDistrictIds());
  }
}
