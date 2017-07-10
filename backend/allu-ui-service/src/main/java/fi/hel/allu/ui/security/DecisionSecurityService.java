package fi.hel.allu.ui.security;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.ui.service.ApplicationService;
import fi.hel.allu.ui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DecisionSecurityService {

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private UserService userService;

  private Set<RoleType> allowedCableReportDecisionRoles = Stream.of(RoleType.ROLE_PROCESS_APPLICATION, RoleType.ROLE_DECISION)
          .collect(Collectors.toSet());

  public boolean canMakeDecision(int applicationId) {
    UserJson user = userService.getCurrentUser();
    Application app = applicationService.findApplicationById(applicationId);
    return app.getType().equals(ApplicationType.CABLE_REPORT)
            ? canMakeDecisionForCableReport(user)
            : canMakeDecisionForOthers(user);
  }

  private boolean canMakeDecisionForCableReport(UserJson user) {
    return user.getAssignedRoles().stream().anyMatch(allowedCableReportDecisionRoles::contains);
  }

  private boolean canMakeDecisionForOthers(UserJson user) {
    return user.getAssignedRoles().contains(RoleType.ROLE_DECISION);
  }
}
