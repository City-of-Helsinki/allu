package fi.hel.allu.supervision.api.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ApprovalDocumentType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.DateReportingService;
import fi.hel.allu.servicecore.service.InvoicingPeriodService;
import fi.hel.allu.servicecore.service.LocationService;
import fi.hel.allu.supervision.api.domain.AreaRentalApplication;
import fi.hel.allu.supervision.api.domain.DatePeriodReportJson;
import fi.hel.allu.supervision.api.domain.DateReportJson;
import fi.hel.allu.supervision.api.domain.InvoicingPeriodJson;

@RestController
@RequestMapping("/v1/arearentals")
@Tag(name = "Applications")
public class AreaRentalController extends BaseApplicationDetailsController<AreaRentalApplication, CreateAreaRentalApplicationJson> {

  private static final List<Integer> ALLOWABLE_PERIOD_LENGTHS = Arrays.asList(1, 3, 6, 12);

  @Autowired
  private DateReportingService dateReportingService;

  @Autowired
  private InvoicingPeriodService invoicingPeriodService;

  @Autowired
  private ApplicationService applicationService;

  @Autowired
  private LocationService locationService;

  @Override
  protected ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  @Override
  protected AreaRentalApplication mapApplication(ApplicationJson application) {
    return new AreaRentalApplication(application);
  }

  @Operation(summary = "Gets work finished approval document for area rental with given ID. Returns draft if work finished is not yet approved.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Document retrieved successfully",
              content = @Content( schema = @Schema(implementation = byte.class))),
      @ApiResponse(responseCode = "404", description = "No document found for given application",
              content = @Content( schema = @Schema(implementation = ErrorInfo.class)))
  })
  @SecurityRequirement(name = "bearerAuth")
  @RequestMapping(value = "/{id}/approval/workfinished", method = RequestMethod.GET, produces = {"application/pdf", "application/json"})
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<byte[]> getWorkFinishedDocument(@PathVariable Integer id) {
    validateType(id);
    return getApprovalDocument(id, ApprovalDocumentType.WORK_FINISHED);
  }

  @Operation(summary = "Sends the work finished approval document for given application as email to "
      + "an specified distribution list.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Approval document sent successfully")
  })
  @SecurityRequirement(name = "bearerAuth")
  @RequestMapping(value = "/{id}/workfinished/send", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> sendWorkFinishedDocument(@PathVariable Integer id,
      @RequestBody List<DistributionEntryJson> distribution) {
    validateType(id);
    applicationServiceComposer.sendDecision(id, new DecisionDetailsJson(distribution), DecisionDocumentType.WORK_FINISHED);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Create a new location")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Location created successfully",
            content = @Content( schema = @Schema(implementation = Location.class))),
    @ApiResponse(responseCode = "400", description = "Invalid location data",
            content = @Content( schema = @Schema(implementation = ErrorInfo.class))),
    @ApiResponse(responseCode = "403", description = "Location addition forbidden",
            content = @Content( schema = @Schema(implementation = ErrorInfo.class)))
  })
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  @RequestMapping(value = "/{applicationId}/locations", method = RequestMethod.POST,
    produces = "application/json", consumes = "application/json")
  public ResponseEntity<Location> createLocation(@PathVariable Integer applicationId,
                                                 @RequestBody @Valid Location location) {
    validateType(applicationId);
    location.setApplicationId(applicationId);
    Location createdLocation = locationService.insertLocation(location);
    return ResponseEntity.ok(createdLocation);
  }

  @Operation(summary = "Delete an existing location")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Location deleted successfully",
            content = @Content( schema = @Schema(implementation = Location.class))),
    @ApiResponse(responseCode = "400", description = "Invalid location data or attempt to remove the last location",
            content = @Content( schema = @Schema(implementation = ErrorInfo.class))),
    @ApiResponse(responseCode = "403", description = "Location deletion forbidden",
            content = @Content( schema = @Schema(implementation = ErrorInfo.class)))
  })
  @SecurityRequirement(name = "bearerAuth")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  @RequestMapping(value = "/{applicationId}/locations/{locationId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteLocation(@PathVariable Integer applicationId,
                                             @PathVariable Integer locationId) {
    ApplicationJson application = applicationServiceComposer.findApplicationById(applicationId);
    validateType(application);
    if (application.getLocations().stream().noneMatch(locationJson -> locationId.equals(locationJson.getId()))) {
      return ResponseEntity.notFound().build();
    }
    if (application.getLocations().size() <= 1) {
      // the last location should not be removed
      throw new IllegalArgumentException("application.location.deleteLastLocationForbidden");
    }
    locationService.deleteLocation(locationId);
    return ResponseEntity.ok().build();
  }

  @Override
  @RequestMapping(value = "/{applicationId}/applicant", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerApplicant(@PathVariable Integer applicationId,
                                                                          @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerApplicant(applicationId, customer);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/propertyDeveloper", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerPropertyDeveloper(@PathVariable Integer applicationId,
                                                                                  @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerPropertyDeveloper(applicationId, customer);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/contractor", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerContractor(@PathVariable Integer applicationId,
                                                                           @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
    return super.updateCustomerContractor(applicationId, customer);
  }

  @Override
  @RequestMapping(value = "/{applicationId}/representative", method = RequestMethod.PUT, produces = "application/json")
  public ResponseEntity<CustomerWithContactsJson> updateCustomerRepresentative(@PathVariable Integer applicationId,
                                                                               @RequestBody @Parameter(description = "The new customer with contacts") CreateCustomerWithContactsJson customer) {
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

  @Operation(summary = "Set invoicing period length for area rental with given ID. Ignored if application is not billable.")
  @SecurityRequirement(name = "bearerAuth")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Invoicing period length set successfully",
              content = @Content( schema = @Schema(implementation = InvoicingPeriodJson.class)))
  })
  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<InvoicingPeriodJson>> setInvoicingPeriodLength(@PathVariable Integer id,
      @Parameter(description = "Period length in months",  example = "1, 3, 6, 12") @RequestParam int periodLength) {
    validateType(id);
    validateUpdateAllowed(id);
    validatePeriodLength(periodLength);
    List<InvoicingPeriodJson> result;
    if (applicationService.isBillable(id)) {
      result = invoicingPeriodService.updateInvoicingPeriods(id, periodLength)
        .stream()
        .map(i -> new InvoicingPeriodJson(i.getId(), i.getStartTime(), i.getEndTime()))
        .collect(Collectors.toList());
    } else {
      result = Collections.emptyList();
    }
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "Remove invoicing periods from area rental with given ID.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Invoicing periods removed successfully")
  })
  @SecurityRequirement(name = "bearerAuth")
  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.DELETE, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> deleteInvoicingPeriods(@PathVariable Integer id) {
    validateType(id);
    validateUpdateAllowed(id);
    invoicingPeriodService.deleteInvoicingPeriods(id);
    return ResponseEntity.ok().build();
  }


  @Operation(summary = "Report customer work finished date")
    @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Date reported successfully"),
    })
  @SecurityRequirement(name = "bearerAuth")
  @RequestMapping(value = "/{id}/customerworkfinished", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerWorkFinished(@Parameter(description = "Id of the application") @PathVariable("id") Integer id,
                                                         @Parameter(description = "Date report containing reporting date and work finished date")
                                                         @RequestBody @Valid DateReportJson dateReport) {
    validateType(id);
    dateReportingService.reportCustomerWorkFinished(id, new ApplicationDateReport(dateReport.getReportingDate(), dateReport.getReportedDate(), null));
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "Report customer location validity period")
  @SecurityRequirement(name = "bearerAuth")
    @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Period reported successfully"),
    })
  @RequestMapping(value = "/{applicationId}/locations/{locationId}/customervalidity", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerLocationValidityPeriod(@Parameter(description = "Id of the application") @PathVariable("applicationId") Integer applicationId,
                                                                   @Parameter(description = "Id of the location") @PathVariable("locationId") Integer locationId,
                                                                   @Parameter(description = "Period report containing reporting date and reported period")
                                                                   @RequestBody @Valid DatePeriodReportJson dateReport) {
    validateType(applicationId);
    validateApplicationHasLocation(applicationId, locationId);
    dateReportingService.reportCustomerLocationValidity(applicationId, locationId, new ApplicationDateReport(dateReport.getReportingDate(),
        dateReport.getReportedStartDate(), dateReport.getReportedEndDate()));
    return ResponseEntity.ok().build();
  }

  private void validateApplicationHasLocation(Integer applicationId, Integer locationId) {
    if (!locationService.getLocationById(locationId).getApplicationId().equals(applicationId)) {
      throw new NoSuchEntityException("application.location.notFound");
    }
  }

  private void validatePeriodLength(int periodLength) {
    if (!ALLOWABLE_PERIOD_LENGTHS.contains(periodLength)) {
      throw new IllegalArgumentException("application.invoicingPeriod.invalid");
    }
  }
}
