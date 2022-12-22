package fi.hel.allu.ui.controller;

import fi.hel.allu.common.domain.types.CodeSetType;
import fi.hel.allu.model.domain.CodeSet;
import fi.hel.allu.servicecore.service.CodeSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/codesets")
public class CodeSetController {

  private final CodeSetService codeSetService;

  @Autowired
  public CodeSetController(CodeSetService codeSetService) {
    this.codeSetService = codeSetService;
  }

  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  @GetMapping(value = "/{type}")
  public ResponseEntity<List<CodeSet>> search(@PathVariable CodeSetType type) {
    List<CodeSet> codeSets = codeSetService.findByType(type);
    codeSets.sort(Comparator.comparing(CodeSet::getDescription));
    return new ResponseEntity<>(codeSets, HttpStatus.OK);
  }
}