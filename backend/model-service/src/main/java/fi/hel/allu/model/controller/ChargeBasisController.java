package fi.hel.allu.model.controller;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications/{id}")
public class ChargeBasisController {

  @Autowired
  ChargeBasisService chargeBasisService;

  @Autowired
  ApplicationService applicationService;

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
}
