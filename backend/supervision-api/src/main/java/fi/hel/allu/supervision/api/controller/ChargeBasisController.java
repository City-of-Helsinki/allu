package fi.hel.allu.supervision.api.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.common.exception.ErrorInfo;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import fi.hel.allu.servicecore.service.InvoicingPeriodService;
import fi.hel.allu.supervision.api.domain.ChargeBasisEntryJson;
import fi.hel.allu.supervision.api.domain.InvoicingPeriodJson;
import fi.hel.allu.supervision.api.mapper.ChargeBasisEntryMapper;
import io.swagger.annotations.*;

@RestController
@RequestMapping("/v1/")
@Api(tags = "Charge basis entries")
public class ChargeBasisController {

  @Autowired
  private ChargeBasisService chargeBasisService;

  @Autowired
  private InvoicingPeriodService invoicingPeriodService;

  @Autowired
  private ApplicationService applicationService;

  @ApiOperation(value = "List charge basis entries for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = ChargeBasisEntryJson.class,
      responseContainer="List"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Charge basis entries retrieved successfully", response = ChargeBasisEntryJson.class, responseContainer="List")
  })
  @RequestMapping(value = "/applications/{id}/chargebasisentries", method = RequestMethod.GET, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE', 'ROLE_VIEW')")
  public ResponseEntity<List<ChargeBasisEntryJson>> findByApplication(@PathVariable Integer id) {
    List<ChargeBasisEntryJson> result;
    if (applicationService.isBillable(id)) {
      result = chargeBasisService.getChargeBasis(id)
        .stream()
        .map(e -> ChargeBasisEntryMapper.mapToJson(e))
        .collect(Collectors.toList());
    } else {
      result = Collections.emptyList();
    }
    return ResponseEntity.ok(result);
  }

  @ApiOperation(value = "List invoicing periods for application with given ID",
      authorizations = @Authorization(value ="api_key"),
      produces = "application/json",
      response = InvoicingPeriodJson.class,
      responseContainer="List"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Invoicing periods retrieved successfully", response = InvoicingPeriodJson.class, responseContainer="List")
  })
  @RequestMapping(value = "/applications/{id}/invoicingperiods", method = RequestMethod.GET, produces = "application/json")
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
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json",
      response = ChargeBasisEntryJson.class
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Entry added successfully", response = ChargeBasisEntryJson.class),
      @ApiResponse(code = 403, message = "Entry addition forbidden", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{applicationid}/chargebasisentries", method = RequestMethod.POST, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> addChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
      @RequestBody @Valid ChargeBasisEntryJson entry) {
    ChargeBasisEntry inserted = chargeBasisService.insertEntry(applicationId, ChargeBasisEntryMapper.mapToModel(entry));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(inserted));
  }

  @ApiOperation(value = "Update charge basis entry. Returns updated entry.",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json",
      response = ChargeBasisEntryJson.class
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Entry updated successfully", response = ChargeBasisEntryJson.class),
      @ApiResponse(code = 403, message = "Entry update forbidden", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{applicationid}/chargebasisentries/{id}", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> updateChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
      @PathVariable(value = "id") Integer id, @RequestBody @Valid ChargeBasisEntryJson entry) {
    ChargeBasisEntry updated = chargeBasisService.updateEntry(applicationId, id, ChargeBasisEntryMapper.mapToModel(entry));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(updated));
  }

  @ApiOperation(value = "Delete charge basis entry.",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Entry deleted successfully"),
      @ApiResponse(code = 403, message = "Entry deletion forbidden", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{applicationid}/chargebasisentries/{id}", method = RequestMethod.DELETE, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<Void> deleteChargeBasisEntry(@PathVariable(value = "applicationid") Integer applicationId,
      @PathVariable(value = "id") Integer id) {
    chargeBasisService.deleteEntry(applicationId, id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @ApiOperation(value = "Set charge basis entry invoicable / not invoicable.",
      authorizations = @Authorization(value ="api_key"),
      consumes = "application/json",
      produces = "application/json"
  )
  @ApiResponses( value = {
      @ApiResponse(code = 200, message = "Entry updated successfully"),
      @ApiResponse(code = 403, message = "Entry update forbidden", response = ErrorInfo.class)
  })
  @RequestMapping(value = "/applications/{applicationId}/chargebasisentries/{id}/invoicable", method = RequestMethod.PUT, produces = "application/json")
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<ChargeBasisEntryJson> setInvoicable(@PathVariable int applicationId, @PathVariable int id,
      @RequestParam("invoicable") boolean invoicable) {
    chargeBasisService.validateInvoicableChangeAllowed(applicationId, chargeBasisService.getEntry(applicationId, id));
    return ResponseEntity.ok(ChargeBasisEntryMapper.mapToJson(chargeBasisService.setInvoicable(applicationId, id, invoicable)));
  }
}
