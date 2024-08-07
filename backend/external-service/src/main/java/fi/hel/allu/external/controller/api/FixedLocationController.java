package fi.hel.allu.external.controller.api;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.external.domain.FixedLocationExt;
import fi.hel.allu.external.mapper.FixedLocationMapper;
import fi.hel.allu.servicecore.service.LocationService;
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

import java.util.List;

@RestController
@RequestMapping({"/v1/fixedlocations", "/v2/fixedlocations"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Fixed locations", description = "API to list Allu fixed locations")
public class FixedLocationController {

    private final LocationService locationService;

    public FixedLocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Operation(description = "Get fixed locations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fixed locations retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FixedLocationExt.class))),
            @ApiResponse(responseCode = "404", description = "No fixed locations found",
                    content = @Content)
    })
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<List<FixedLocationExt>> getAll(
            @Parameter(description = "Application kind of the fixed locations to get", required = true)
            @RequestParam ApplicationKind applicationKind,
            @Parameter(description = "Spatial reference system ID of the geometry.")
            @RequestParam(required = false) Integer srId) {
        return new ResponseEntity<>(
                FixedLocationMapper.mapToExt(locationService.getFixedLocationList(applicationKind, srId)),
                HttpStatus.OK);
    }

    @Operation(description = "Get fixed location by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fixed location retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FixedLocationExt.class))),
            @ApiResponse(responseCode = "404", description = "No fixed locations found for given  ID",
                    content = @Content)
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<FixedLocationExt> findById(
            @Parameter(description = "Id of the fixed location") @PathVariable Integer id,
            @Parameter(description = "Spatial reference system ID of the geometry.")
            @RequestParam(required = false) Integer srId) {
        return new ResponseEntity<>(FixedLocationMapper.mapToExt(locationService.getFixedLocationById(id, srId)),
                                    HttpStatus.OK);
    }

}