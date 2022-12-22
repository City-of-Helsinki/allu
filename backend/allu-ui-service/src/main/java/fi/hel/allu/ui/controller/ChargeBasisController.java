package fi.hel.allu.ui.controller;

import fi.hel.allu.common.validator.ValidList;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/applications/{id}")
public class ChargeBasisController {

  @Autowired
  private ChargeBasisService chargeBasisService;

  /**
   * Get the charge basis entries for an application
   *
   * @param id the application ID
   * @return the charge basis entries for the application
   */
  @GetMapping(value = "/charge-basis")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ChargeBasisEntry>> getChargeBasis(@PathVariable int id) {
    return new ResponseEntity<>(chargeBasisService.getChargeBasis(id), HttpStatus.OK);
  }

  /**
   * Set the manual charge basis entries for an application
   *
   * @param id the application ID
   * @param chargeBasisEntries the charge basis entries to store. Only entries
   *          that are marked as manually set will be used
   * @return the new charge basis entries for the application
   */
  @PutMapping(value = "/charge-basis")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<ChargeBasisEntry>> setChargeBasis(@PathVariable int id,
                                                               @Valid @RequestBody ValidList<ChargeBasisEntry> chargeBasisEntries) {
    return new ResponseEntity<>(chargeBasisService.setChargeBasis(id, chargeBasisEntries), HttpStatus.OK);
  }

  /**
   * Set the charge basis entry invoicable / non-invoicable
   *
   * @param id the application ID
   * @param entryId id of the charge basis entry
   * @param invoicable whether entry should be invoicable
   *
   * @return updated charge basis entry
   */
  @PutMapping(value = "/charge-basis/{entryId}/invoicable")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ChargeBasisEntry> setInvoicable(@PathVariable int id, @PathVariable int entryId,
                                                        @RequestParam("invoicable") boolean invoicable) {
    return new ResponseEntity<>(chargeBasisService.setInvoicable(id, entryId, invoicable), HttpStatus.OK);
  }
}