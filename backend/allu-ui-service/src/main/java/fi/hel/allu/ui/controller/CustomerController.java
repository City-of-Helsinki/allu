package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.CustomerJson;
import fi.hel.allu.ui.domain.CustomerWithContactsJson;
import fi.hel.allu.ui.domain.ContactJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.service.CustomerService;
import fi.hel.allu.ui.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing customer information.
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

  @Autowired
  CustomerService customerService;
  @Autowired
  ContactService contactService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<CustomerJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(customerService.findCustomerById(id), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CustomerJson>> findAll() {
    return new ResponseEntity<>(customerService.findAllCustomers(), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/contacts", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ContactJson>> findByCustomer(@PathVariable int id) {
    return new ResponseEntity<>(contactService.findByCustomer(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<CustomerJson>> search(@Valid @RequestBody QueryParametersJson queryParameters) {
    return new ResponseEntity<>(customerService.search(queryParameters), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerJson> create(@Valid @RequestBody(required = true) CustomerJson customer) {
    return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerJson> update(@PathVariable int id, @Valid @RequestBody(required = true) CustomerJson customer) {
    return new ResponseEntity<>(customerService.updateCustomer(id, customer), HttpStatus.OK);
  }

  @RequestMapping(value = "/withcontacts", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerWithContactsJson> createWithContacts(@Valid @RequestBody CustomerWithContactsJson customerWithContactsJson) {
    return new ResponseEntity<>(customerService.createCustomerWithContacts(customerWithContactsJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/withcontacts", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<CustomerWithContactsJson> updateWithContacts(
      @PathVariable int id,
      @Valid @RequestBody CustomerWithContactsJson customerWithContactsJson) {
    return new ResponseEntity<>(customerService.updateCustomerWithContacts(id, customerWithContactsJson), HttpStatus.OK);
  }
}
