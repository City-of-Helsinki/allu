package fi.hel.allu.external.api.controller;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import fi.hel.allu.external.domain.*;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.JsonProcessingException;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.util.PdfMerger;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import fi.hel.allu.external.validation.DefaultImageValidator;
import fi.hel.allu.servicecore.service.DecisionService;

/**
 * Base class for external application controllers
 */
@SecurityRequirement(name = "bearerAuth")
public abstract class BaseApplicationController<T extends BaseApplicationExt, M extends ApplicationExtMapper<T>>  {

  @Autowired
  protected ApplicationServiceExt applicationService;

  @Autowired
  protected ApplicationExtGeometryValidator geometryValidator;

  @Autowired
  protected DefaultImageValidator defaultImageValidator;

  @Autowired
  private DecisionService decisionService;

  @Autowired
  private TerminationService terminationService;

  protected abstract M getMapper();

  @InitBinder
  protected void initBinder(WebDataBinder binder) {
    if (binder.getTarget() != null && BaseApplicationExt.class.isAssignableFrom(binder.getTarget().getClass())) {
      binder.addValidators(geometryValidator, defaultImageValidator);
      addApplicationTypeSpecificValidators(binder);
    }
  }

  protected void addApplicationTypeSpecificValidators(WebDataBinder binder) {
  }

  @Operation(summary = "Create new application", description =  "Create new application. Returns ID of the created application. "
      + "If application is still pending in client side, pendingOnClient should be set to true to prevent handling "
      + "of application in Allu and to allow later modification of application data by client. If application is ready to be handled "
      + "in Allu, pendingOnClient should be set to false.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Application added successfully", content = @Content(schema =
      @Schema(implementation = Integer.class))),
      @ApiResponse(responseCode = "400", description = "Invalid application", content = @Content(schema =
      @Schema(implementation = ErrorInfo.class)) )
  })
  @RequestMapping(method = RequestMethod.POST, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> create(@Parameter(description = "Application data", required = true)
                                        @Valid @RequestBody T applicationExt) throws JsonProcessingException {
    return new ResponseEntity<>(applicationService.createApplication(applicationExt, getMapper()), HttpStatus.OK);
  }

  @Operation(summary = "Update application. Allowed only if handling of application has not been started")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Application updated successfully", content = @Content(schema =
      @Schema(implementation = Integer.class))),
      @ApiResponse(responseCode = "400", description = "Invalid application",  content = @Content(schema =
      @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Integer> update(@Parameter(description = "Id of the application to update.")
                                        @PathVariable Integer id,
                                        @Parameter(description = "Application data", required = true)
                                        @Valid @RequestBody T application) throws JsonProcessingException {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateFullUpdateAllowed(applicationId);
    applicationService.validateOwnedByExternalUser(applicationId);
    return new ResponseEntity<>(applicationService.updateApplication(applicationId, application, getMapper()), HttpStatus.OK);
  }

  @Operation(summary = "Send response for information request specified by ID parameter.", description =
          "Only fields listed in response are processed in Allu. Also data sent through some separate API " +
                  "(e.g. application attachments) should be included in field list of response.")
  @ApiResponses(value =  {
      @ApiResponse(responseCode = "200", description = "Response added successfully", content = @Content(schema =
      @Schema(implementation = ErrorInfo.class))),
      @ApiResponse(responseCode = "400", description = "Invalid request response", content = @Content(schema =
      @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(value = "{applicationid}/informationrequests/{requestid}/response", method = RequestMethod.POST, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> addResponse(@Parameter(description = "Id of the application") @PathVariable("applicationid") Integer applicationId,
                                          @Parameter(description = "Id of the information request") @PathVariable("requestid") Integer requestId,
                                          @Parameter(description = "Content of the response") @RequestBody @Valid InformationRequestResponseExt<T> response) throws JsonProcessingException {
    applicationService.addInformationRequestResponse(applicationService.getApplicationIdForExternalId(applicationId), requestId, response, getMapper());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Report application changes.", description = "Only fields listed in change information are processed in Allu. "
    + "Also data sent through some separate API (e.g. application attachments) should be included in field list of change. " +
    "Reporting changes is only allowed for application in \"decision\" or \"operational condition\" states.")
  @ApiResponses(value =  {
    @ApiResponse(responseCode = "200", description = "Change reported successfully", content = @Content),
    @ApiResponse(responseCode = "400", description = "Invalid change information", content = @Content(schema =
    @Schema(implementation = ErrorInfo.class))),
    @ApiResponse(responseCode = "403", description = "Reported change not allowed", content = @Content(schema =
    @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(value = "{applicationid}/reportchange", method = RequestMethod.POST, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportChange(@Parameter(description = "Id of the application") @PathVariable("applicationid") Integer applicationId,
                                           @Parameter(description = "Contents of the change") @RequestBody @Valid InformationRequestResponseExt<T> change) throws JsonProcessingException {
    applicationService.reportApplicationChange(applicationService.getApplicationIdForExternalId(applicationId), change, getMapper());
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Gets decision document for application with given ID")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Decision document retrieved successfully", content = @Content(schema =
      @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "404", description = "No decision document found for given application", content = @Content(schema =
      @Schema(implementation = ErrorInfo.class)))
  })
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getDecision(@PathVariable Integer id) throws IOException {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    byte[] decision = decisionService.getFinalDecision(applicationId);

    List<byte[]> attachments = getDecisionAttachments(applicationId);
    return PdfResponseBuilder.createResponseEntity(PdfMerger.appendDocuments(decision, attachments));
  }

  protected List<byte[]> getDecisionAttachments(Integer applicationId) {
    return applicationService.getDecisionAttachmentDocuments(applicationId);
  }

  @Operation(summary = "Gets decision metadata for application with given ID. If there's not yet decision for application, decision maker is null")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Decision metadata retrieved successfully", content = @Content(schema =
      @Schema(implementation = DecisionExt.class))),
  })
  @RequestMapping(value = "/{id}/decision/metadata", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<DecisionExt> getDecisionMetadata(@PathVariable Integer id)  {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    UserExt handler = applicationService.getHandler(applicationId);
    UserExt decisionMaker = applicationService.getDecisionMaker(applicationId);
    return ResponseEntity.ok(new DecisionExt(handler, decisionMaker));
  }

  protected ResponseEntity<byte[]> getTerminationDocument(Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    byte[] termination = terminationService.getFinalTermination(applicationId);
    return PdfResponseBuilder.createResponseEntity(termination);
  }
}
