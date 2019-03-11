package fi.hel.allu.supervision.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.ConfigurationService;
import fi.hel.allu.servicecore.service.LocationService;
import fi.hel.allu.servicecore.service.UserService;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1")
@Api
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private ConfigurationService configurationService;

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private LocationService locationService;

  private static final Map<ApplicationType, ConfigurationKey> APPLICATION_TYPE_TO_DECISION_MAKER_KEY = new HashMap<ApplicationType, ConfigurationKey>() {{
    put(ApplicationType.EXCAVATION_ANNOUNCEMENT, ConfigurationKey.EXCAVATION_ANNOUNCEMENT_DECISION_MAKER);
    put(ApplicationType.AREA_RENTAL, ConfigurationKey.AREA_RENTAL_DECISION_MAKER);
    put(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS, ConfigurationKey.TEMPORARY_TRAFFIC_ARRANGEMENTS_DECISION_MAKER);
    put(ApplicationType.PLACEMENT_CONTRACT, ConfigurationKey.PLACEMENT_CONTRACT_DECISION_MAKER);
    put(ApplicationType.EVENT, ConfigurationKey.EVENT_DECISION_MAKER);
    put(ApplicationType.SHORT_TERM_RENTAL, ConfigurationKey.SHORT_TERM_RENTAL_DECISION_MAKER);
  }};

  @ApiOperation(value = "Get user by user name",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "User retrieved successfully", response = UserJson.class),
  })
  @RequestMapping(value = "/users/username/{username}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<UserJson> getUserByUsername(@PathVariable String username) {
    return ResponseEntity.ok(userService.findUserByUserName(username));
  }

  @ApiOperation(value = "Get user by user ID",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "User retrieved successfully", response = UserJson.class)
  })
  @RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<UserJson> getUserById(@PathVariable Integer id) {
    return ResponseEntity.ok(userService.findUserById(id));
  }

  @ApiOperation(value = "Get active users by user role",
      authorizations = @Authorization(value = "api_key"),
      produces = "application/json",
      response = UserJson.class,
      responseContainer = "List"
      )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Users retrieved successfully", response = UserJson.class, responseContainer = "List")
  })
  @RequestMapping(value = "/users/role/{roleType}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<UserJson>> getUsersByRole(@PathVariable RoleType roleType) {
    return ResponseEntity.ok(activeUsers(userService.findUserByRole(roleType)));
  }

  @ApiOperation(value = "Get all active users",
      authorizations = @Authorization(value = "api_key"),
      produces = "application/json",
      response = UserJson.class,
      responseContainer = "List"
      )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Users retrieved successfully", response = UserJson.class, responseContainer = "List")
  })
  @RequestMapping(value = "/users", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<UserJson>> getAllUsers() {
    return ResponseEntity.ok(activeUsers(userService.findAllActiveUsers()));
  }

  @ApiOperation(value = "Get default decision maker for given application type",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class
      )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Decision maker retrieved successfully", response = UserJson.class),
  })
  @RequestMapping(value = "/users/decisionmakers", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<UserJson> getDecisionMaker(@ApiParam(allowableValues = "EXCAVATION_ANNOUNCEMENT, AREA_RENTAL, TEMPORARY_TRAFFIC_ARRANGEMENTS, PLACEMENT_CONTRACT, EVENT, SHORT_TERM_RENTAL")
                                                   @RequestParam ApplicationType applicationType) {
    UserJson decisionMaker = findDecisionMakerForApplicationType(applicationType);
    return ResponseEntity.ok(decisionMaker);
  }

  @ApiOperation(value = "Get default supervisor for application with given ID. ",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = UserJson.class
      )
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Supervisor retrieved successfully", response = UserJson.class),
  })
  @RequestMapping(value = "/applications/{applicationId}/defaultsupervisor", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<UserJson> getDecisionMaker(@ApiParam(value = "Application ID") @PathVariable Integer applicationId) {
    UserJson supervisor = findSupervisorForApplication(applicationId);
    return ResponseEntity.ok(supervisor);
  }

  private UserJson findSupervisorForApplication(Integer applicationId) {
    Application application = applicationService.findApplicationById(applicationId);
    return locationService.findSupervisionTaskOwner(application);
  }

  private UserJson findDecisionMakerForApplicationType(ApplicationType applicationType) {
    return Optional.ofNullable(configurationService.getSingleValue(APPLICATION_TYPE_TO_DECISION_MAKER_KEY.get(applicationType)))
        .map(u -> userService.findUserByUserName(u))
        .orElseThrow(() -> new NoSuchEntityException("user.notFound"));
  }

  private List<UserJson> activeUsers(List<UserJson> users) {
    return users.stream()
        .filter(u -> u.isActive())
        .collect(Collectors.toList());
  }
}
