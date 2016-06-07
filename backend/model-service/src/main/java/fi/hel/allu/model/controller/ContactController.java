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
import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.domain.Contact;

@RestController
@RequestMapping("/contacts")
public class ContactController {

  @Autowired
  private ContactDao contactDao;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<Contact> find(@PathVariable int id) {
    Optional<Contact> contact = contactDao.findById(id);
    Contact contactValue = contact
        .orElseThrow(() -> new NoSuchEntityException("Contact not found", Integer.toString(id)));
    return new ResponseEntity<>(contactValue, HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Contact>> findByOrganization(
      @RequestParam(value = "organizationId") final int organizationId) {
    List<Contact> contacts = contactDao.findByOrganization(organizationId);
    return new ResponseEntity<>(contacts, HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Contact> update(@PathVariable int id, @Valid @RequestBody(required = true) Contact contact) {
    return new ResponseEntity<>(contactDao.update(id, contact), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Contact> insert(@Valid @RequestBody(required = true) Contact contact) {
    if (contact.getId() != null) {
      throw new IllegalArgumentException("Id must be null for insert");
    }
    return new ResponseEntity<>(contactDao.insert(contact), HttpStatus.OK);
  }
}
