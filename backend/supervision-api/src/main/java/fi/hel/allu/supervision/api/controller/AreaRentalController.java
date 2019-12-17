package fi.hel.allu.supervision.api.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

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
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.CreateAreaRentalApplicationJson;
import fi.hel.allu.servicecore.domain.CreateCustomerWithContactsJson;
import fi.hel.allu.servicecore.domain.CustomerWithContactsJson;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.DateReportingService;
import fi.hel.allu.servicecore.service.InvoicingPeriodService;
import fi.hel.allu.servicecore.service.LocationService;
import fi.hel.allu.supervision.api.domain.AreaRentalApplication;
import fi.hel.allu.supervision.api.domain.DatePeriodReportJson;
import fi.hel.allu.supervision.api.domain.DateReportJson;
import fi.hel.allu.supervision.api.domain.InvoicingPeriodJson;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/arearentals")
@Api(tags = "Applications")
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

  @ApiOperation(value = "Gets work finished approval document for area rental with given ID. Returns draft if work finished is not yet approved.",
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

  @ApiOperation(value = "Create a new location",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json",
    response = Location.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Location created successfully", response = Location.class),
    @ApiResponse(code = 400, message = "Invalid location data", response = ErrorInfo.class),
    @ApiResponse(code = 403, message = "Location addition forbidden", response = ErrorInfo.class)
  })
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

  @ApiOperation(value = "Delete an existing location",
    authorizations = @Authorization(value = "api_key"),
    response = Location.class)
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Location deleted successfully", response = Location.class),
    @ApiResponse(code = 400, message = "Invalid location data or attempt to remove the last location", response = ErrorInfo.class),
    @ApiResponse(code = 403, message = "Location deletion forbidden", response = ErrorInfo.class)
  })
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

  @ApiOperation(value = "Set invoicing period length for area rental with given ID. Ignored if application is not billable.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = InvoicingPeriodJson.class,
      responseContainer="List"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Invoicing period length set successfully", response = InvoicingPeriodJson.class, responseContainer="List")
  })
  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<InvoicingPeriodJson>> setInvoicingPeriodLength(@PathVariable Integer id,
      @ApiParam(value = "Period length in months", allowableValues = "1, 3, 6, 12") @RequestParam int periodLength) {
    validateType(id);
    validateUpdateAllowed(id);
    validatePeriodLength(periodLength);
    List<InvoicingPeriodJson> result;
    if (applicationService.isBillable(id)) {
      result = invoicingPeriodService.createInvoicingPeriods(id, periodLength)
        .stream()
        .map(i -> new InvoicingPeriodJson(i.getId(), i.getStartTime(), i.getEndTime()))
        .collect(Collectors.toList());
    } else {
      result = Collections.emptyList();
    }
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "Remove invoicing periods from area rental with given ID.",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Invoicing periods removed successfully")
  })
  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.DELETE, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> deleteInvoicingPeriods(@PathVariable Integer id) {
    validateType(id);
    validateUpdateAllowed(id);
    invoicingPeriodService.deleteInvoicingPeriods(id);
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

  @ApiOperation(value = "Report customer location validity period",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json"
    )
    @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Period reported successfully"),
    })
  @RequestMapping(value = "/{applicationId}/locations/{locationId}/customervalidity", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> reportCustomerLocationValidityPeriod(@ApiParam(value = "Id of the application") @PathVariable("applicationId") Integer applicationId,
                                                                   @ApiParam(value = "Id of the location") @PathVariable("locationId") Integer locationId,
                                                                   @ApiParam(value = "Period report containing reporting date and reported period")
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
