package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.DepositJson;
import fi.hel.allu.servicecore.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class DepositController {

  private DepositService depositService;

  @Autowired
  public DepositController(DepositService depositService) {
    this.depositService = depositService;
  }

  @PostMapping(value = "/deposit")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DepositJson> create(@Valid @RequestBody DepositJson deposit) {
    return new ResponseEntity<>(depositService.create(deposit), HttpStatus.OK);
  }

  @PutMapping(value = "/deposit/{id}")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DepositJson> update(@PathVariable int id, @Valid @RequestBody DepositJson deposit) {
    return new ResponseEntity<>(depositService.update(id, deposit), HttpStatus.OK);
  }

  @DeleteMapping(value = "/deposit/{id}")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    depositService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{applicationId}/deposit")
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<DepositJson> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(depositService.findByApplicationId(applicationId), HttpStatus.OK);
  }

}