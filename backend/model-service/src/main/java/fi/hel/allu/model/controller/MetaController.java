package fi.hel.allu.model.controller;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.StructureMetaDao;
import fi.hel.allu.model.domain.meta.StructureMeta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * Rest interface for handling application metadata.
 */
@RestController
@RequestMapping("/meta")
public class MetaController {

  private final StructureMetaDao structureMetaDao;

  public MetaController(StructureMetaDao structureMetaDao) {
    this.structureMetaDao = structureMetaDao;
  }

  @PostMapping(value = {"/{type}", "/{type}/{version}"})
  public ResponseEntity<StructureMeta> findByType(@PathVariable String type,
                                                  @PathVariable Optional<Integer> version,
                                                  @RequestBody Map<String, String> pathOverride) {
    Integer resolvedVersion = version.orElseGet(() -> structureMetaDao.getLatestMetadataVersion());
    return structureMetaDao.findCompleteByType(type, resolvedVersion, pathOverride)
        .map(meta -> new ResponseEntity<>(meta, HttpStatus.OK))
        .orElseThrow(() -> new NoSuchEntityException("Metadata not found for type", type));
  }

  @GetMapping(value = "/translation/{type}/{text}")
  public ResponseEntity<String> findTranslation(@PathVariable String type, @PathVariable String text) {
    final String translation = structureMetaDao.findTranslation(type, text);
    if (translation == null) {
      throw new NoSuchEntityException("Translation not found for text", text);
    }
    return new ResponseEntity<>(translation, HttpStatus.OK);
  }
}