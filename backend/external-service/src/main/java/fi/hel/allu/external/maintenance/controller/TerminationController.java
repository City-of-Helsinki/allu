package fi.hel.allu.external.maintenance.controller;

import fi.hel.allu.servicecore.service.TerminationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/applications/termination")
public class TerminationController {

  @Autowired
  TerminationService terminationService;

  @RequestMapping(value = "/terminate", method = RequestMethod.PATCH)
  @PreAuthorize("hasAnyRole('ROLE_SERVICE')")
  public ResponseEntity<Void> terminateApplicationsPendingForTermination() {
    terminationService.terminateApplicationsPendingForTermination();
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
