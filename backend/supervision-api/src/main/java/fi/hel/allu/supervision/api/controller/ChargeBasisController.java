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
import io.swagger.v3.oas.annotations.Operation;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Charge basis entries")
public class ChargeBasisController {

  private final ChargeBasisService chargeBasisService;

  private final InvoicingPeriodService invoicingPeriodService;

  private final ApplicationService applicationService;

  public ChargeBasisController(ChargeBasisService chargeBasisService, InvoicingPeriodService invoicingPeriodService, ApplicationService applicationService) {
    this.chargeBasisService = chargeBasisService;
    this.invoicingPeriodService = invoicingPeriodService;
    this.applicationService = applicationService;
  }

  @Operation(summary = "List charge basis entries for application with given ID")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Charge basis entries retrieved successfully",
            content = @Content(schema = @Schema(implementation = ChargeBasisEntryJson.class)))
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

  @Operation(summary = "List invoicing periods for application with given ID"
  )
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Invoicing periods retrieved successfully",
            content = @Content(schema = @Schema(implementation = InvoicingPeriodJson.class)))
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

  @Operation(summary = "Add charge basis entry for application. Returns created entry.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entry added successfully",
            content = @Content(schema = @Schema(implementation = ChargeBasisEntryJson.class))),
    @ApiResponse(responseCode = "403", description = "Entry addition forbidden",
            content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @PostMapping(value = "/applications/{applicationid}/chargebasisentries", consumes = "application/json", produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> addChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
                                                                  @RequestBody @Valid ChargeBasisEntryJson entry) {
    ChargeBasisEntry inserted = chargeBasisService.insertEntry(applicationId, ChargeBasisEntryMapper.mapToModel(entry));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(inserted));
  }

  @Operation(summary = "Update charge basis entry. Returns updated entry.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entry updated successfully",
            content = @Content(schema = @Schema(implementation = ChargeBasisEntryJson.class))),
    @ApiResponse(responseCode = "403", description = "Entry update forbidden",
            content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @PutMapping(value = "/applications/{applicationid}/chargebasisentries/{id}", produces = "application/json", consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> updateChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
                                                                     @PathVariable(value = "id") Integer id, @RequestBody @Valid ChargeBasisEntryJson entry) {
    ChargeBasisEntry updated = chargeBasisService.updateEntry(applicationId, id, ChargeBasisEntryMapper.mapToModel(entry));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(updated));
  }

  @Operation(summary = "Delete charge basis entry.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entry deleted successfully"),
    @ApiResponse(responseCode = "403", description = "Entry deletion forbidden",
            content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @DeleteMapping(value = "/applications/{applicationid}/chargebasisentries/{id}", produces = "application/json",
          consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> deleteChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
                                                     @PathVariable(value = "id") Integer id) {
    chargeBasisService.deleteEntry(applicationId, id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(summary = "Set charge basis entry invoicable / not invoicable.")
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Entry updated successfully"),
    @ApiResponse(responseCode = "403", description = "Entry update forbidden",
            content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
  })
  @PutMapping(value = "/applications/{applicationId}/chargebasisentries/{id}/invoicable", produces = "application/json",
          consumes = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> setInvoicable(@PathVariable int applicationId, @PathVariable int id,
                                                            @RequestParam("invoicable") boolean invoicable) {
    chargeBasisService.validateInvoicableChangeAllowed(applicationId, chargeBasisService.getEntry(applicationId, id));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(chargeBasisService.setInvoicable(applicationId, id, invoicable)));
  }
}
