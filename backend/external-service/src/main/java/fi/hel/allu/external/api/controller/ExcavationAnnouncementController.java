package fi.hel.allu.external.api.controller;

import java.time.ZonedDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.external.domain.ExcavationAnnouncementExt;
import fi.hel.allu.external.domain.ValidityPeriodExt;
import fi.hel.allu.external.mapper.ExcavationAnnouncementExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.servicecore.service.ApprovalDocumentService;
import fi.hel.allu.servicecore.service.DateReportingService;
import fi.hel.allu.servicecore.service.DecisionService;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/excavationannouncements")
@Api(value = "v1/excavationannouncements")
public class ExcavationAnnouncementController
    extends BaseApplicationController<ExcavationAnnouncementExt, ExcavationAnnouncementExtMapper> {

  @Autowired
  private ExcavationAnnouncementExtMapper mapper;

  @Autowired
  private ApplicationServiceExt applicationService;

  @Autowired
  private ApprovalDocumentService approvalDocumentService;

  @Autowired
  private DecisionService decisionService;

  @Autowired
  private DateReportingService dateReportingService;

  @Override
  protected ExcavationAnnouncementExtMapper getMapper() {
    return mapper;
  }

  @ApiOperation(value = "Report work finished date for excavation announcement specified by ID parameter.",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Date reported successfully", response = Void.class),
  })
  @RequestMapping(value = "/{id}/workfinished", method = RequestMethod.PUT)
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
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Date reported successfully", response = Void.class),
  })
  @RequestMapping(value = "/{id}/operationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportOperationalCondition(
      @ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
      @ApiParam(value = "Operational condition date") @RequestBody @NotNull ZonedDateTime operationalConditionDate) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), operationalConditionDate, null);
    dateReportingService.reportCustomerOperationalCondition(applicationId, dateReport);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Report change in application validity period",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses(value =  {
      @ApiResponse(code = 200, message = "Validity period change reported successfully", response = Void.class),
  })
  @RequestMapping(value = "/{id}/validityperiod", method = RequestMethod.PUT)
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
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Approval document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No approval document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/approval/operationalcondition", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getOperationalConditionApprovalDocument(@PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    byte[] bytes = approvalDocumentService.getFinalApprovalDocument(applicationId, ApprovalDocumentType.OPERATIONAL_CONDITION);
    return returnPdfResponse(bytes);
  }

  @ApiOperation(value = "Gets work finished approval document for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Approval document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No approval document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/approval/workfinished", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getWorkFinishedApprovalDocument(@PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    byte[] bytes = approvalDocumentService.getFinalApprovalDocument(applicationId, ApprovalDocumentType.WORK_FINISHED);
    return returnPdfResponse(bytes);
  }

  @ApiOperation(value = "Gets decision document for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Decision document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No decision document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/decision", method = RequestMethod.GET, produces = "application/pdf")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<byte[]> getDecision(@PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    byte[] bytes = decisionService.getFinalDecision(applicationId);
    return returnPdfResponse(bytes);
  }
}
