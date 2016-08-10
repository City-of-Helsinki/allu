package fi.hel.allu.search.controller;

import java.util.List;

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

import fi.hel.allu.search.domain.ApplicationES;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.search.service.ApplicationSearchService;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  @Autowired
  private ApplicationSearchService applicationSearchService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Void> create(@RequestBody ApplicationES applicationES) {
    applicationSearchService.insertApplication(applicationES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Void> update(@PathVariable String id,
      @RequestBody(required = true) ApplicationES applicationES) {
    applicationSearchService.updateApplication(id, applicationES);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<Void> delete(@PathVariable String id) {
    applicationSearchService.deleteApplication(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity<Void> deleteIndex() {
    applicationSearchService.deleteIndex();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<ApplicationES> findById(@PathVariable String id) {
    ApplicationES applicationES = applicationSearchService.findById(id);
    return new ResponseEntity<>(applicationES, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.POST)
  public ResponseEntity<List<ApplicationES>> search(@Valid @RequestBody QueryParameters queryParameters) {
    return new ResponseEntity<>(applicationSearchService.findByField(queryParameters), HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public ResponseEntity<List<ApplicationES>> searchAll(@RequestParam(value = "queryString") String queryString) {
    return new ResponseEntity<>(applicationSearchService.findFromAllFields(queryString), HttpStatus.OK);
  }

}