package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.model.domain.Location;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.supervision.api.domain.AreaRentalApplication;
import io.swagger.annotations.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/arearentals")
@Api(tags = "Applications")
public class AreaRentalController extends BaseApplicationDetailsController<AreaRentalApplication> {

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  @Override
  protected AreaRentalApplication mapApplication(ApplicationJson application) {
    return new AreaRentalApplication(application);
  }

  @ApiOperation(value = "Gets work finished approval document for area rental with given ID. Returns draft if work finished is not yet approved.",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/approval/workfinished", method = RequestMethod.GET, produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<byte[]> getWorkFinishedDocument(@PathVariable Integer id) {
    validateType(id);
    return getApprovalDocument(id, ApprovalDocumentType.WORK_FINISHED);
  }

  @ApiOperation(value = "Create a new location",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json",
    response = Location.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Location created successfully", response = Location.class),
    @ApiResponse(code = 400, message = "Invalid location data", response = ErrorInfo.class),
    @ApiResponse(code = 403, message = "Location addition forbidden", response = ErrorInfo.class)
  })
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  @RequestMapping(value = "/{applicationId}/locations", method = RequestMethod.POST,
    produces = "application/json", consumes = "application/json")
  public ResponseEntity<Location> createLocation(@PathVariable Integer applicationId,
                                                 @RequestBody @Valid Location location) {
    validateType(applicationId);
    location.setApplicationId(applicationId);
    Location createdLocation = locationService.insertLocation(location);
    return ResponseEntity.ok(createdLocation);
  }

  @ApiOperation(value = "Delete an existing location",
    authorizations = @Authorization(value = "api_key"),
    response = Location.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Location deleted successfully", response = Location.class),
    @ApiResponse(code = 400, message = "Invalid location data or attempt to remove the last location", response = ErrorInfo.class),
    @ApiResponse(code = 403, message = "Location deletion forbidden", response = ErrorInfo.class)
  })
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  @RequestMapping(value = "/{applicationId}/locations/{locationId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteLocation(@PathVariable Integer applicationId,
                                             @PathVariable Integer locationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    validateType(application);
    if (application.getLocations().stream().noneMatch(locationJson -> locationId.equals(locationJson.getId()))) {
      return ResponseEntity.notFound().build();
    }
    if (application.getLocations().size() <= 1) {
      // the last location should not be removed
      throw new IllegalArgumentException("application.location.deleteLastLocationForbidden");
    }
    locationService.deleteLocation(locationId);
    return ResponseEntity.ok().build();
  }
}
