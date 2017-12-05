package fi.hel.allu.model.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.model.domain.Deposit;
import fi.hel.allu.model.service.DepositService;

@RestController
public class DepositController {

  private DepositService depositService;

  @Autowired
  public DepositController(DepositService depositService) {
    this.depositService = depositService;
  }

  @RequestMapping(value = "/deposit/{id}", method = RequestMethod.GET)
  public ResponseEntity<Deposit> findById(@PathVariable int id) {
    return new ResponseEntity<>(depositService.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/deposit", method = RequestMethod.POST)
  public ResponseEntity<Deposit> create(@Valid @RequestBody Deposit deposit) {
    return new ResponseEntity<>(depositService.create(deposit), HttpStatus.OK);
  }

  @RequestMapping(value = "/deposit/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Deposit> update(@PathVariable int id, @Valid @RequestBody Deposit deposit) {
    return new ResponseEntity<>(depositService.update(id, deposit), HttpStatus.OK);
  }

  @RequestMapping(value = "/deposit/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable int id) {
    depositService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/applications/{applicationId}/deposit", method = RequestMethod.GET)
  public ResponseEntity<Deposit> findByApplicationId(@PathVariable int applicationId) {
    return new ResponseEntity<>(depositService.findByApplicationId(applicationId), HttpStatus.OK);
  }

}
