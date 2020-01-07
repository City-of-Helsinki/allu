package fi.hel.allu.supervision.api.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.DateReportingService;
import fi.hel.allu.supervision.api.domain.DatePeriodReportJson;
import fi.hel.allu.supervision.api.domain.DateReportJson;
import fi.hel.allu.supervision.api.domain.ExcavationAnnouncementApplication;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/excavationannouncements")
@Api(tags = "Applications")
public class ExcavationAnnouncementController extends BaseApplicationDetailsController<ExcavationAnnouncementApplication, CreateExcavationAnnouncementApplicationJson> {

  @Autowired
  private DateReportingService dateReportingService;

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
  }

  @Override
  protected ExcavationAnnouncementApplication mapApplication(ApplicationJson application) {
    return new ExcavationAnnouncementApplication(application);
  }

  @ApiOperation(value = "Gets work finished approval document for excavation announcement with given ID. Returns draft if work finished is not yet approved.",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/approval/workfinished", method = RequestMethod.GET, produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<byte[]> getWorkFinishedDocument(@PathVariable Integer id) {
    validateType(id);
    return getApprovalDocument(id, ApprovalDocumentType.WORK_FINISHED);
  }

  @ApiOperation(value = "Gets operational condition approval document for excavation announcement with given ID. Returns draft if operational condition is not yet approved.",
      authorizations = @Authorization(value ="api_key"),
      response = byte.class,
      responseContainer = "Array")
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Document retrieved successfully", response = byte.class, responseContainer = "Array"),
      @ApiResponse(code = 404, message = "No document found for given application", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/{id}/approval/operationalcondition", method = RequestMethod.GET, produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<byte[]> getOperationalConditionDocument(@PathVariable Integer id) {
    validateType(id);
    return getApprovalDocument(id, ApprovalDocumentType.OPERATIONAL_CONDITION);
  }

  @ApiOperation(value = "Sends the operational condition approval document for given application as email to "
      + "an specified distribution list.",
      authorizations = @Authorization(value ="api_key")
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Approval document sent successfully")
  })
  @RequestMapping(value = "/{id}/operationalcondition/send", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> sendOperationalConditionDocument(@PathVariable Integer id,
      @RequestBody List<DistributionEntryJson> distribution) {
    validateType(id);
    applicationServiceComposer.sendDecision(id, new DecisionDetailsJson(distribution), DecisionDocumentType.OPERATIONAL_CONDITION);
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Sends the work finished approval document for given application as email to "
      + "an specified distribution list.",
      authorizations = @Authorization(value ="api_key")
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Approval document sent successfully")
  })
  @RequestMapping(value = "/{id}/workfinished/send", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> sendWorkFinishedDocument(@PathVariable Integer id,
      @RequestBody List<DistributionEntryJson> distribution) {
    validateType(id);
    applicationServiceComposer.sendDecision(id, new DecisionDetailsJson(distribution), DecisionDocumentType.WORK_FINISHED);
    return ResponseEntity.ok().build();
  }

  @Override
  @RequestMapping(value = "/{applicationId}/applicant", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                          @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerApplicant(applicationId, customer);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/propertyDeveloper", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerPropertyDeveloper(@PathVariable Integer applicationId,
                                                                                  @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerPropertyDeveloper(applicationId, customer);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/contractor", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerContractor(@PathVariable Integer applicationId,
                                                                           @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerContractor(applicationId, customer);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/representative", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(@PathVariable Integer applicationId,
                                                                               @RequestBody @ApiParam("The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerRepresentative(applicationId, customer);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/propertyDeveloper", method = RequestMethod.DELETE, produces = "application/json")
  public ResponseEntity<Void> removePropertyDeveloper(@PathVariable Integer applicationId) {
    return super.removePropertyDeveloper(applicationId);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/representative", method = RequestMethod.DELETE, produces = "application/json")
  public ResponseEntity<Void> removeRepresentative(@PathVariable Integer applicationId) {
    return super.removeRepresentative(applicationId);
  }

  @ApiOperation(value = "Report customer operational condition date",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json"
    )
    @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Date reported successfully"),
    })
  @RequestMapping(value = "/{id}/customeroperationalcondition", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerOperationalCondition(@ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
                                                                 @ApiParam(value = "Date report containing reporting date and operational condition date")
                                                                 @RequestBody @Valid DateReportJson dateReport) {
    validateType(id);
    dateReportingService.reportCustomerOperationalCondition(id, new ApplicationDateReport(dateReport.getReportingDate(), dateReport.getReportedDate(), null));
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Report customer work finished date",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json"
    )
    @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Date reported successfully"),
    })
  @RequestMapping(value = "/{id}/customerworkfinished", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerWorkFinished(@ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
                                                         @ApiParam(value = "Date report containing reporting date and work finished date")
                                                         @RequestBody @Valid DateReportJson dateReport) {
    validateType(id);
    dateReportingService.reportCustomerWorkFinished(id, new ApplicationDateReport(dateReport.getReportingDate(), dateReport.getReportedDate(), null));
    return ResponseEntity.ok().build();
  }

  @ApiOperation(value = "Report customer application validity period",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json"
    )
    @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Period reported successfully"),
    })
  @RequestMapping(value = "/{id}/customervalidity", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerValidityPeriod(@ApiParam(value = "Id of the application") @PathVariable("id") Integer id,
                                                           @ApiParam(value = "Period report containing reporting date and reported period")
                                                           @RequestBody @Valid DatePeriodReportJson dateReport) {
    validateType(id);
    dateReportingService.reportCustomerValidity(id, new ApplicationDateReport(dateReport.getReportingDate(),
        dateReport.getReportedStartDate(), dateReport.getReportedEndDate()));
    return ResponseEntity.ok().build();
  }

}
