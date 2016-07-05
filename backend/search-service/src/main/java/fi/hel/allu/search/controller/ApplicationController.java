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

@RestController
@RequestMapping("/applications")
public class ApplicationController {

  @Autowired
  private ApplicationSearchService applicationSearchService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity create(@RequestBody ApplicationES applicationES) {
    applicationSearchService.insertApplication(applicationES);
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity update(@PathVariable String id, @RequestBody(required = true) ApplicationES applicationES) {
    applicationSearchService.updateApplication(id, applicationES);
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
  public ResponseEntity delete(@PathVariable String id) {
    applicationSearchService.deleteApplication(id);
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/index", method = RequestMethod.DELETE)
  public ResponseEntity deleteIndex() {
    applicationSearchService.deleteIndex();
    return new ResponseEntity(HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<ApplicationES> findById(@PathVariable String id) {
    ApplicationES applicationES = applicationSearchService.findById(id);
    return new ResponseEntity(applicationES, HttpStatus.OK);
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