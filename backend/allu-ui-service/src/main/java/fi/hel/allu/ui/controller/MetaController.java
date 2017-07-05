package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.ui.domain.StructureMetaJson;
import fi.hel.allu.ui.service.MetaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for providing metadata.
 */
@RestController
@RequestMapping("/meta")
public class MetaController {

  @Autowired
  MetaService metaService;

  @RequestMapping(value = "/{applicationType}", method = RequestMethod.GET)
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StructureMetaJson> findByApplicationType(@PathVariable final ApplicationType applicationType) {
    return new ResponseEntity<>(metaService.findMetadataForApplication(applicationType), HttpStatus.OK);
  }
}
