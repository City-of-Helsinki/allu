package fi.hel.allu.external.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.InformationRequestResponseExt;
import fi.hel.allu.external.domain.PlacementContractExt;
import fi.hel.allu.external.mapper.PlacementContractExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/placementcontracts")
@Api(value = "v1/placementcontracts")
public class PlacementContractController {

  @Autowired
  private ApplicationServiceExt applicationService;

  @Autowired
  private PlacementContractExtMapper placementContractMapper;

  @Autowired
  private ApplicationExtGeometryValidator geometryValidator;

  @InitBinder("placementContractExt")
  protected void initBinder(WebDataBinder binder) {
    binder.addValidators(geometryValidator);
  }

  @ApiOperation(value = "Create new placement contract application. "
      + "If application is still pending in client side, pendingOnClient should be set to true to prevent handling "
      + "of application in Allu and to allow later modification of application data by client. If application is ready to be handled "
      + "in Allu, pendingOnClient should be set to false.",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
    @ApiResponse(code = 200, message = "Application created successfully", response = Integer.class),
    @ApiResponse(code = 400, message = "Invalid application", response = ErrorInfo.class)
  })
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> create(@ApiParam(value = "Placement contract data", required = true)
                                        @Valid @RequestBody PlacementContractExt placementContract) throws JsonProcessingException {
    return new ResponseEntity<>(applicationService.createApplication(placementContract, placementContractMapper), HttpStatus.OK);
  }

  @ApiOperation(value = "Update placement contract application. Allowed only if application was created with pendingOnClient = true",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Application updated successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> update(@ApiParam(value = "Id of the placement contract application to update.")
                                        @PathVariable Integer id,
                                        @ApiParam(value = "Placement contract data", required = true)
                                        @Valid @RequestBody PlacementContractExt placementContract) throws JsonProcessingException {
    applicationService.validateFullUpdateAllowed(id);
    applicationService.validateOwnedByExternalUser(id);
    return new ResponseEntity<>(applicationService.updateApplication(id, placementContract, placementContractMapper), HttpStatus.OK);
  }

  @ApiOperation(value = "Send response for information request specified by ID parameter. Only fields listed in response are processed in Allu. "
      + "Also data sent through some separate API (e.g. application attachments) should be included in field list of response.",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Response added successfully", response = Void.class),
      @ApiResponse(code = 400, message = "Invalid request response", response = ErrorInfo.class)
  })
  @RequestMapping(value = "{applicationid}/informationrequests/{requestid}/response", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> addResponse(@ApiParam(value = "Id of the application") @PathVariable("applicationid") Integer applicationId,
                                          @ApiParam(value = "Id of the information request") @PathVariable("requestid") Integer requestId,
                                          @ApiParam(value = "Content of the response") @RequestBody @Valid InformationRequestResponseExt<PlacementContractExt> response) throws JsonProcessingException {
    applicationService.addInformationRequestResponse(applicationId, requestId, response, placementContractMapper);
    return new ResponseEntity<>(HttpStatus.OK);
  }


}
