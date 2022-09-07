package fi.hel.allu.external.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.config.ApplicationProperties;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Application kinds")
public class ApplicationKindController {

  private final ApplicationProperties applicationProperties;

  public ApplicationKindController(ApplicationProperties applicationProperties) {
    this.applicationProperties = applicationProperties;
  }

  @Operation(summary = "Get Allu application kinds")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Application kinds retrieved",
                  content = @Content(schema = @Schema(implementation = ApplicationKind.class))),
          @ApiResponse(responseCode = "404", description = "Application kinds not found", content = @Content)
  })
  @GetMapping(value = {"/v1/applicationkinds"}, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  @Deprecated
  public ResponseEntity<List<ApplicationKind>> getAllSupportedByV1(
    @Parameter(description = "Application type of the kinds to get", required = true)
    @RequestParam ApplicationType applicationType) {
    List<ApplicationKind> result = ApplicationKind.forApplicationType(applicationType)
        .stream()
        .filter(k -> !applicationProperties.getV1ExcludedApplicationKinds().contains(k.name()))
        .collect(Collectors.toList());
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @Operation(summary = "Get Allu application kinds")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Application kinds retrieved",
                  content = @Content(schema = @Schema(implementation = ApplicationKind.class))),
          @ApiResponse(responseCode = "404", description = "Application kinds not found", content = @Content)
  })
  @GetMapping(value = "/v2/applicationkinds", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<ApplicationKind>> getAll(
    @Parameter(description = "Application type of the kinds to get", required = true)
    @RequestParam ApplicationType applicationType) {
    return new ResponseEntity<>(ApplicationKind.forApplicationType(applicationType), HttpStatus.OK);
  }
}
