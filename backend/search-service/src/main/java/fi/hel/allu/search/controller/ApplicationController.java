package fi.hel.allu.search.controller;

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  private ApplicationSearchService applicationSearchService;

  @Autowired
  public ApplicationController(ApplicationSearchService applicationSearchService) {
    this.applicationSearchService = applicationSearchService;
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
  public ResponseEntity<List<Integer>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(applicationSearchService.findByField(queryParameters), HttpStatus.OK);
  }
}
