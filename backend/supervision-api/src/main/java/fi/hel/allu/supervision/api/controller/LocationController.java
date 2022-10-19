package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.domain.InvoiceJson;
import fi.hel.allu.supervision.api.service.LocationUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/locations")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Locations")
public class LocationController {
  private final LocationUpdateService locationUpdateService;

  @Autowired
  public LocationController(LocationUpdateService locationUpdateService) {
    this.locationUpdateService = locationUpdateService;
  }

  @Operation(summary = "Update a location",
    description =
      "<p>Data is given as key/value pair updated field being the key and it's new value (as JSON) the value. "
        + "All fields that are not marked as read only can be updated through this API.</p>")
  @ApiResponses( value = {
    @ApiResponse(responseCode = "200", description = "Location updated successfully"),
    @ApiResponse(responseCode = "403", description = "Location update forbidden",
            content = @Content(schema = @Schema(implementation = ErrorInfo.class))),

  })
  @PutMapping(value = "/{id}", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Location> updateLocation(@PathVariable Integer id,
                                                 @RequestBody @Parameter(description = "Map containing field names with their new values.") Map<String, Object> fields) {
    Location updated = locationUpdateService.update(id, fields);
    return ResponseEntity.ok(updated);
  }
}
