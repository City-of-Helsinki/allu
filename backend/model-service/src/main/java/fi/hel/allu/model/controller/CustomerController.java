package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.types.CustomerRoleType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.CustomerDao;
import fi.hel.allu.model.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

  @Autowired
  private CustomerDao customerDao;
  @Autowired
  private ApplicationDao applicationDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Customer> findCustomer(@PathVariable int id) {
    Optional<Customer> customer = customerDao.findById(id);
    Customer customerValue = customer
        .orElseThrow(() -> new NoSuchEntityException("Customer not found", Integer.toString(id)));
    return new ResponseEntity<>(customerValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Customer>> findCustomers(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(customerDao.findByIds(ids), HttpStatus.OK);
  }

  /**
   * Returns application ids of the applications having given customer.
   *
   * @param id    id of the customer whose related applications are returned.
   * @return  List of application ids. Never <code>null</code>.
   */
  @RequestMapping(value = "/applications/{id}", method = RequestMethod.GET)
  public ResponseEntity<Map<Integer, List<CustomerRoleType>>> findApplicationsByCustomer(@PathVariable int id) {
    return new ResponseEntity<>(applicationDao.findByCustomer(id), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Customer>> findAllCustomers() {
    return new ResponseEntity<>(customerDao.findAll(), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Customer> updateCustomer(@PathVariable int id,
                                                 @Valid @RequestBody(required = true) Customer customer) {
    Customer resultCustomer = customerDao.update(id, customer);
    return new ResponseEntity<>(resultCustomer, resultCustomer != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Customer> addCustomer(@Valid @RequestBody(required = true) Customer customer) {
    return new ResponseEntity<>(customerDao.insert(customer), HttpStatus.OK);
  }
}
