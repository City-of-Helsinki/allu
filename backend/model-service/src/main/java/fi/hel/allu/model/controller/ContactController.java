package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.validator.ValidList;
import fi.hel.allu.model.dao.ContactDao;
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

  /**
   * Insert contact item
   *
   * @param contact
   *          The contents of the contact item
   * @return The inserted contact item
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Contact> insert(@Valid @RequestBody(required = true) Contact contact) {
    if (contact.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(contactDao.insert(contact), HttpStatus.OK);
  }

  /**
   * Update a contact item
   *
   * @param id
   *          The ID of the contact item to update
   * @param contact
   *          The new contents of the contact item
   * @return The contact item after insertion
   */
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Contact> update(@PathVariable int id, @Valid @RequestBody(required = true) Contact contact) {
    return new ResponseEntity<>(contactDao.update(id, contact), HttpStatus.OK);
  }

  /**
   * Get all contacts for an applicant
   *
   * @param applicantId
   *          The ID of the applicant
   * @return All contact items for the given applicant
   */
  @RequestMapping(value = "/applicant/{applicantId}", method = RequestMethod.GET)
  public ResponseEntity<List<Contact>> findByApplicant(@PathVariable int applicantId) {
    List<Contact> contacts = contactDao.findByApplicant(applicantId);
    return new ResponseEntity<>(contacts, HttpStatus.OK);
  }

  /**
   * Get the contact list for an application
   *
   * @param applicationId The application's ID
   * @return List of the application's contacts in preference order
   */
  @RequestMapping(value = "/application/{applicationId}", method = RequestMethod.GET)
  public ResponseEntity<List<Contact>> findByApplication(@PathVariable int applicationId) {
    List<Contact> contacts = contactDao.findByApplication(applicationId);
    return new ResponseEntity<>(contacts, HttpStatus.OK);
  }

  /**
   * Set application's contact list
   *
   * @param applicationId
   *          The application's ID
   * @param contacts
   *          List of contacts in preference order
   * @return The application's contact list after the insert/update
   */
  @RequestMapping(method = RequestMethod.PUT, params = "applicationId")
  public ResponseEntity<List<Contact>> setApplicationContacts(
      @RequestParam(value = "applicationId") final int applicationId, @Valid @RequestBody ValidList<Contact> contacts) {
    return new ResponseEntity<>(contactDao.setApplicationContacts(applicationId, contacts), HttpStatus.OK);
  }
}
