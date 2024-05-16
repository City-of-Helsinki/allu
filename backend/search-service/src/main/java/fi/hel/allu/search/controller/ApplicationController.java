package fi.hel.allu.search.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.search.domain.CustomerWithContactsES;
import fi.hel.allu.search.service.*;
import fi.hel.allu.search.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.ApplicationQueryParameters;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  private final ApplicationSearchService applicationSearchService;
  private final CustomerSearchService customerSearchService;
  private final ContactSearchService contactSearchService;
  private final ProjectSearchService projectSearchService;
  private final SupervisionTaskSearchService supervisionTaskSearchService;

  @Autowired
  public ApplicationController(ApplicationSearchService applicationSearchService,
                               CustomerSearchService customerSearchService,
                               ContactSearchService contactSearchService,
                               ProjectSearchService projectSearchService,
                               SupervisionTaskSearchService supervisionTaskSearchService) {
    this.applicationSearchService = applicationSearchService;
    this.customerSearchService = customerSearchService;
    this.contactSearchService = contactSearchService;
    this.projectSearchService = projectSearchService;
    this.supervisionTaskSearchService = supervisionTaskSearchService;
  }

  @PostMapping
  public ResponseEntity<Void> create(@RequestBody ApplicationES applicationES) {
    applicationSearchService.insert(applicationES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping(value = "/update")
  public ResponseEntity<Void> update(@RequestBody List<ApplicationES> applicationESs, @RequestParam(required = false) Boolean waitRefresh) {
    applicationSearchService.bulkUpdate(applicationESs, waitRefresh);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Support for partially updating an application. For example, if you want to update application tags of an application 1, you should
   * use 1 as key of map and another map consisting of <code>applicationTags</code> as key and list of new application tag strings as value.
   *
   * @param idToPartialUpdateObj  Map having application id as key and partial update structure as value.
   * @return Nothing.
   */
  @PutMapping(value = "/partialupdate")
  public ResponseEntity<Void> partialUpdate(@RequestBody Map<Integer, Object> idToPartialUpdateObj, @RequestParam(required = false) Boolean waitRefresh) {
    applicationSearchService.partialUpdate(idToPartialUpdateObj, waitRefresh);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Update customers and related contacts for an application
   *
   * @param id application id
   * @param customersByRoleType list of customer structures to update for the application
   */
  @PutMapping(value = "/{id}/customersWithContacts")
  public ResponseEntity<Void> updateCustomersWithContacts(@PathVariable Integer id,
                                                          @RequestBody Map<CustomerRoleType, CustomerWithContactsES> customersByRoleType) {
    applicationSearchService.updateCustomersWithContacts(id, customersByRoleType);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    applicationSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @DeleteMapping(value = "/index")
  public ResponseEntity<Void> deleteIndex() {
    applicationSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/search")
  public ResponseEntity<Page<ApplicationES>> search(@Valid @RequestBody ApplicationQueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageRequest,
      @RequestParam(defaultValue = "false") Boolean matchAny) {
    return new ResponseEntity<>(applicationSearchService.findApplicationByField(queryParameters, pageRequest, matchAny), HttpStatus.OK);
  }

  @PostMapping(value = "/sync/start")
  public ResponseEntity<Void> startSync() {
    applicationSearchService.prepareSync();
    customerSearchService.prepareSync();
    contactSearchService.prepareSync();
    projectSearchService.prepareSync();
    supervisionTaskSearchService.prepareSync();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/sync/commit")
  public ResponseEntity<Void> commitSync() {
    applicationSearchService.endSync();
    customerSearchService.endSync();
    contactSearchService.endSync();
    projectSearchService.endSync();
    supervisionTaskSearchService.endSync();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/sync/cancel")
  public ResponseEntity<Void> cancelSync() {
    applicationSearchService.cancelSync();
    customerSearchService.cancelSync();
    contactSearchService.cancelSync();
    projectSearchService.cancelSync();
    supervisionTaskSearchService.cancelSync();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/sync/data")
  public ResponseEntity<Void> syncData(@Valid @RequestBody List<ApplicationES> applicationESs) {
    applicationSearchService.syncData(applicationESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}