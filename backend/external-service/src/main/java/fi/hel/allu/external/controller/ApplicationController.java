package fi.hel.allu.external.controller;

import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Public interface for managing applications.
 */
@RestController
@RequestMapping("/v1/applications")
public class ApplicationController {

  @Autowired
  ApplicationExtMapper applicationExtMapper;

  @Autowired
  ApplicationServiceComposer applicationServiceComposer;

  @RequestMapping(method = RequestMethod.POST)
//  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationExt> create(@Valid @RequestBody ApplicationExt application) {
    ApplicationJson applicationJson = applicationExtMapper.createApplicationJson(application);
    ApplicationJson createdApplicationJson = applicationServiceComposer.createApplication(applicationJson);
    return new ResponseEntity<ApplicationExt>(applicationExtMapper.mapApplicationExt(createdApplicationJson), HttpStatus.OK);
  }

//  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
////  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
//  public ResponseEntity<ApplicationJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ApplicationJson
//      applicationJson) {
//    return new ResponseEntity<>(applicationServiceComposer.updateApplication(id, applicationJson), HttpStatus.OK);
//  }
}
