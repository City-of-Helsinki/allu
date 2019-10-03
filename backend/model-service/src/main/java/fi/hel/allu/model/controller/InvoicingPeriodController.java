package fi.hel.allu.model.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.InvoicingPeriodService;

@RestController
@RequestMapping("/applications")
public class InvoicingPeriodController {

  @Autowired
  private InvoicingPeriodService invoicingPeriodService;

  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.POST)
  public ResponseEntity<List<InvoicingPeriod>> createInvoicingPeriods(@PathVariable Integer id, @RequestParam(value = "periodLength") int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.createInvoicingPeriods(id, periodLength));
  }

  @RequestMapping(value = "/{id}/recurring/invoicingperiods", method = RequestMethod.POST)
  public ResponseEntity<List<InvoicingPeriod>> createRecurringApplicationPeriods(@PathVariable Integer id) {
    return ResponseEntity.ok(invoicingPeriodService.createRecurringApplicationPeriods(id));
  }

  @RequestMapping(value = "/{id}/excavation/invoicingperiods", method = RequestMethod.PUT)
  public ResponseEntity<Void> setExcavationAnnouncementPeriods(@PathVariable Integer id) {
    invoicingPeriodService.setExcavationAnnouncementPeriods(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.PUT)
  public ResponseEntity<List<InvoicingPeriod>> updateInvoicingPeriods(@PathVariable Integer id, @RequestParam(value = "periodLength") int periodLength) {
    return ResponseEntity.ok(invoicingPeriodService.updateInvoicingPeriods(id, periodLength));
  }

  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.GET)
  public ResponseEntity<List<InvoicingPeriod>> getInvoicingPeriods(@PathVariable Integer id) {
    return ResponseEntity.ok(invoicingPeriodService.findForApplicationId(id));
  }
  @RequestMapping(value = "/{id}/invoicingperiods", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteInvoicingPeriods(@PathVariable Integer id) {
    invoicingPeriodService.deletePeriods(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }


}