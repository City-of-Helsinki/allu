package fi.hel.allu.ui.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.servicecore.domain.DepositJson;
import fi.hel.allu.servicecore.service.DepositService;

@RestController
public class DepositController {

  private DepositService depositService;

  @Autowired
  public DepositController(DepositService depositService) {
    this.depositService = depositService;
  }

  @RequestMapping(value = "/deposit", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DepositJson> create(@Valid @RequestBody DepositJson deposit) {
    return new ResponseEntity<>(depositService.create(deposit), HttpStatus.OK);
  }

  @RequestMapping(value = "/deposit/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DepositJson> update(@PathVariable int id, @Valid @RequestBody DepositJson deposit) {
    return new ResponseEntity<>(depositService.update(id, deposit), HttpStatus.OK);
  }

  @RequestMapping(value = "/deposit/{id}", method = RequestMethod.DELETE)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    depositService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/applications/{applicationId}/deposit", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DepositJson> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(depositService.findByApplicationId(applicationId), HttpStatus.OK);
  }

}
