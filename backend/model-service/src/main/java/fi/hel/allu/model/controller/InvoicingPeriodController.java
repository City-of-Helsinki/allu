package fi.hel.allu.model.controller;

import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.InvoicingPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class InvoicingPeriodController {

  @Autowired
  private InvoicingPeriodService invoicingPeriodService;

  @PostMapping(value = "/{id}/invoicingperiods")
  public ResponseEntity<List<InvoicingPeriod>> createInvoicingPeriods(@PathVariable Integer id, @RequestParam(value = "periodLength") int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.createInvoicingPeriods(id, periodLength));
  }

  @PostMapping(value = "/{id}/recurring/invoicingperiods")
  public ResponseEntity<List<InvoicingPeriod>> createRecurringApplicationPeriods(@PathVariable Integer id) {
    return ResponseEntity.ok(invoicingPeriodService.createRecurringApplicationPeriods(id));
  }

  @PutMapping(value = "/{id}/excavation/invoicingperiods")
  public ResponseEntity<Void> setExcavationAnnouncementPeriods(@PathVariable Integer id) {
    invoicingPeriodService.setExcavationAnnouncementPeriods(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/{id}/invoicingperiods")
  public ResponseEntity<List<InvoicingPeriod>> updateInvoicingPeriods(@PathVariable Integer id, @RequestParam(value = "periodLength") int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.updateInvoicingPeriods(id, periodLength));
  }

  @GetMapping(value = "/{id}/invoicingperiods")
  public ResponseEntity<List<InvoicingPeriod>> getInvoicingPeriods(@PathVariable Integer id) {
    return ResponseEntity.ok(invoicingPeriodService.findForApplicationId(id));
  }
  @DeleteMapping(value = "/{id}/invoicingperiods")
  public ResponseEntity<Void> deleteInvoicingPeriods(@PathVariable Integer id) {
    invoicingPeriodService.deletePeriods(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }


}