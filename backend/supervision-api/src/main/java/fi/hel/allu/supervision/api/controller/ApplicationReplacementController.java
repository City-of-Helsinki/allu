package fi.hel.allu.supervision.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;

@RestController
@RequestMapping("/v1/applications")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Applications")
public class ApplicationReplacementController {

  private final ApplicationServiceComposer applicationServiceComposer;

  public ApplicationReplacementController(ApplicationServiceComposer applicationServiceComposer) {
    this.applicationServiceComposer = applicationServiceComposer;
  }

  @Operation(summary = "Creates replacing application for application with given ID and returns id of the replacing application")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application replaced successfully"),
      @ApiResponse(responseCode = "400", description = "Application cannot be replaced",
              content = @Content(schema = @Schema(implementation = ErrorInfo.class)))})
  @PostMapping(value = "/{id}/replace")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Integer> replaceApplication(@PathVariable Integer id) {
    Integer replacingApplicationId = applicationServiceComposer.replaceApplication(id).getId();
    return ResponseEntity.ok(replacingApplicationId);
  }
}
