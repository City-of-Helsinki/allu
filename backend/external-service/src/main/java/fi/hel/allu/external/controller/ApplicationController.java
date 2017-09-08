package fi.hel.allu.external.controller;

import fi.hel.allu.external.domain.ApplicationExt;
import fi.hel.allu.external.domain.ApplicationProgressReportExt;
import fi.hel.allu.external.mapper.ApplicationExtMapper;
import fi.hel.allu.external.service.ApplicationServiceExt;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

  @Autowired
  ApplicationServiceExt applicationServiceExt;

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ApplicationExt> create(@Valid @RequestBody ApplicationExt application) {
    ApplicationJson applicationJson = applicationExtMapper.createApplicationJson(application);
    // TODO: if method caller has only ROLE_TRUSTED_PARTNER, fetch user information and make sure that applications APPLICANT matches user info
    // TODO: if ROLE_TRUSTED_PARTNER and APPLICANT does not match user info, throw HTTP 403 forbidden (not allowed to create application on behalf other users)
    ApplicationJson createdApplicationJson = applicationServiceComposer.createApplication(applicationJson);
    return new ResponseEntity<>(applicationExtMapper.mapApplicationExt(createdApplicationJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<ApplicationExt> update(@PathVariable int id,
      @Valid @RequestBody(required = true) ApplicationExt applicationExt) {
    // TODO: ROLE_TRUSTED_PARTNER: check that both the stored and
    // updated application's applicants match user info
    ApplicationJson applicationJson = applicationServiceComposer.findApplicationById(id);
    // TODO: ROLE_TRUSTED_PARTNER: check that application's state allows editing
    applicationExtMapper.mergeApplicationJson(applicationJson, applicationExt);
    return new ResponseEntity<>(
        applicationExtMapper.mapApplicationExt(applicationServiceComposer.updateApplication(id, applicationJson)),
        HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}/progress", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_INTERNAL','ROLE_TRUSTED_PARTNER')")
  public ResponseEntity<Void> reportProgress(@PathVariable int id,
      @Valid @RequestBody ApplicationProgressReportExt progress) {
    // TODO: ROLE_TRUSTED_PARTNER can only set dates that are within
    // (5 days ago .. now).
    applicationServiceExt.reportProgress(id, progress);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
