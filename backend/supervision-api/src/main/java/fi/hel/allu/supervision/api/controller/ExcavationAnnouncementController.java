package fi.hel.allu.supervision.api.controller;

import java.util.List;

import javax.validation.Valid;

import fi.hel.allu.servicecore.service.*;
import fi.hel.allu.supervision.api.mapper.ApplicationMapperCollector;
import fi.hel.allu.supervision.api.service.ApplicationUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.supervision.api.domain.DatePeriodReportJson;
import fi.hel.allu.supervision.api.domain.DateReportJson;
import fi.hel.allu.supervision.api.domain.ExcavationAnnouncementApplication;

@RestController
@RequestMapping("/v1/excavationannouncements")
@Tag(name = "Applications")
public class ExcavationAnnouncementController extends BaseApplicationDetailsController<ExcavationAnnouncementApplication, CreateExcavationAnnouncementApplicationJson> {

  private final DateReportingService dateReportingService;

  public ExcavationAnnouncementController(ApprovalDocumentService approvalDocumentService,
                                          ChargeBasisService chargeBasisService,
                                          ApplicationServiceComposer applicationServiceComposer,
                                          ApplicationUpdateService applicationUpdateService,
                                          LocationService locationService,
                                          ApplicationMapperCollector applicationMapperCollector,
                                          DateReportingService dateReportingService) {
    super(approvalDocumentService, chargeBasisService, applicationServiceComposer, applicationUpdateService,
          locationService, applicationMapperCollector);
    this.dateReportingService = dateReportingService;
  }

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
  }

  @Override
  protected ExcavationAnnouncementApplication mapApplication(ApplicationJson application) {
    return new ExcavationAnnouncementApplication(application);
  }

  @Operation(summary = "Gets work finished approval document for excavation announcement with given ID. " +
      "Returns draft if work finished is not yet approved.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Document retrieved successfully",
          content = @Content(schema = @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "404", description = "No document found for given application",
          content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @GetMapping(value = "/{id}/approval/workfinished", produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<byte[]> getWorkFinishedDocument(@PathVariable Integer id) {
    validateType(id);
    return getApprovalDocument(id, ApprovalDocumentType.WORK_FINISHED);
  }

  @Operation(summary = "Gets operational condition approval document for excavation announcement with given ID. " +
      "Returns draft if operational condition is not yet approved.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Document retrieved successfully",
          content = @Content(schema = @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "404", description = "No document found for given application",
          content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @GetMapping(value = "/{id}/approval/operationalcondition", produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<byte[]> getOperationalConditionDocument(@PathVariable Integer id) {
    validateType(id);
    return getApprovalDocument(id, ApprovalDocumentType.OPERATIONAL_CONDITION);
  }

  @Operation(summary = "Sends the operational condition approval document for given application as email to "
      + "an specified distribution list.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Approval document sent successfully")
  })
  @PostMapping(value = "/{id}/operationalcondition/send")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> sendOperationalConditionDocument(@PathVariable Integer id,
      @RequestBody List<DistributionEntryJson> distribution) {
    validateType(id);
    applicationServiceComposer.sendDecision(id, new DecisionDetailsJson(distribution), DecisionDocumentType.OPERATIONAL_CONDITION);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Sends the work finished approval document for given application as email to "
      + "an specified distribution list.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Approval document sent successfully")
  })
  @PostMapping(value = "/{id}/workfinished/send")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> sendWorkFinishedDocument(@PathVariable Integer id,
      @RequestBody List<DistributionEntryJson> distribution) {
    validateType(id);
    applicationServiceComposer.sendDecision(id, new DecisionDetailsJson(distribution), DecisionDocumentType.WORK_FINISHED);
    return ResponseEntity.ok().build();
  }

  @Override
  @PutMapping(value = "/{applicationId}/applicant", produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                          @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerApplicant(applicationId, customer);
  }

  @Override
  @PutMapping(value = "/{applicationId}/propertyDeveloper", produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerPropertyDeveloper(@PathVariable Integer applicationId,
                                                                                  @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerPropertyDeveloper(applicationId, customer);
  }

  @Override
  @PutMapping(value = "/{applicationId}/contractor", produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerContractor(@PathVariable Integer applicationId,
                                                                           @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerContractor(applicationId, customer);
  }

  @Override
  @PutMapping(value = "/{applicationId}/representative", produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(@PathVariable Integer applicationId,
                                                                               @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerRepresentative(applicationId, customer);
  }

  @Override
  @DeleteMapping(value = "/{applicationId}/propertyDeveloper", produces = "application/json")
  public ResponseEntity<Void> removePropertyDeveloper(@PathVariable Integer applicationId) {
    return super.removePropertyDeveloper(applicationId);
  }

  @Override
  @DeleteMapping(value = "/{applicationId}/representative", produces = "application/json")
  public ResponseEntity<Void> removeRepresentative(@PathVariable Integer applicationId) {
    return super.removeRepresentative(applicationId);
  }

  @Operation(summary = "Report customer operational condition date")
    @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Date reported successfully"),
    })
  @PutMapping(value = "/{id}/customeroperationalcondition", consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerOperationalCondition(@Parameter(description = "Id of the application") @PathVariable("id") Integer id,
                                                                 @Parameter(description = "Date report containing reporting date and operational condition date")
                                                                 @RequestBody @Valid DateReportJson dateReport) {
    validateType(id);
    dateReportingService.reportCustomerOperationalCondition(id, new ApplicationDateReport(dateReport.getReportingDate(), dateReport.getReportedDate(), null));
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Report customer work finished date")
    @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Date reported successfully"),
    })
  @PutMapping(value = "/{id}/customerworkfinished", consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerWorkFinished(@Parameter(description = "Id of the application") @PathVariable("id") Integer id,
                                                         @Parameter(description = "Date report containing reporting date and work finished date")
                                                         @RequestBody @Valid DateReportJson dateReport) {
    validateType(id);
    dateReportingService.reportCustomerWorkFinished(id, new ApplicationDateReport(dateReport.getReportingDate(), dateReport.getReportedDate(), null));
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Report customer application validity period")
    @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Period reported successfully"),
    })
  @PutMapping(value = "/{id}/customervalidity", consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerValidityPeriod(@Parameter(description = "Id of the application") @PathVariable("id") Integer id,
                                                           @Parameter(description = "Period report containing reporting date and reported period")
                                                           @RequestBody @Valid DatePeriodReportJson dateReport) {
    validateType(id);
    dateReportingService.reportCustomerValidity(id, new ApplicationDateReport(dateReport.getReportingDate(),
        dateReport.getReportedStartDate(), dateReport.getReportedEndDate()));
    return ResponseEntity.ok().build();
  }

}
