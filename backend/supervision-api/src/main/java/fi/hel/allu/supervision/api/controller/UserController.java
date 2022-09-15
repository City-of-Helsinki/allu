package fi.hel.allu.supervision.api.controller;

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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Users")
public class UserController {

    private static final EnumMap<ApplicationType, ConfigurationKey> APPLICATION_TYPE_TO_DECISION_MAKER_KEY =
            new EnumMap<>(
                    ApplicationType.class);
    private final UserService userService;
    private final ConfigurationService configurationService;
    private final ApplicationService applicationService;
    private final LocationService locationService;

    public UserController(UserService userService, ConfigurationService configurationService,
                          ApplicationService applicationService, LocationService locationService) {
        this.userService = userService;
        this.configurationService = configurationService;
        this.applicationService = applicationService;
        this.locationService = locationService;
        APPLICATION_TYPE_TO_DECISION_MAKER_KEY.put(ApplicationType.EXCAVATION_ANNOUNCEMENT,
                                                   ConfigurationKey.EXCAVATION_ANNOUNCEMENT_DECISION_MAKER);
        APPLICATION_TYPE_TO_DECISION_MAKER_KEY.put(ApplicationType.AREA_RENTAL,
                                                   ConfigurationKey.AREA_RENTAL_DECISION_MAKER);
        APPLICATION_TYPE_TO_DECISION_MAKER_KEY.put(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS,
                                                   ConfigurationKey.TEMPORARY_TRAFFIC_ARRANGEMENTS_DECISION_MAKER);
        APPLICATION_TYPE_TO_DECISION_MAKER_KEY.put(ApplicationType.PLACEMENT_CONTRACT,
                                                   ConfigurationKey.PLACEMENT_CONTRACT_DECISION_MAKER);
        APPLICATION_TYPE_TO_DECISION_MAKER_KEY.put(ApplicationType.EVENT, ConfigurationKey.EVENT_DECISION_MAKER);
        APPLICATION_TYPE_TO_DECISION_MAKER_KEY.put(ApplicationType.SHORT_TERM_RENTAL,
                                                   ConfigurationKey.SHORT_TERM_RENTAL_DECISION_MAKER);
    }

    @Operation(summary = "Get user by user name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserJson.class))),
    })
    @GetMapping(value = "/users/username/{username}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<UserJson> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findUserByUserName(username));
    }

    @Operation(summary = "Get user by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserJson.class)))
    })
    @GetMapping(value = "/users/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<UserJson> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.findUserById(id));
    }

    @Operation(summary = "Get active users by user role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserJson.class)))
    })
    @GetMapping(value = "/users/role/{roleType}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<List<UserJson>> getUsersByRole(@PathVariable RoleType roleType) {
        return ResponseEntity.ok(activeUsers(userService.findUserByRole(roleType)));
    }

    @Operation(summary = "Get all active users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserJson.class)))
    })
    @GetMapping(value = "/users", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
    public ResponseEntity<List<UserJson>> getAllUsers() {
        return ResponseEntity.ok(activeUsers(userService.findAllActiveUsers()));
    }

    @Operation(summary = "Get default decision maker for given application type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Decision maker retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserJson.class))),
    })
    @GetMapping(value = "/users/decisionmakers", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<UserJson> getDecisionMaker(@RequestParam ApplicationType applicationType) {
        UserJson decisionMaker = findDecisionMakerForApplicationType(applicationType);
        return ResponseEntity.ok(decisionMaker);
    }

    @Operation(summary = "Get default supervisor for application with given ID. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervisor retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserJson.class))),
    })
    @GetMapping(value = "/applications/{applicationId}/defaultsupervisor", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
    public ResponseEntity<UserJson> getDecisionMaker(
            @Parameter(description = "Application ID") @PathVariable Integer applicationId) {
        UserJson supervisor = findSupervisorForApplication(applicationId);
        return ResponseEntity.ok(supervisor);
    }

    private UserJson findSupervisorForApplication(Integer applicationId) {
        Application application = applicationService.findApplicationById(applicationId);
        return locationService.findSupervisionTaskOwner(application);
    }

    private UserJson findDecisionMakerForApplicationType(ApplicationType applicationType) {
        return Optional.ofNullable(
                        configurationService.getSingleValue(APPLICATION_TYPE_TO_DECISION_MAKER_KEY.get(applicationType)))
                .map(userService::findUserByUserName)
                .orElseThrow(() -> new NoSuchEntityException("user.notFound"));
    }

    private List<UserJson> activeUsers(List<UserJson> users) {
        return users.stream()
                .filter(UserJson::isActive)
                .collect(Collectors.toList());
    }
}
