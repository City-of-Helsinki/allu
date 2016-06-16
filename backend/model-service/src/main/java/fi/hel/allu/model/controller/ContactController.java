package fi.hel.allu.model.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.validator.ValidList;
import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.domain.Contact;

@RestController
@RequestMapping("/contacts")
public class ContactController {

  @Autowired
  private ContactDao contactDao;

  // Contact item handling: find, insert, update
  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Contact> find(@PathVariable int id) {
    Optional<Contact> contact = contactDao.findById(id);
    Contact contactValue = contact
        .orElseThrow(() -> new NoSuchEntityException("Contact not found", Integer.toString(id)));
    return new ResponseEntity<>(contactValue, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Contact> insert(@Valid @RequestBody(required = true) Contact contact) {
    if (contact.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(contactDao.insert(contact), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Contact> update(@PathVariable int id, @Valid @RequestBody(required = true) Contact contact) {
    return new ResponseEntity<>(contactDao.update(id, contact), HttpStatus.OK);
  }

  // Find all contacts for an organization:
  @RequestMapping(method = RequestMethod.GET, params = "organizationId")
  public ResponseEntity<List<Contact>> findByOrganization(
      @RequestParam(value = "organizationId") final int organizationId) {
    List<Contact> contacts = contactDao.findByOrganization(organizationId);
    return new ResponseEntity<>(contacts, HttpStatus.OK);
  }

  // get/set application's contact list:
  @RequestMapping(method = RequestMethod.GET, params = "applicationId")
  public ResponseEntity<List<Contact>> findByApplication(
      @RequestParam(value = "applicationId") final int applicationId) {
    List<Contact> contacts = contactDao.findByApplication(applicationId);
    return new ResponseEntity<>(contacts, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, params = "applicationId")
  public ResponseEntity<List<Contact>> setApplicationContacts(
      @RequestParam(value = "applicationId") final int applicationId, @Valid @RequestBody ValidList<Contact> contacts) {
    return new ResponseEntity<>(contactDao.setApplicationContacts(applicationId, contacts), HttpStatus.OK);
  }

  // get/set project's contact list:
  @RequestMapping(method = RequestMethod.GET, params = "projectId")
  public ResponseEntity<List<Contact>> findByProject(@RequestParam(value = "projectId") final int projectId) {
    List<Contact> contacts = contactDao.findByProject(projectId);
    return new ResponseEntity<>(contacts, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT, params = "projectId")
  public ResponseEntity<List<Contact>> setProjectContacts(@RequestParam(value = "projectId") final int projectId,
      @Valid @RequestBody ValidList<Contact> contacts) {
    return new ResponseEntity<>(contactDao.setProjectContacts(projectId, contacts), HttpStatus.OK);
  }
}
