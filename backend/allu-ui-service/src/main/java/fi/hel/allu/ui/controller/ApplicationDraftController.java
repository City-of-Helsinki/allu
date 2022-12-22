package fi.hel.allu.ui.controller;

import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.service.ApplicationDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/drafts")
public class ApplicationDraftController {

  @Autowired
  private ApplicationDraftService applicationDraftService;

  @PostMapping
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> create(@RequestBody @Validated(ApplicationJson.Draft.class) ApplicationJson applicationDraftJson) {
    return new ResponseEntity<>(applicationDraftService.createDraft(applicationDraftJson), HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> findById(@PathVariable int id) {
    return new ResponseEntity<>(applicationDraftService.findById(id), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> update(@PathVariable int id, @RequestBody @Validated(ApplicationJson.Draft.class) ApplicationJson applicationDraftJson) {
    return new ResponseEntity<>(applicationDraftService.updateDraft(id, applicationDraftJson), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<Void> delete(@PathVariable int id) {
    applicationDraftService.deleteDraft(id);
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  @PutMapping(value = "{id}/application")
  @PreAuthorize("hasAnyRole('ROLE_CREATE_APPLICATION')")
  public ResponseEntity<ApplicationJson> convertToApplication(@PathVariable int id, @Valid @RequestBody(required = true) ApplicationJson applicationDraftJson) {
    return new ResponseEntity<>(applicationDraftService.convertToApplication(id, applicationDraftJson), HttpStatus.OK);
  }


}