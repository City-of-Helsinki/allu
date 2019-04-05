package fi.hel.allu.supervision.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.supervision.api.domain.BaseApplication;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

public abstract class BaseApplicationDetailsController<A extends BaseApplication<?>> {


  protected abstract ApplicationType getApplicationType();
  protected abstract A mapApplication(ApplicationJson application);

  @Autowired
  private ApprovalDocumentService approvalDocumentService;
  @Autowired
  private ChargeBasisService chargeBasisService;
  @Autowired
  private ApplicationServiceComposer applicationServiceComposer;

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

  @ApiOperation(value = "Update application. "
      + "Update is allowed only if the status of the application is PENDING and application is not an external application "
      + "or status of the application is HANDLING, PRE_RESERVED or RETURNED_TO_PREPARATION.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
      )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application updated successfully"),
      @ApiResponse(code = 409, message = "Update failed, given version of application updated by another user", response = ErrorInfo.class),
      @ApiResponse(code = 403, message = "Application update forbidden", response = ErrorInfo.class),

  })
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<A> updateApplication(@PathVariable Integer id, @RequestBody @Valid A application) {
    ApplicationJson updatedApplication = doUpdate(id, application);
    return ResponseEntity.ok(mapApplication(updatedApplication));
  }

  private <T extends BaseApplication<?>> ApplicationJson doUpdate(Integer id, A application) {
    ApplicationJson applicationJson = application.getApplication();
    applicationJson.setExtension(application.getExtension());
    validateUpdateAllowed(id);
    return applicationServiceComposer.updateApplication(id, applicationJson);
  }

  private void validateUpdateAllowed(Integer id) {
    StatusType status = applicationServiceComposer.getApplicationStatus(id).getStatus();
    if (status != StatusType.NOTE
        && status != StatusType.HANDLING
        && status != StatusType.PRE_RESERVED
        && status != StatusType.RETURNED_TO_PREPARATION
        && (status != StatusType.PENDING || isExternalApplication(id))) {
      throw new IllegalArgumentException("application.applicationStatus.forbidden");
    }
  }

  private boolean isExternalApplication(Integer id) {
    return applicationServiceComposer.getApplicationExternalOwner(id) != null;
  }

  protected void validateType(Integer applicationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    validateType(application);
  }

  private void validateType(ApplicationJson application) {
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
