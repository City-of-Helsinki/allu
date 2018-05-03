package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.StructureMetaDao;
import fi.hel.allu.model.domain.meta.StructureMeta;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

/**
 * Rest interface for handling application metadata.
 */
@RestController
@RequestMapping("/meta")
public class MetaController {

  @Autowired
  private StructureMetaDao structureMetaDao;

  private static final String APPLICATION = "Application";
  private static final String EXTENSION = "/extension";

  @RequestMapping(value = "/{applicationType}", method = RequestMethod.GET)
  public ResponseEntity<StructureMeta> findByIdRecurse(@PathVariable String applicationType) {
    Optional<StructureMeta> structureMetaOpt = structureMetaDao.findCompleteByApplicationType(APPLICATION,
        Collections.singletonMap(EXTENSION, applicationType));
    StructureMeta structureMeta = structureMetaOpt
        .orElseThrow(() -> new NoSuchEntityException("Metadata not found for application type", applicationType));
    return new ResponseEntity<>(structureMeta, HttpStatus.OK);
  }

  @RequestMapping(value = "/{applicationType}/{version}", method = RequestMethod.GET)
  public ResponseEntity<StructureMeta> findByIdRecurse(@PathVariable String applicationType,
      @PathVariable int version) {
    Optional<StructureMeta> structureMetaOpt = structureMetaDao.findCompleteByApplicationType(APPLICATION, version,
        Collections.singletonMap(EXTENSION, applicationType));
    StructureMeta structureMeta = structureMetaOpt
        .orElseThrow(() -> new NoSuchEntityException("Metadata not found for application type", applicationType));
    return new ResponseEntity<>(structureMeta, HttpStatus.OK);
  }

  @RequestMapping(value = "/translation/{type}/{text}", method = RequestMethod.GET)
  public ResponseEntity<String> findTranslation(@PathVariable String type, @PathVariable String text) {
    final String translation = structureMetaDao.findTranslation(type, text);
    if (translation == null) {
      throw new NoSuchEntityException("Translation not found for text", text);
    }
    return new ResponseEntity<>(translation, HttpStatus.OK);
  }
}
