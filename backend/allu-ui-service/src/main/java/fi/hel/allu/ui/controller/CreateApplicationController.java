package fi.hel.allu.ui.controller;


import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ApplicationListJson;
import fi.hel.allu.ui.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/applications")
public class CreateApplicationController {

  @Autowired
  private ApplicationService applicationService;

  @RequestMapping(method = RequestMethod.POST)
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationListJson> create(@Valid @RequestBody ApplicationListJson applicationListJson) {
    return new ResponseEntity<>(applicationService.createApplication(applicationListJson), HttpStatus.OK);
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  @PreAuthorize("hasAnyRole('ROLE_PROCESS_APPLICATION')")
  public ResponseEntity<ApplicationJson> update(@PathVariable int id, @Valid @RequestBody(required = true) ApplicationJson
      applicationJson) {
    return new ResponseEntity<>(applicationService.updateApplication(id, applicationJson), HttpStatus.OK);
  }

}
