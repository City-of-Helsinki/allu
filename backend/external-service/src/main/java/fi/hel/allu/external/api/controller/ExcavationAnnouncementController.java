package fi.hel.allu.external.api.controller;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.external.domain.ExcavationAnnouncementOutExt;
import fi.hel.allu.external.domain.ValidityPeriodExt;
import fi.hel.allu.external.mapper.ExcavationAnnouncementExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.validation.ApplicationExtGeometryValidator;
import fi.hel.allu.external.validation.DefaultImageValidator;
import fi.hel.allu.servicecore.service.*;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@RestController
@RequestMapping({"/v1/excavationannouncements", "/v2/excavationannouncements"})
@Api(tags = "Excavation announcements")
public class ExcavationAnnouncementController
        extends BaseApplicationController<ExcavationAnnouncementExt, ExcavationAnnouncementExtMapper> {
  private final ExcavationAnnouncementExtMapper mapper;

  private final ApprovalDocumentService approvalDocumentService;

  private final DateReportingService dateReportingService;

  public ExcavationAnnouncementController(ExcavationAnnouncementExtMapper mapper,
                                          ApplicationServiceExt applicationServiceExt,
                                          ApprovalDocumentService approvalDocumentService,
                                          DateReportingService dateReportingService,
                                          ApplicationExtGeometryValidator geometryValidator,
                                          DefaultImageValidator defaultImageValidator,
                                          DecisionService decisionService,
                                          TerminationService terminationService) {
    super(applicationServiceExt, geometryValidator, defaultImageValidator, decisionService, terminationService);
    this.mapper = mapper;
    this.approvalDocumentService = approvalDocumentService;
    this.dateReportingService = dateReportingService;
  }

  @Override
  protected ExcavationAnnouncementExtMapper getMapper() {
    return mapper;
  }

  @ApiOperation(value = "Report work finished date for excavation announcement specified by ID parameter.",
          authorizations = @Authorization(value = "api_key"))
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Date reported successfully", response = Void.class),
  })
  @PutMapping(value = "/{id}/workfinished")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportWorkFinished(
          @ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
          @ApiParam(value = "Work finished date") @RequestBody @NotNull ZonedDateTime workFinishedDate) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), workFinishedDate, null);
    dateReportingService.reportCustomerWorkFinished(applicationId, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Report operational condition date for excavation announcement specified by ID parameter.",
          authorizations = @Authorization(value = "api_key"))
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Date reported successfully", response = Void.class),
  })
  @PutMapping(value = "/{id}/operationalcondition")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportOperationalCondition(
          @ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
          @ApiParam(value = "Operational condition date") @RequestBody @NotNull ZonedDateTime operationalConditionDate) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    applicationService.validateModificationAllowed(applicationId);
    ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), operationalConditionDate,
                                                                 null);
    dateReportingService.reportCustomerOperationalCondition(applicationId, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Report change in application validity period. Operation is not allowed if application is " +
          "already finished.",
          authorizations = @Authorization(value = "api_key"))
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Validity period change reported successfully", response = Void.class),
  })
  @PutMapping(value = "/{id}/validityperiod")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportValidityPeriod(
          @ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
          @ApiParam(value = "Work finished date") @RequestBody @Valid ValidityPeriodExt validityPeriod) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(),
                                                                 validityPeriod.getValidityPeriodStart(), validityPeriod.getValidityPeriodEnd());
    dateReportingService.reportCustomerValidity(applicationId, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Gets operational condition approval document for application with given ID",
          authorizations = @Authorization(value = "api_key"),
          response = byte.class,
          responseContainer = "Array")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Approval document retrieved successfully", response = byte.class,
                  responseContainer = "Array"),
          @ApiResponse(code = 404, message = "No approval document found for given application", response =
                  ErrorInfo.class)
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

  @ApiOperation(value = "Gets work finished approval document for application with given ID",
          authorizations = @Authorization(value = "api_key"),
          response = byte.class,
          responseContainer = "Array")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Approval document retrieved successfully", response = byte.class,
                  responseContainer = "Array"),
          @ApiResponse(code = 404, message = "No approval document found for given application", response =
                  ErrorInfo.class)
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

  @ApiOperation(value = "Gets excavation announcement with given ID",
          authorizations = @Authorization(value = "api_key"),
          response = ExcavationAnnouncementOutExt.class)
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Application retrieved successfully", response =
                  ExcavationAnnouncementOutExt.class),
          @ApiResponse(code = 404, message = "No excavation announcement found for given ID", response =
                  ErrorInfo.class)
  })
  @GetMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ExcavationAnnouncementOutExt> getExcavationAnnouncement(@PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.findById(applicationId, (mapper::mapApplicationJson)));
  }
}
