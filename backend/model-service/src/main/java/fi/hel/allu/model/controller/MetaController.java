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

import java.util.Optional;

/**
 * Rest interface for handling application metadata.
 */
@RestController
@RequestMapping("/meta")
public class MetaController {

  @Autowired
  private StructureMetaDao structureMetaDao;

  @RequestMapping(value = "/{applicationType}", method = RequestMethod.GET)
  public ResponseEntity<StructureMeta> findById(@PathVariable String applicationType) {
    Optional<StructureMeta> structureMetaOpt = structureMetaDao.findByApplicationType(applicationType);
    StructureMeta structureMeta = structureMetaOpt
        .orElseThrow(() -> new NoSuchEntityException("Metadata not found for application type", applicationType));
    return new ResponseEntity<>(structureMeta, HttpStatus.OK);
  }

  @RequestMapping(value = "/{applicationType}/{version}", method = RequestMethod.GET)
  public ResponseEntity<StructureMeta> findById(
      @PathVariable String applicationType,
      @PathVariable int version) {
    Optional<StructureMeta> structureMetaOpt = structureMetaDao.findByApplicationType(applicationType, version);
    StructureMeta structureMeta = structureMetaOpt
        .orElseThrow(() -> new NoSuchEntityException("Metadata not found for application type", applicationType));
    return new ResponseEntity<>(structureMeta, HttpStatus.OK);
  }
}
