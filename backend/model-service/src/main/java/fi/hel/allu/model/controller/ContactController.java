package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.domain.ApplicationWithContacts;
import fi.hel.allu.model.domain.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contacts")
public class ContactController {

  @Autowired
  private ContactDao contactDao;
  @Autowired
  private ApplicationDao applicationDao;

  /**
   * Find contact item by id
   *
   * @param id
   *          The id of the contact item
   * @return The contents of requested contact item
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Contact> find(@PathVariable int id) {
    Optional<Contact> contact = contactDao.findById(id);
    Contact contactValue = contact
        .orElseThrow(() -> new NoSuchEntityException("Contact not found", Integer.toString(id)));
    return new ResponseEntity<>(contactValue, HttpStatus.OK);
  }

  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<Contact>> findContacts(@RequestBody List<Integer> ids) {
    return new ResponseEntity<>(contactDao.findByIds(ids), HttpStatus.OK);
  }


  /**
   * Get all contacts for a customer.
   *
   * @param customerId  The ID of the customer.
   * @return All contact items for the given customer.
   */
  @RequestMapping(value = "/customer/{customerId}", method = RequestMethod.GET)
  public ResponseEntity<List<Contact>> findByCustomer(@PathVariable int customerId) {
    List<Contact> contacts = contactDao.findByCustomer(customerId);
    return new ResponseEntity<>(contacts, HttpStatus.OK);
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
    List<ApplicationWithContacts> applicationsWithContacts = applicationDao.findRelatedApplicationsWithContacts(ids);
    return new ResponseEntity<>(applicationsWithContacts, HttpStatus.OK);
  }

  /**
   * Insert contact items.
   *
   * @param contacts  The contacts to be inserted.
   * @return The inserted contacts.
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<List<Contact>> insert(@Valid @RequestBody(required = true) List<Contact> contacts) {
    return new ResponseEntity<>(contactDao.insert(contacts), HttpStatus.OK);
  }

  /**
   * Update a contact item
   *
   * @param contacts  The new contents of the contact item
   * @return The contact item after insertion
   */
  @RequestMapping(method = RequestMethod.PUT)
  public ResponseEntity<List<Contact>> update(@Valid @RequestBody List<Contact> contacts) {
    return new ResponseEntity<>(contactDao.update(contacts), HttpStatus.OK);
  }
}
