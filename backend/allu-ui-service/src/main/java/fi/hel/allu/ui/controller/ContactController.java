package fi.hel.allu.ui.controller;

import fi.hel.allu.ui.domain.ContactJson;
import fi.hel.allu.ui.domain.QueryParametersJson;
import fi.hel.allu.ui.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Controller for managing contact information.
 */
@RestController
@RequestMapping("/contacts")
public class ContactController {

  @Autowired
  ContactService contactService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ContactJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(contactService.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/applicant/{applicantId}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ContactJson>> findByApplicant(@PathVariable int applicantId) {
    return new ResponseEntity<>(contactService.findByApplicant(applicantId), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<List<ContactJson>> search(@Valid @RequestBody QueryParametersJson queryParameters) {
    return new ResponseEntity<>(contactService.search(queryParameters), HttpStatus.OK);
  }

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<ContactJson> create(@Valid @RequestBody(required = true) ContactJson contactJson) {
    return new ResponseEntity<>(contactService.createContact(contactJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION','ROLE_DECISION')")
  public ResponseEntity<ContactJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ContactJson contactJson) {
    return new ResponseEntity<>(contactService.updateContact(id, contactJson), HttpStatus.OK);
  }

  // TODO: delete/hide contact        DELETE /contacts/{id} ?
  // TODO: search incementally contacts by name       POST /customers/contacts/search
}
