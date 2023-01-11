package fi.hel.allu.servicecore.mapper;

import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.UserES;
import fi.hel.allu.servicecore.domain.UserJson;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Mapping between different user objects.
 */
@Component
public class UserMapper {
  public static UserJson mapToUserJson(User user) {
    return new UserJson(
        user.getId(),
        user.getUserName(),
        user.getRealName(),
        user.getEmailAddress(),
        user.getPhone(),
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
        StringUtils.lowerCase(userJson.getUserName()),
        userJson.getRealName(),
        userJson.getEmailAddress(),
        userJson.getPhone(),
        userJson.getTitle(),
        userJson.isActive(),
        userJson.getLastLogin(),
        userJson.getAllowedApplicationTypes(),
        userJson.getAssignedRoles(),
        userJson.getCityDistrictIds());
  }

  public List<ApplicationES> populateOwners(Map<Integer, User> applicationIdUserMap, List<ApplicationES> applicationESList){
    for (ApplicationES es : applicationESList){
      User user = applicationIdUserMap.get(es.getId());
      if (user != null){
        es.setOwner(new UserES(user.getUserName(), user.getRealName()));
      }
    }
    return applicationESList;
  }
}