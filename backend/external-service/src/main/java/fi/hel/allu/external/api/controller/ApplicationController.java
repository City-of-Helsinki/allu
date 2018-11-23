package fi.hel.allu.external.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(value = "v1/applications")
public class ApplicationController {

  @Autowired
  private ApplicationServiceExt applicationService;

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
}
