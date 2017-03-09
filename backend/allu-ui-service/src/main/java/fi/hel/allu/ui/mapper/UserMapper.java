package fi.hel.allu.ui.mapper;

import fi.hel.allu.model.domain.User;
import fi.hel.allu.ui.domain.UserJson;
import fi.hel.allu.ui.security.AlluUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

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
        userJson.getAllowedApplicationTypes(),
        userJson.getAssignedRoles(),
        userJson.getCityDistrictIds());
  }

  public static AlluUser mapToAlluUser(UserJson userJson) {
    List<GrantedAuthority> roles =
        userJson.getAssignedRoles().stream().map(r -> new SimpleGrantedAuthority(r.toString())).collect(Collectors.toList());
    return new AlluUser(userJson.getUserName(), roles, userJson.getEmailAddress());
  }
}
