package fi.hel.allu.external.api.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.external.domain.*;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.external.service.LocationServiceExt;
import fi.hel.allu.servicecore.service.SupervisionTaskService;

@RestController
@RequestMapping({"/v1/applications", "/v2/applications"})
@Tag(name = "Applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

  @Autowired
  private ApplicationServiceExt applicationService;

  @Autowired
  private LocationServiceExt locationService;

  @Autowired
  private SupervisionTaskService supervisionTasksService;

  @Operation(summary = "Sets Allu application cancelled"
        )
  @RequestMapping(value = "/{id}/cancelled", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> cancelApplication(@Parameter(description = "Id of the application to cancel.") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    applicationService.cancelApplication(applicationId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Gets Allu application data for given application ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Application retrieved successfully",  content  = @Content(schema =
      @Schema(implementation = ApplicationExt.class))),
      @ApiResponse(responseCode = "404", description = "No application found for given ID")
  })
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ApplicationExt> getApplication(@Parameter(description = "Id of the application to get") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.findById(applicationId, ApplicationExtMapper::mapToApplicationExt));
  }

  @Operation(summary = "Gets application location data for given application ID")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Location data retrieved successfully", content = @Content(schema =
      @Schema(implementation = LocationExt.class))),
      @ApiResponse(responseCode = "404", description = "No location data found for given ID")
  })
  @RequestMapping(value = "/{id}/location", method = RequestMethod.GET,  produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<LocationExt> getLocation(@Parameter(description = "Id of the application") @PathVariable Integer id,
      @Parameter(description = "Spatial reference system ID of the  geometry.", allowEmptyValue = true) @RequestParam(required = false) Integer srId) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(locationService.findByApplicationId(applicationId, srId));
  }

  @Operation(summary = "Gets application customers for given application ID in a map, customer role as a key.",
      description = "Customer roles: "
              + "<ul>"
              + "<li>APPLICANT (hakija)</li>"
              + "<li>PROPERTY_DEVELOPER (rakennuttaja)</li>"
              + "<li>REPRESENTATIVE (asiamies)</li>"
              + "<li>CONTRACTOR (urakoitsija)</li>"
              + "</ul>")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Customers retrieved successfully", content = @Content(schema =
      @Schema(implementation = CustomerWithContactsExt.class))),
      @ApiResponse(responseCode = "404", description = "No customer data found for given ID")
  })
  @RequestMapping(value = "/{id}/customers", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Map<CustomerRoleType, CustomerWithContactsExt>> getCustomers(@Parameter(description = "Id of the application") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.findApplicationCustomers(applicationId));
  }

  @Operation(summary = "Gets invoice recipient for given application ID.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Invoice recipient retrieved successfully", content  = @Content(schema =
      @Schema(implementation = CustomerExt.class)) ),
      @ApiResponse(responseCode = "404", description = "No invoice recipient found for application with given ID")
  })
  @RequestMapping(value = "/{id}/invoicerecipient", method = RequestMethod.GET,  produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<CustomerExt> getInvoiceRecipient(@Parameter(description = "Id of the application") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    return ResponseEntity.ok(applicationService.findInvoiceRecipient(applicationId));
  }

  @Operation(summary = "Gets supervision tasks for application with given application ID.")
  @ApiResponses( value = {
      @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully", content  = @Content(schema =
      @Schema(implementation = SupervisionTaskExt.class))),
      @ApiResponse(responseCode = "404", description = "No tasks found for given application ID")
  })
  @RequestMapping(value = "/{id}/supervisiontasks", method = RequestMethod.GET,  produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<List<SupervisionTaskExt>> getSupervisionTasks(@Parameter(description = "Id of the application") @PathVariable Integer id) {
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

  @Operation(summary = "Marks required survey done for given application ID")
  @RequestMapping(value = "/{id}/surveydone", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> markSurveyDone(@Parameter(description = "Id of the application to mark survey done for.") @PathVariable Integer id) {
    Integer applicationId = applicationService.getApplicationIdForExternalId(id);
    applicationService.validateOwnedByExternalUser(applicationId);
    applicationService.markSurveyDone(applicationId);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
