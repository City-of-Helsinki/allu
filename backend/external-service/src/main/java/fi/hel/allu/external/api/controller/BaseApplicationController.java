package fi.hel.allu.external.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.BaseApplicationExt;
import fi.hel.allu.external.domain.InformationRequestResponseExt;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import io.swagger.annotations.*;

/**
 * Base class for external application controllers
 */
public abstract class BaseApplicationController<T extends BaseApplicationExt, M extends ApplicationExtMapper<T>>  {

  @Autowired
  protected ApplicationServiceExt applicationService;

  @Autowired
  protected ApplicationExtGeometryValidator geometryValidator;

  protected abstract M getMapper();

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    if (binder.getTarget() != null && BaseApplicationExt.class.isAssignableFrom(binder.getTarget().getClass())) {
      binder.addValidators(geometryValidator);
    }
  }

  @ApiOperation(value = "Create new application. Returns ID of the created application. "
      + "If application is still pending in client side, pendingOnClient should be set to true to prevent handling "
      + "of application in Allu and to allow later modification of application data by client. If application is ready to be handled "
      + "in Allu, pendingOnClient should be set to false.",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Application added successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid application", response = ErrorInfo.class)
  })
  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> create(@ApiParam(value = "Application data", required = true)
                                        @Valid @RequestBody T applicationExt) throws JsonProcessingException {
    return new ResponseEntity<>(applicationService.createApplication(applicationExt, getMapper()), HttpStatus.OK);
  }

  @ApiOperation(value = "Update application. Allowed only if handling of application has not been started",
      produces = "application/json",
      response = Integer.class,
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Application updated successfully", response = Integer.class),
      @ApiResponse(code = 400, message = "Invalid application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> update(@ApiParam(value = "Id of the application to update.")
                                        @PathVariable Integer id,
                                        @ApiParam(value = "Application data", required = true)
                                        @Valid @RequestBody T application) throws JsonProcessingException {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateFullUpdateAllowed(applicationId);
    applicationService.validateOwnedByExternalUser(applicationId);
    return new ResponseEntity<>(applicationService.updateApplication(id, application, getMapper()), HttpStatus.OK);
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
                                          @ApiParam(value = "Content of the response") @RequestBody @Valid InformationRequestResponseExt<T> response) throws JsonProcessingException {
    applicationService.addInformationRequestResponse(applicationService.getApplicationIdForExternalId(applicationId), requestId, response, getMapper());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  protected ResponseEntity<byte[]> returnPdfResponse(byte[] bytes) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }


}
