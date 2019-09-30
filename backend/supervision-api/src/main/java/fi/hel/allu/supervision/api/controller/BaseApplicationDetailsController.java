package fi.hel.allu.supervision.api.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import fi.hel.allu.servicecore.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.supervision.api.domain.BaseApplication;
import fi.hel.allu.supervision.api.service.ApplicationUpdateService;
import io.swagger.annotations.*;

public abstract class BaseApplicationDetailsController<A extends BaseApplication<?>> {


  protected abstract ApplicationType getApplicationType();
  protected abstract A mapApplication(ApplicationJson application);

  @Autowired
  private ApprovalDocumentService approvalDocumentService;
  @Autowired
  private ChargeBasisService chargeBasisService;
  @Autowired
  protected ApplicationServiceComposer applicationServiceComposer;
  @Autowired
  private ApplicationUpdateService applicationUpdateService;
  @Autowired
  protected LocationService locationService;


  @ApiOperation(value = "Get application details",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application retrieved successfully"),
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<A> getApplicationDetails(@PathVariable Integer id) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(id);
    validateType(application);
    return ResponseEntity.ok(mapApplication(application));
  }

  @ApiOperation(value = "Get applications with given list of IDs",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Applications retrieved successfully"),
  })
  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<A>> getApplicationsWithIds(@RequestParam("ids") final List<Integer> ids) {
    List<ApplicationJson> applications = applicationServiceComposer.findApplicationsByIds(ids);
    applications.forEach(a -> validateType(a));
    return ResponseEntity.ok(applications.stream().map(a -> mapApplication(a)).collect(Collectors.toList()));
  }

  @ApiOperation(value = "Update application with given version number.",
      notes =
        "<p>Data is given as key/value pair updated field being the key and it's new value (as JSON) the value. "
      + "All fields that are not marked as read only can be updated through this API.</p>"
      + "<p>Update is allowed only if the status of the application is PENDING and application is not an external application "
      + "or status of the application is HANDLING, PRE_RESERVED or RETURNED_TO_PREPARATION.</p>",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application updated successfully"),
      @ApiResponse(code = 409, message = "Update failed, given version of application updated by another user", response = ErrorInfo.class),
      @ApiResponse(code = 403, message = "Application update forbidden", response = ErrorInfo.class),

  })
  @RequestMapping(value = "/{id}/version/{version}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<A> updateApplication(@PathVariable Integer id, @PathVariable Integer version,
      @RequestBody @ApiParam("Map containing field names with their new values.") Map<String, Object> fields) {
    validateType(id);
    ApplicationJson updatedApplication = applicationUpdateService.update(id, version, fields);
    return ResponseEntity.ok(mapApplication(updatedApplication));
  }

  protected void validateType(Integer applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    validateType(application);
  }

  protected void validateType(ApplicationJson application) {
    if (application.getType() != getApplicationType()) {
      throw new IllegalOperationException("applicationtype.invalid");
    }
  }

  protected ResponseEntity<byte[]> getApprovalDocument(Integer applicationId, ApprovalDocumentType type) {
    return pdfResult(approvalDocumentService.getApprovalDocument(applicationId, type,
        chargeBasisService.getUnlockedAndInvoicableChargeBasis(applicationId)));
  }

  protected ResponseEntity<byte[]> pdfResult(byte[] bytes) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_PDF);
    return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
  }

}
