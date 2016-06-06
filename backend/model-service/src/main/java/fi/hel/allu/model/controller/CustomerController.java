package fi.hel.allu.model.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.domain.Customer;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  @Autowired
  private CustomerDao customerDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Customer> find(@PathVariable int id) {
    Optional<Customer> customer = customerDao.findById(id);
    Customer customerValue = customer
        .orElseThrow(() -> new NoSuchEntityException("Customer not found", Integer.toString(id)));
    return new ResponseEntity<>(customerValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Customer> update(@PathVariable int id, @RequestBody(required = true) Customer customer) {
    return new ResponseEntity<>(customerDao.update(id, customer), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Customer> insert(@RequestBody(required = true) Customer customer) {
    if (customer.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(customerDao.insert(customer), HttpStatus.OK);
  }
}
