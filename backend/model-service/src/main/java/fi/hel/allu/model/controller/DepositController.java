package fi.hel.allu.model.controller;

import fi.hel.allu.model.domain.Deposit;
import fi.hel.allu.model.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class DepositController {

  private DepositService depositService;

  @Autowired
  public DepositController(DepositService depositService) {
    this.depositService = depositService;
  }

  @GetMapping(value = "/deposit/{id}")
  public ResponseEntity<Deposit> findById(@PathVariable int id) {
    return new ResponseEntity<>(depositService.findById(id), HttpStatus.OK);
  }

  @PostMapping(value = "/deposit")
  public ResponseEntity<Deposit> create(@Valid @RequestBody Deposit deposit) {
    return new ResponseEntity<>(depositService.create(deposit), HttpStatus.OK);
  }

  @PutMapping(value = "/deposit/{id}")
  public ResponseEntity<Deposit> update(@PathVariable int id, @Valid @RequestBody Deposit deposit) {
    return new ResponseEntity<>(depositService.update(id, deposit), HttpStatus.OK);
  }

  @DeleteMapping(value = "/deposit/{id}")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    depositService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{applicationId}/deposit")
  public ResponseEntity<Deposit> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(depositService.findByApplicationId(applicationId), HttpStatus.OK);
  }

}