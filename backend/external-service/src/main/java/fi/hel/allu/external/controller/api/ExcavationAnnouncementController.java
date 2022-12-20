package fi.hel.allu.external.controller.api;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.external.domain.ExcavationAnnouncementOutExt;
import fi.hel.allu.external.domain.ValidityPeriodExt;
import fi.hel.allu.external.mapper.ExcavationAnnouncementExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.Validators;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.DateReportingService;
import fi.hel.allu.servicecore.service.DecisionService;
import fi.hel.allu.servicecore.service.TerminationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@RestController
@RequestMapping({"/v1/excavationannouncements", "/v2/excavationannouncements"})
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Excavation announcements", description = "Excavation announcement application API")
public class ExcavationAnnouncementController
        extends BaseApplicationController<ExcavationAnnouncementExt, ExcavationAnnouncementExtMapper> {
    private final ExcavationAnnouncementExtMapper mapper;

    private final ApprovalDocumentService approvalDocumentService;

    private final DateReportingService dateReportingService;

    public ExcavationAnnouncementController(ExcavationAnnouncementExtMapper mapper,
                                            ApplicationServiceExt applicationServiceExt,
                                            ApprovalDocumentService approvalDocumentService,
                                            DateReportingService dateReportingService,
                                            Validators validators,
                                            DecisionService decisionService,
                                            TerminationService terminationService) {
        super(applicationServiceExt, decisionService, validators, terminationService);
        this.mapper = mapper;
        this.approvalDocumentService = approvalDocumentService;
        this.dateReportingService = dateReportingService;
    }

    @Override
    protected ExcavationAnnouncementExtMapper getMapper() {
        return mapper;
    }

    @Operation(summary = "Report work finished date for excavation announcement specified by ID parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Date reported successfully", content = @Content)
    })
    @PutMapping(value = "/{id}/workfinished")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<Void> reportWorkFinished(
            @Parameter(description = "Id of the application") @PathVariable("id") Integer id,
            @Parameter(description = "Work finished date") @RequestBody @NotNull ZonedDateTime workFinishedDate) {
        Integer applicationId = applicationService.getApplicationIdForExternalId(id);
        applicationService.validateOwnedByExternalUser(applicationId);
        ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), workFinishedDate, null);
        dateReportingService.reportCustomerWorkFinished(applicationId, dateReport);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Report operational condition date for excavation announcement specified by ID parameter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Date reported successfully", content = @Content),
    })
    @PutMapping(value = "/{id}/operationalcondition")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<Void> reportOperationalCondition(
            @Parameter(description = "Id of the application") @PathVariable("id") Integer id,
            @Parameter(description = "Operational condition date") @RequestBody
            @NotNull ZonedDateTime operationalConditionDate) {
        Integer applicationId = applicationService.getApplicationIdForExternalId(id);
        applicationService.validateOwnedByExternalUser(applicationId);
        applicationService.validateModificationAllowed(applicationId);
        ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), operationalConditionDate,
                                                                     null);
        dateReportingService.reportCustomerOperationalCondition(applicationId, dateReport);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Report change in application validity period. Operation is not allowed if application is " +
            "already finished.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validity period change reported successfully",
                    content = @Content),
    })
    @PutMapping(value = "/{id}/validityperiod")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<Void> reportValidityPeriod(
            @Parameter(description = "Id of the application") @PathVariable("id") Integer id,
            @Parameter(description = "Work finished date") @RequestBody @Valid ValidityPeriodExt validityPeriod) {
        Integer applicationId = applicationService.getApplicationIdForExternalId(id);
        applicationService.validateOwnedByExternalUser(applicationId);
        ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(),
                                                                     validityPeriod.getValidityPeriodStart(),
                                                                     validityPeriod.getValidityPeriodEnd());
        dateReportingService.reportCustomerValidity(applicationId, dateReport);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Gets operational condition approval document for application with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approval document retrieved successfully",
                    content = @Content(schema = @Schema(implementation = byte.class))),
            @ApiResponse(responseCode = "404", description = "No approval document found for given application",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/{id}/approval/operationalcondition", produces = "application" +
            "/pdf")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<byte[]> getOperationalConditionApprovalDocument(@PathVariable Integer id) {
        Integer applicationId = applicationService.getApplicationIdForExternalId(id);
        applicationService.validateOwnedByExternalUser(applicationId);
        byte[] bytes = approvalDocumentService.getFinalApprovalDocument(applicationId,
                                                                        ApprovalDocumentType.OPERATIONAL_CONDITION);
        return PdfResponseBuilder.createResponseEntity(bytes);
    }

    @Operation(summary = "Gets work finished approval document for application with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Approval document retrieved successfully",
                    content = @Content(schema = @Schema(implementation = byte.class))),
            @ApiResponse(responseCode = "404", description = "No approval document found for given application",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/{id}/approval/workfinished", produces = "application/pdf")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<byte[]> getWorkFinishedApprovalDocument(@PathVariable Integer id) {
        Integer applicationId = applicationService.getApplicationIdForExternalId(id);
        applicationService.validateOwnedByExternalUser(applicationId);
        byte[] bytes = approvalDocumentService.getFinalApprovalDocument(applicationId,
                                                                        ApprovalDocumentType.WORK_FINISHED);
        return PdfResponseBuilder.createResponseEntity(bytes);
    }

    @Operation(summary = "Gets excavation announcement with given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Application retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExcavationAnnouncementExt.class))),
            @ApiResponse(responseCode = "404", description = "No excavation announcement found for given ID",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
    public ResponseEntity<ExcavationAnnouncementOutExt> getExcavationAnnouncement(@PathVariable Integer id) {
        Integer applicationId = applicationService.getApplicationIdForExternalId(id);
        applicationService.validateOwnedByExternalUser(applicationId);
        return ResponseEntity.ok(applicationService.findById(applicationId, (mapper::mapApplicationJson)));
    }
}