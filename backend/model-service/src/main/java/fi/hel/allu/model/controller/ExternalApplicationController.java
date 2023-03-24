package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.ExternalApplication;
import fi.hel.allu.model.service.ExternalApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExternalApplicationController {

  private final ExternalApplicationService externalApplicationService;

  @Autowired
  public ExternalApplicationController(ExternalApplicationService externalApplicationService) {
    this.externalApplicationService = externalApplicationService;
  }

  @PostMapping(value = "applications/{id}/originalapplication")
  public ResponseEntity<Void> save(@PathVariable Integer id,
      @RequestBody(required = true) ExternalApplication externalApplication) {
    externalApplicationService.save(id, externalApplication);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Finds original application with application ID.
   */
  @GetMapping(value = "applications/{id}/originalapplication")
  public ResponseEntity<ExternalApplication> findByApplicationId(@PathVariable Integer id) {
    return new ResponseEntity<>(externalApplicationService.findByApplicationId(id), HttpStatus.OK);
  }
}