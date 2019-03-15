package fi.hel.allu.supervision.api.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<ChargeBasisEntryJson>> findByApplication(@PathVariable Integer id) {
    List<ChargeBasisEntryJson> result = chargeBasisService.getChargeBasis(id)
        .stream()
        .map(e -> ChargeBasisEntryMapper.mapToJson(e))
        .collect(Collectors.toList());
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
  @PreAuthorize("hasAnyRole('ROLE_SUPERVISE')")
  public ResponseEntity<List<InvoicingPeriodJson>> findPeriodsByApplication(@PathVariable Integer id) {
    List<InvoicingPeriodJson> result = invoicingPeriodService.getInvoicingPeriods(id)
        .stream()
        .map(i -> new InvoicingPeriodJson(i.getId(), i.getStartTime(), i.getEndTime()))
        .collect(Collectors.toList());
    return ResponseEntity.ok(result);
  }
}
