package fi.hel.allu.external.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import fi.hel.allu.external.domain.ApplicationHistoryExt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.external.config.ApplicationProperties;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Application kinds")
public class ApplicationKindController {

  @Autowired
  private ApplicationProperties applicationProperties;

  @Operation(summary = "Get Allu application kinds")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Application kinds retrieved",
                  content = @Content(schema = @Schema(implementation = ApplicationKind.class))),
          @ApiResponse(responseCode = "404", description = "Application kinds not found", content = @Content)
  })
  @RequestMapping(value = {"/v1/applicationkinds"}, method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  @Deprecated
  public ResponseEntity<List<ApplicationKind>> getAllSupportedByV1(
    @Parameter(description = "Application type of the kinds to get", required = true)
    @RequestParam(required = true) ApplicationType applicationType) {
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
  @RequestMapping(value = "/v2/applicationkinds", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<ApplicationKind>> getAll(
    @Parameter(description = "Application type of the kinds to get", required = true)
    @RequestParam(required = true) ApplicationType applicationType) {
    return new ResponseEntity<>(ApplicationKind.forApplicationType(applicationType), HttpStatus.OK);
  }
}
