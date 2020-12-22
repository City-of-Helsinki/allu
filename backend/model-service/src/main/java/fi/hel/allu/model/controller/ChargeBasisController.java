package fi.hel.allu.model.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;

@RestController
@RequestMapping("/applications/{id}")
public class ChargeBasisController {

  private final ChargeBasisService chargeBasisService;
  private final ApplicationService applicationService;

  @Autowired
  public ChargeBasisController(
      ChargeBasisService chargeBasisService,
      ApplicationService applicationService) {
    this.chargeBasisService = chargeBasisService;
    this.applicationService = applicationService;
  }

  /**
   * Get the charge basis entries for an application
   *
   * @param id the application ID
   * @return the charge basis entries for the application
   */
  @RequestMapping(value = "/charge-basis", method = RequestMethod.GET)
  public ResponseEntity<List<ChargeBasisEntry>> findByApplicationId(@PathVariable int id) {
    return new ResponseEntity<>(chargeBasisService.getChargeBasis(id), HttpStatus.OK);
  }

  /**
   * Get the charge basis entries for an application without using invoicing periods defined
   * for the application, i.e. everything on a single invoice.
   */
  @RequestMapping(value = "/single-invoice-charge-basis", method = RequestMethod.GET)
  public ResponseEntity<List<ChargeBasisEntry>> findSingleInvoiceByApplicationId(@PathVariable int id) {
   return ResponseEntity.ok(chargeBasisService.findSingleInvoiceByApplicationId(id));
  }

  @RequestMapping(value = "/location/{locationid}/invoicable/sum", method = RequestMethod.GET)
  public ResponseEntity<Integer> getInvoicableSumForLocation(@PathVariable(value = "id") int id, @PathVariable(value = "locationid") Integer locationId) {
    return ResponseEntity.ok(chargeBasisService.getInvoicableSumForLocation(id, locationId));
  }

  /**
   * Set the charge basis entries for an application
   *
   * @param id the application ID
   * @param chargeBasisEntries the charge basis entries for the application.
   *          Only the entries that are marked as manually set will be stored.
   * @return the charge basis (calculated and manual) entries for the application
   */
  @Transactional
  @RequestMapping(value = "/charge-basis", method = RequestMethod.PUT)
  public ResponseEntity<List<ChargeBasisEntry>> setManualChargeBasis(@PathVariable int id,
                                                                     @RequestBody List<ChargeBasisEntry> chargeBasisEntries) {
    chargeBasisService.setManualChargeBasis(id, chargeBasisEntries);
    applicationService.updateApplicationPricing(id);
    return new ResponseEntity<>(chargeBasisService.getChargeBasis(id), HttpStatus.OK);
  }

  @Transactional
  @RequestMapping(value = "/charge-basis/{entryId}/invoicable", method = RequestMethod.PUT)
  public ResponseEntity<ChargeBasisEntry> setInvoicable(@PathVariable int id, @PathVariable int entryId,
                                                        @RequestParam boolean invoicable) {
    ResponseEntity<ChargeBasisEntry> response = new ResponseEntity<>(chargeBasisService.setInvoicable(id, entryId, invoicable), HttpStatus.OK);
    applicationService.updateApplicationPricing(id);
    return response;
  }

  @RequestMapping(value = "/charge-basis", method = RequestMethod.POST)
  public ResponseEntity<ChargeBasisEntry> insertEntry(@PathVariable int id,
                                                      @RequestBody ChargeBasisEntry entry) {
    ChargeBasisEntry inserted = chargeBasisService.insert(id, entry);
    applicationService.updateApplicationPricing(id);
    return ResponseEntity.ok(inserted);
  }

  @RequestMapping(value = "/charge-basis/{entryId}", method = RequestMethod.GET)
  public ResponseEntity<ChargeBasisEntry> getEntry(@PathVariable int id, @PathVariable int entryId) {
    return ResponseEntity.ok(chargeBasisService.getEntry(id, entryId));
  }

  @RequestMapping(value = "/charge-basis/{entryId}", method = RequestMethod.PUT)
  public ResponseEntity<ChargeBasisEntry> updateEntry(@PathVariable int id, @PathVariable int entryId,
                                                        @RequestBody ChargeBasisEntry entry) {
    ChargeBasisEntry updated = chargeBasisService.updateEntry(id, entryId, entry);
    applicationService.updateApplicationPricing(id);
    return ResponseEntity.ok(updated);
  }

  @RequestMapping(value = "/charge-basis/{entryId}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteEntry(@PathVariable int id, @PathVariable int entryId) {
    chargeBasisService.deleteEntry(id, entryId);
    applicationService.updateApplicationPricing(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/charge-basis/recalculate", method = RequestMethod.PUT)
  public ResponseEntity<List<ChargeBasisEntry>> recalculateEntries(@PathVariable int id) {
    applicationService.updateChargeBasis(id);
    applicationService.updateApplicationPricing(id);
    return ResponseEntity.ok(chargeBasisService.getChargeBasis(id));
  }
}
