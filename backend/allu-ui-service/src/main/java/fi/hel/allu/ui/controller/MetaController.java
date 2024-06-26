package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.StructureMetaJson;
import fi.hel.allu.servicecore.service.MetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for providing metadata.
 */
@RestController
public class MetaController {

  @Autowired
  MetaService metaService;

  @GetMapping(value = "/meta/{type}")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StructureMetaJson> findByType(@PathVariable final String type) {
    return new ResponseEntity<>(metaService.findMetadataFor(type), HttpStatus.OK);
  }

  @GetMapping(value = "/applications/{applicationType}/meta")
  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  public ResponseEntity<StructureMetaJson> findByApplicationType(@PathVariable final ApplicationType applicationType) {
    return new ResponseEntity<>(metaService.findMetadataForApplication(applicationType), HttpStatus.OK);
  }
}