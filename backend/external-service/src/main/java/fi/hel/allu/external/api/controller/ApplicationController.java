package fi.hel.allu.external.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.domain.LocationExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.service.LocationServiceExt;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(value = "v1/applications")
public class ApplicationController {

  @Autowired
  private ApplicationServiceExt applicationService;

  @Autowired
  private LocationServiceExt locationService;


  @ApiOperation(value = "Sets Allu application cancelled",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/{id}/cancelled", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> cancelApplication(@ApiParam(value = "Id of the application to cancel.") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    applicationService.cancelApplication(applicationId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Gets Allu application data for given application ID",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application retrieved successfully", response = ApplicationExt.class),
      @ApiResponse(code = 404, message = "No application found for given ID")
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ApplicationExt> getApplication(@ApiParam(value = "Id of the application to get") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.findById(applicationId));
  }

  @ApiOperation(value = "Gets application location data for given application ID",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Location data retrieved successfully", response = LocationExt.class),
      @ApiResponse(code = 404, message = "No location data found for given ID")
  })
  @RequestMapping(value = "/{id}/location", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<LocationExt> getLocation(@ApiParam(value = "Id of the application") @PathVariable Integer id,
      @ApiParam(value = "Spatial reference system ID of the  geometry.", required = false, defaultValue = "3879") @RequestParam(required = false) Integer srId) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(locationService.findByApplicationId(applicationId, srId));
  }
}
