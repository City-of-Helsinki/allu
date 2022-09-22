package fi.hel.allu.search.controller;

import fi.hel.allu.search.domain.ApplicationWithContactsES;
import fi.hel.allu.search.domain.ContactES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.ContactSearchService;
import fi.hel.allu.search.util.Constants;
import fi.hel.allu.search.util.CustomersIndexUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for searching and indexing contacts.
 */
@RestController
@RequestMapping("/contacts")
public class ContactController {
  private final ContactSearchService contactSearchService;
  private final ApplicationSearchService applicationSearchService;

  @Autowired
  public ContactController(ContactSearchService contactSearchService,
      ApplicationSearchService applicationSearchService) {
    this.contactSearchService = contactSearchService;
    this.applicationSearchService = applicationSearchService;
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody List<ContactES> contactES) {
    contactSearchService.bulkInsert(contactES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/update")
  public ResponseEntity<Void> update(@RequestBody List<ContactES> contactESs) {
    contactSearchService.bulkUpdate(contactESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/applications")
  public ResponseEntity<Void> updateContactsOfApplications(@RequestBody List<ApplicationWithContactsES> applicationWithContacts) {
    Map<Integer, Object> contactsUpdateStructure =
        CustomersIndexUtil.getContactsUpdateStructure(applicationWithContacts).entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)); // rather silly way to cast Map<Integer, Map> to Map<Integer, Object>
    applicationSearchService.partialUpdate(contactsUpdateStructure, false);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    contactSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/index")
  public ResponseEntity<Void> deleteIndex() {
    contactSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/search")
  public ResponseEntity<Page<Integer>> search(@Valid @RequestBody QueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageRequest) {
    return new ResponseEntity<>(contactSearchService.findByField(queryParameters, pageRequest, false), HttpStatus.OK);
  }

  @PostMapping(value = "/sync/data")
  public ResponseEntity<Void> syncData(@Valid @RequestBody List<ContactES> contactESs) {
    contactSearchService.syncData(contactESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
