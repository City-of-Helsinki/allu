package fi.hel.allu.model.controller;

import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.model.dao.CodeSetDao;
import fi.hel.allu.model.domain.CodeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/codesets")
public class CodeSetController {

  private final CodeSetDao codeSetDao;

  @Autowired
  public CodeSetController(CodeSetDao codeSetDao) {
    this.codeSetDao = codeSetDao;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET)
  public ResponseEntity<CodeSet> findById(@PathVariable Integer id) {
    final Optional<CodeSet> codeSet = codeSetDao.findById(id);
    return codeSet
        .map(cs -> new ResponseEntity<>(cs, HttpStatus.OK))
        .orElseThrow(() -> new NoSuchEntityException("CodeSet not found", id));
  }

  @RequestMapping(value = "/find", method = RequestMethod.POST)
  public ResponseEntity<List<CodeSet>> findByIds(@RequestBody List<Integer> ids) {
    final List<CodeSet> codeSet = codeSetDao.findByIds(ids);
    return new ResponseEntity<>(codeSet, HttpStatus.OK);
  }

  @RequestMapping(value = "/search", method = RequestMethod.GET)
  public ResponseEntity<List<CodeSet>> searchByType(@RequestParam("type") CodeSetType type) {
    final List<CodeSet> codeSets = codeSetDao.findByType(type);
    return new ResponseEntity<>(codeSets, HttpStatus.OK);
  }

  @RequestMapping(value = "/find", method = RequestMethod.GET)
  public ResponseEntity<CodeSet> findByTypeAndCode(
      @RequestParam("type") CodeSetType type,
      @RequestParam("code") String code) {

    final Optional<CodeSet> codeSet = codeSetDao.findByTypeAndCode(type, code);
    return codeSet
        .map(cs -> new ResponseEntity<>(cs, HttpStatus.OK))
        .orElseThrow(() -> new NoSuchEntityException("CodeSet not found", "type=" + type + ", code=" + code));
  }
}
