package fi.hel.allu.external.maintenance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fi.hel.allu.servicecore.service.ApplicationArchiverService;

/**
 * Public interface for managing applications.
 */
@RestController
@RequestMapping("/v1/applications")
public class ApplicationController {

  @Autowired
  ApplicationArchiverService applicationArchiverService;

  @RequestMapping(value = "/finished/status", method = RequestMethod.PATCH)
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> updateStatusForFinishedApplications() {
    applicationArchiverService.updateStatusForFinishedApplications();
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @RequestMapping(value = "/finished/archive", method = RequestMethod.PATCH)
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> archiveFinishedApplications(@RequestBody List<Integer> applicationIds) {
    applicationArchiverService.archiveApplicationsIfNecessary(applicationIds);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
