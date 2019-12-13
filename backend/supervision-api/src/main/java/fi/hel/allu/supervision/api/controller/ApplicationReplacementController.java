package fi.hel.allu.supervision.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(tags = "Applications")
public class ApplicationReplacementController {

  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

  @ApiOperation(value = "Creates replacing application for application with given ID and returns id of the replacing application",
      authorizations = @Authorization(value = "api_key"))
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Application replaced successfully"),
      @ApiResponse(code = 400, message = "Application cannot be replaced", response = ErrorInfo.class), })
  @RequestMapping(value = "/{id}/replace", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Integer> replaceApplication(@PathVariable Integer id) {
    Integer replacingApplicationId = applicationServiceComposer.replaceApplication(id).getId();
    return ResponseEntity.ok(replacingApplicationId);
  }
}
