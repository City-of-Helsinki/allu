package fi.hel.allu.ui.controller;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.servicecore.service.ChargeBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
  @RequestMapping(value = "/charge-basis", method = RequestMethod.GET)
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
  @RequestMapping(value = "/charge-basis", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<List<ChargeBasisEntry>> setChargeBasis(@PathVariable int id,
                                                               @Valid @RequestBody List<ChargeBasisEntry> chargeBasisEntries) {
    return new ResponseEntity<>(chargeBasisService.setChargeBasis(id, chargeBasisEntries), HttpStatus.OK);
  }
}
