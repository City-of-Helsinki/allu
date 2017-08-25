package fi.hel.allu.external.controller;

import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ApplicationExt> create(@Valid @RequestBody ApplicationExt application) {
    ApplicationJson applicationJson = applicationExtMapper.createApplicationJson(application);
    // TODO: if method caller has only ROLE_TRUSTED_PARTNER, fetch user information and make sure that applications APPLICANT matches user info
    // TODO: if ROLE_TRUSTED_PARTNER and APPLICANT does not match user info, throw HTTP 403 forbidden (not allowed to create application on behalf other users)
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
