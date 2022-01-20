package fi.hel.allu.supervision.api.controller;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.InvoicingPeriodService;
import fi.hel.allu.supervision.api.domain.ChargeBasisEntryJson;
import fi.hel.allu.supervision.api.domain.InvoicingPeriodJson;
import fi.hel.allu.supervision.api.mapper.ChargeBasisEntryMapper;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/")
@Api(tags = "Charge basis entries")
public class ChargeBasisController {

  private final ChargeBasisService chargeBasisService;

  private final InvoicingPeriodService invoicingPeriodService;

  private final ApplicationService applicationService;

  public ChargeBasisController(ChargeBasisService chargeBasisService, InvoicingPeriodService invoicingPeriodService, ApplicationService applicationService) {
    this.chargeBasisService = chargeBasisService;
    this.invoicingPeriodService = invoicingPeriodService;
    this.applicationService = applicationService;
  }

  @ApiOperation(value = "List charge basis entries for application with given ID",
    authorizations = @Authorization(value = "api_key"),
    produces = "application/json",
    response = ChargeBasisEntryJson.class,
    responseContainer = "List"
  )
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Charge basis entries retrieved successfully", response = ChargeBasisEntryJson.class, responseContainer = "List")
  })
  @GetMapping(value = "/applications/{id}/chargebasisentries", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<ChargeBasisEntryJson>> findByApplication(@PathVariable Integer id) {
    List<ChargeBasisEntryJson> result;
    if (applicationService.isBillable(id)) {
      result = chargeBasisService.getChargeBasis(id)
        .stream()
        .map(ChargeBasisEntryMapper::mapToJson)
        .collect(Collectors.toList());
      if (applicationService.getApplicationStatus(id).getStatus() == StatusType.DECISIONMAKING) {
        result.forEach(e -> e.setLocked(true));
      }

    } else {
      result = Collections.emptyList();
    }
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "List invoicing periods for application with given ID",
    authorizations = @Authorization(value = "api_key"),
    produces = "application/json",
    response = InvoicingPeriodJson.class,
    responseContainer = "List"
  )
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Invoicing periods retrieved successfully", response = InvoicingPeriodJson.class, responseContainer = "List")
  })
  @GetMapping(value = "/applications/{id}/invoicingperiods", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<InvoicingPeriodJson>> findPeriodsByApplication(@PathVariable Integer id) {
    List<InvoicingPeriodJson> result;
    if (applicationService.isBillable(id)) {
      result = invoicingPeriodService.getInvoicingPeriods(id)
        .stream()
        .map(i -> new InvoicingPeriodJson(i.getId(), i.getStartTime(), i.getEndTime()))
        .collect(Collectors.toList());
    } else {
      result = Collections.emptyList();
    }
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "Add charge basis entry for application. Returns created entry.",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json",
    response = ChargeBasisEntryJson.class
  )
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Entry added successfully", response = ChargeBasisEntryJson.class),
    @ApiResponse(code = 403, message = "Entry addition forbidden", response = ErrorInfo.class)
  })
  @PostMapping(value = "/applications/{applicationid}/chargebasisentries", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> addChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
                                                                  @RequestBody @Valid ChargeBasisEntryJson entry) {
    ChargeBasisEntry inserted = chargeBasisService.insertEntry(applicationId, ChargeBasisEntryMapper.mapToModel(entry));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(inserted));
  }

  @ApiOperation(value = "Update charge basis entry. Returns updated entry.",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json",
    response = ChargeBasisEntryJson.class
  )
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Entry updated successfully", response = ChargeBasisEntryJson.class),
    @ApiResponse(code = 403, message = "Entry update forbidden", response = ErrorInfo.class)
  })
  @PutMapping(value = "/applications/{applicationid}/chargebasisentries/{id}", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> updateChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
                                                                     @PathVariable(value = "id") Integer id, @RequestBody @Valid ChargeBasisEntryJson entry) {
    ChargeBasisEntry updated = chargeBasisService.updateEntry(applicationId, id, ChargeBasisEntryMapper.mapToModel(entry));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(updated));
  }

  @ApiOperation(value = "Delete charge basis entry.",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json"
  )
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Entry deleted successfully"),
    @ApiResponse(code = 403, message = "Entry deletion forbidden", response = ErrorInfo.class)
  })
  @DeleteMapping(value = "/applications/{applicationid}/chargebasisentries/{id}", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> deleteChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
                                                     @PathVariable(value = "id") Integer id) {
    chargeBasisService.deleteEntry(applicationId, id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Set charge basis entry invoicable / not invoicable.",
    authorizations = @Authorization(value = "api_key"),
    consumes = "application/json",
    produces = "application/json"
  )
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Entry updated successfully"),
    @ApiResponse(code = 403, message = "Entry update forbidden", response = ErrorInfo.class)
  })
  @PutMapping(value = "/applications/{applicationId}/chargebasisentries/{id}/invoicable", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> setInvoicable(@PathVariable int applicationId, @PathVariable int id,
                                                            @RequestParam("invoicable") boolean invoicable) {
    chargeBasisService.validateInvoicableChangeAllowed(applicationId, chargeBasisService.getEntry(applicationId, id));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(chargeBasisService.setInvoicable(applicationId, id, invoicable)));
  }
}
