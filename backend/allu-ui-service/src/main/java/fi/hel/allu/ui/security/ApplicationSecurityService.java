package fi.hel.allu.ui.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.UserService;

@Service
public class ApplicationSecurityService {
  @Autowired
  private UserService userService;

  public boolean canCreate(ApplicationType type) {
    UserJson user = userService.getCurrentUser();
    return user.getAllowedApplicationTypes().contains(type)
        && user.getAssignedRoles().contains(RoleType.ROLE_CREATE_APPLICATION);
  }
}
