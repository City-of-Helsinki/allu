package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.supervision.api.service.LocationUpdateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/locations")
@Api(tags = "Locations")
public class LocationController {
  private LocationUpdateService locationUpdateService;

  @Autowired
  public LocationController(LocationUpdateService locationUpdateService) {
    this.locationUpdateService = locationUpdateService;
  }

  @ApiOperation(value = "Update a location",
    notes =
      "<p>Data is given as key/value pair updated field being the key and it's new value (as JSON) the value. "
        + "All fields that are not marked as read only can be updated through this API.</p>",
    authorizations = @Authorization(value ="api_key"),
    produces = "application/json"
  )
  @ApiResponses( value = {
    @ApiResponse(code = 200, message = "Location updated successfully"),
    @ApiResponse(code = 403, message = "Location update forbidden", response = ErrorInfo.class),

  })
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Location> updateLocation(@PathVariable Integer id,
                                                 @RequestBody @ApiParam("Map containing field names with their new values.") Map<String, Object> fields) {
    Location updated = locationUpdateService.update(id, fields);
    return ResponseEntity.ok(updated);
  }
}
