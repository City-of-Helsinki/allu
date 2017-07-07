package fi.hel.allu.external.controller;

import fi.hel.allu.external.domain.ApplicationExt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public interface for managing applications.
 */
@RestController
@RequestMapping("/applications")
public class ApplicationController {

  @RequestMapping(method = RequestMethod.POST)
//  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationExt> create(@RequestBody ApplicationExt application) {
    return new ResponseEntity<ApplicationExt>(new ApplicationExt(), HttpStatus.OK);
  }

//  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
////  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
//  public ResponseEntity<ApplicationJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ApplicationJson
//      applicationJson) {
//    return new ResponseEntity<>(applicationServiceComposer.updateApplication(id, applicationJson), HttpStatus.OK);
//  }
}
