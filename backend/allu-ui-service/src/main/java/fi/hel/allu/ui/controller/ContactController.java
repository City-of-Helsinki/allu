package fi.hel.allu.ui.controller;

import javax.validation.Valid;

import fi.hel.allu.search.domain.QueryParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.servicecore.domain.ContactJson;
import fi.hel.allu.servicecore.service.ContactService;

/**
 * Controller for managing contact information.
 */
@RestController
public class ContactController {

  @Autowired
  ContactService contactService;

  @RequestMapping(value = "/contacts/{id}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ContactJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(contactService.findById(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/contacts/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ContactJson> updateContact(@PathVariable int id, @RequestBody @Valid ContactJson contact) {
    return new ResponseEntity<>(contactService.updateContact(id, contact), HttpStatus.OK);
  }

  @RequestMapping(value = "/customers/{customerid}/contacts", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<ContactJson> createContact(@PathVariable(value = "customerid") int customerId, @RequestBody @Valid ContactJson contact) {
    contact.setCustomerId(customerId);
    return new ResponseEntity<>(contactService.createContact(contact), HttpStatus.OK);
  }

  @RequestMapping(value = "/contacts/search", method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<Page<ContactJson>> search(@Valid @RequestBody QueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE, sort="name", direction= Sort.Direction.ASC) Pageable pageRequest) {
    return new ResponseEntity<>(contactService.search(queryParameters, pageRequest), HttpStatus.OK);
  }
}
