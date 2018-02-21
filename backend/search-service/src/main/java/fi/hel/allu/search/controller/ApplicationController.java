package fi.hel.allu.search.controller;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationSearchService;
import fi.hel.allu.search.service.CustomerSearchService;

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

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  private final ApplicationSearchService applicationSearchService;
  private final CustomerSearchService customerSearchService;

  @Autowired
  public ApplicationController(ApplicationSearchService applicationSearchService,
                               CustomerSearchService customerSearchService) {
    this.applicationSearchService = applicationSearchService;
    this.customerSearchService = customerSearchService;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody ApplicationES applicationES) {
    applicationSearchService.insert(applicationES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/update", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@RequestBody List<ApplicationES> applicationESs) {
    applicationSearchService.bulkUpdate(applicationESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Support for partially updating an application. For example, if you want to update application tags of an application 1, you should
   * use 1 as key of map and another map consisting of <code>applicationTags</code> as key and list of new application tag strings as value.
   *
   * @param idToPartialUpdateObj  Map having application id as key and partial update structure as value.
   * @return Nothing.
   */
  @RequestMapping(value = "/partialupdate", method = RequestMethod.PUT)
  public ResponseEntity<Void> partialUpdate(@RequestBody Map<Integer, Object> idToPartialUpdateObj) {
    applicationSearchService.partialUpdate(idToPartialUpdateObj);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable String id) {
    applicationSearchService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteIndex() {
    applicationSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<Page<Integer>> search(@Valid @RequestBody QueryParameters queryParameters,
      @PageableDefault(page = Constants.DEFAULT_PAGE_NUMBER, size = Constants.DEFAULT_PAGE_SIZE) Pageable pageRequest) {
    return new ResponseEntity<>(applicationSearchService.findByField(queryParameters, pageRequest), HttpStatus.OK);
  }

  @RequestMapping(value = "/sync/start", method = RequestMethod.POST)
  public ResponseEntity<Void> startSync() {
    applicationSearchService.prepareSync();
    customerSearchService.prepareSync();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sync/commit", method = RequestMethod.POST)
  public ResponseEntity<Void> commitSync() {
    applicationSearchService.endSync();
    customerSearchService.endSync();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sync/cancel", method = RequestMethod.POST)
  public ResponseEntity<Void> cancelSync() {
    applicationSearchService.cancelSync();
    customerSearchService.cancelSync();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/sync/data", method = RequestMethod.POST)
  public ResponseEntity<Void> syncData(@Valid @RequestBody List<ApplicationES> applicationESs) {
    applicationSearchService.syncData(applicationESs);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
