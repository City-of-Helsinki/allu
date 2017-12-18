package fi.hel.allu.model.controller;

import fi.hel.allu.model.domain.ApplicationWithContacts;
import fi.hel.allu.model.domain.Contact;
import fi.hel.allu.model.domain.ContactChange;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.CustomerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

  @Autowired
  private CustomerService customerService;
  @Autowired
  private ApplicationService applicationService;

  /**
   * Find contact item by id
   *
   * @param id The id of the contact item
   * @return The contents of requested contact item
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Contact> find(@PathVariable int id) {
    return new ResponseEntity<>(customerService.findContact(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Contact>> findContacts(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(customerService.findContacts(ids), HttpStatus.OK);
  }

  /**
   * Find all contacts, with paging support
   *
   * @param pageRequest page request for the search
   */
  @RequestMapping()
  public ResponseEntity<Page<Contact>> findAll(@PageableDefault(page = 0, size = 100) Pageable pageRequest) {
    return new ResponseEntity<>(customerService.findAllContacts(pageRequest), HttpStatus.OK);
  }

  /**
   * Get all contacts for a customer.
   *
   * @param customerId  The ID of the customer.
   * @return All contact items for the given customer.
   */
  @RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET)
  public ResponseEntity<List<Contact>> findByCustomer(@PathVariable int customerId) {
    return new ResponseEntity<>(customerService.findContactsByCustomer(customerId), HttpStatus.OK);
  }

  /**
   * Find all contacts of applications having given contact.
   *
   * @param ids  of the contacts whose related applications with contacts are fetched.
   * @return  all contacts of applications having given contact. It's worth noticing that the same application may appear more than once
   *          in the result list. This happens, if contact appears in application under several customer roles.
   */
  @RequestMapping(value = "/application/related", method = RequestMethod.POST)
  public ResponseEntity<List<ApplicationWithContacts>> findRelatedApplicationsWithContacts(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(applicationService.findRelatedApplicationsWithContacts(ids), HttpStatus.OK);
  }

  /**
   * Insert contact items.
   *
   * @param contactChange Contact change request.
   * @return The inserted contacts.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<List<Contact>> insert(@Valid @RequestBody(required = true) ContactChange contactChange) {
    return new ResponseEntity<>(customerService.insertContacts(contactChange.getContacts(), contactChange.getUserId()),
        HttpStatus.OK);
  }

  /**
   * Update a contact item
   *
   * @param contactChange Contact change request.
   * @return The contact item after insertion
   */
  @RequestMapping(method = RequestMethod.PUT)
  public ResponseEntity<List<Contact>> update(@Valid @RequestBody ContactChange contactChange) {
    return new ResponseEntity<>(customerService.updateContacts(contactChange.getContacts(), contactChange.getUserId()),
        HttpStatus.OK);
  }
}
