package fi.hel.allu.ui.controller;

import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.servicecore.service.InvoicingPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/applications")
public class InvoicingPeriodController {

  @Autowired
  private InvoicingPeriodService invoicingPeriodService;

  @PostMapping(value = "/{id}/invoicingperiods")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> createInvoicingPeriods(@PathVariable Integer id, @RequestParam int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.createInvoicingPeriods(id, periodLength));
  }

  @PutMapping(value = "/{id}/invoicingperiods")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> updateInvoicingPeriods(@PathVariable Integer id, @RequestParam int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.updateInvoicingPeriods(id, periodLength));
  }

  @GetMapping(value = "/{id}/invoicingperiods")
  @PreAuthorize("hasAnyRole('ROLE_VIEW', 'ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> getInvoicingPeriods(@PathVariable Integer id) {
    return ResponseEntity.ok(invoicingPeriodService.getInvoicingPeriods(id));
  }

  @DeleteMapping(value = "/{id}/invoicingperiods")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> deleteInvoicingPeriods(@PathVariable Integer id) {
    invoicingPeriodService.deleteInvoicingPeriods(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}