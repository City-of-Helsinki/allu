package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ContactDao;
import fi.hel.allu.model.domain.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    @Autowired
    private ContactDao contactDao;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Contact> find(@PathVariable int id) {
        Optional<Contact> contact = contactDao.findById(id);
        if (contact.isPresent()) {
            return new ResponseEntity<>(contact.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Contact>> findByOrganization(@RequestParam(value = "organizationId") final int organizationId) {
        List<Contact> contacts = contactDao.findByOrganization(organizationId);
        if (contacts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(contacts, HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Contact> update(@PathVariable int id, @RequestBody(required = true) Contact contact) {
        return new ResponseEntity<>(contactDao.update(id, contact), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Contact> insert(@RequestBody(required = true) Contact contact) {
        if (contact.getId() != null) {
            throw new IllegalArgumentException("Id must be null for insert");
        }
        return new ResponseEntity<>(contactDao.insert(contact), HttpStatus.OK);
    }
}
