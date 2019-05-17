package fi.hel.allu.ui.security;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.exception.IllegalOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class ApplicationSecurityService {
  @Autowired
  private UserService userService;

  public boolean canCreate(ApplicationType type) {
    UserJson user = userService.getCurrentUser();
    return user.getAllowedApplicationTypes().contains(type)
        && user.getAssignedRoles().contains(RoleType.ROLE_CREATE_APPLICATION);
  }

  public boolean canModifyTag(ApplicationTagType tagType) {
    UserJson user = userService.getCurrentUser();
    return hasRole(user, RoleType.ROLE_CREATE_APPLICATION, RoleType.ROLE_PROCESS_APPLICATION)
        || (hasRole(user, RoleType.ROLE_MANAGE_SURVEY) && ApplicationTagType.SURVEY_REQUIRED == tagType);
  }

  private boolean hasRole(UserJson user, RoleType...roles) {
    return Stream.of(roles).anyMatch(role -> user.getAssignedRoles().contains(role));
  }
}
