package fi.hel.allu.external.api.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.domain.CustomerWithContactsExt;
import fi.hel.allu.external.domain.LocationExt;
import fi.hel.allu.external.domain.SupervisionTaskExt;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.service.LocationServiceExt;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.service.SupervisionTaskService;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/applications")
@Api(value = "v1/applications")
public class ApplicationController {

  @Autowired
  private ApplicationServiceExt applicationService;

  @Autowired
  private LocationServiceExt locationService;

  @Autowired
  private SupervisionTaskService supervisionTasksService;

  @ApiOperation(value = "Sets Allu application cancelled",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @RequestMapping(value = "/{id}/cancelled", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> cancelApplication(@ApiParam(value = "Id of the application to cancel.") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    applicationService.cancelApplication(applicationId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Gets Allu application data for given application ID",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Application retrieved successfully", response = ApplicationExt.class),
      @ApiResponse(code = 404, message = "No application found for given ID")
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ApplicationExt> getApplication(@ApiParam(value = "Id of the application to get") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.findById(applicationId));
  }

  @ApiOperation(value = "Gets application location data for given application ID",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Location data retrieved successfully", response = LocationExt.class),
      @ApiResponse(code = 404, message = "No location data found for given ID")
  })
  @RequestMapping(value = "/{id}/location", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<LocationExt> getLocation(@ApiParam(value = "Id of the application") @PathVariable Integer id,
      @ApiParam(value = "Spatial reference system ID of the  geometry.", required = false, defaultValue = "3879") @RequestParam(required = false) Integer srId) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(locationService.findByApplicationId(applicationId, srId));
  }

  @ApiOperation(value = "Gets application customers for given application ID in a map, customer role as a key.",
      notes = "Customer roles: "
              + "<ul>"
              + "<li>APPLICANT (hakija)</li>"
              + "<li>PROPERTY_DEVELOPER (rakennuttaja)</li>"
              + "<li>REPRESENTATIVE (asiamies)</li>"
              + "<li>CONTRACTOR (urakoitsija)</li>"
              + "</ul>",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Customers retrieved successfully", response = CustomerWithContactsExt.class, responseContainer = "Map"),
      @ApiResponse(code = 404, message = "No customer data found for given ID")
  })
  @RequestMapping(value = "/{id}/customers", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Map<CustomerRoleType, CustomerWithContactsExt>> getCustomers(@ApiParam(value = "Id of the application") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.findApplicationCustomers(applicationId));
  }

  @ApiOperation(value = "Gets supervision tasks for application with given application ID.",
      produces = "application/json",
      authorizations=@Authorization(value ="api_key"))
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Tasks retrieved successfully", response = SupervisionTaskExt.class, responseContainer = "List"),
      @ApiResponse(code = 404, message = "No tasks found for given application ID")
  })
  @RequestMapping(value = "/{id}/supervisiontasks", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<SupervisionTaskExt>> getSupervisionTasks(@ApiParam(value = "Id of the application") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    List<SupervisionTaskExt> supervisionTasks = supervisionTasksService.findByApplicationId(applicationId).stream()
        .map(t -> new SupervisionTaskExt(
            t.getActualFinishingTime() != null ? t.getActualFinishingTime() : t.getPlannedFinishingTime(),
            t.getType(),
            t.getStatus()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(supervisionTasks);
  }

}
