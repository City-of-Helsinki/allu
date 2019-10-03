package fi.hel.allu.ui.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.servicecore.service.InvoicingPeriodService;


@RestController
@RequestMapping("/applications")
public class InvoicingPeriodController {

  @Autowired
  private InvoicingPeriodService invoicingPeriodService;

  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> createInvoicingPeriods(@PathVariable Integer id, @RequestParam int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.createInvoicingPeriods(id, periodLength));
  }

  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> updateInvoicingPeriods(@PathVariable Integer id, @RequestParam int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.updateInvoicingPeriods(id, periodLength));
  }

  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW', 'ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> getInvoicingPeriods(@PathVariable Integer id) {
    return ResponseEntity.ok(invoicingPeriodService.getInvoicingPeriods(id));
  }

  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<InvoicingPeriod>> deleteInvoicingPeriods(@PathVariable Integer id) {
    invoicingPeriodService.deleteInvoicingPeriods(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
