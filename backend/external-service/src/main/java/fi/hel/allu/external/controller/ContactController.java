package fi.hel.allu.external.controller;

import fi.hel.allu.external.domain.ContactExt;
import fi.hel.allu.external.mapper.ContactExtMapper;
import fi.hel.allu.servicecore.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

/**
 * Public interface for managing customer information.
 */
@RestController
@RequestMapping("/v1/contacts")
public class ContactController {

  @Autowired
  ContactService contactService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ContactExt> findById(@PathVariable int id) {
    return new ResponseEntity<>(ContactExtMapper.mapContactExt(contactService.findById(id)), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ContactExt> create(@Valid @RequestBody ContactExt contact) {
    // TODO: check that contact is connected to a customer this user is allowed to alter (i.e. CustomerController.getConnectedCustomers)
    return new ResponseEntity<>(
        ContactExtMapper.mapContactExt(
            contactService.createContacts(Collections.singletonList(ContactExtMapper.mapContactJson(contact))).get(0)),
        HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ContactExt> update(@Valid @RequestBody ContactExt contactExt) {
    // TODO: ROLE_TRUSTED_PARTNER should be allowed to edit only the contacts of customers connected to the token i.e. what getConnectedCustomers() returns
    return new ResponseEntity<>(
        ContactExtMapper.mapContactExt(
            contactService.updateContacts(Collections.singletonList(ContactExtMapper.mapContactJson(contactExt))).get(0)),
        HttpStatus.OK);
  }
}
