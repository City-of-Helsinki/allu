package fi.hel.allu.ui.controller;

import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.servicecore.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/configurations")
public class ConfigurationController {

  private final ConfigurationService configurationService;

  @Autowired
  ConfigurationController(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @PreAuthorize("hasAnyRole('ROLE_VIEW')")
  @GetMapping
  public ResponseEntity<List<Configuration>> getConfigurations() {
    return ResponseEntity.ok(configurationService.getAllConfigurations());
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @PutMapping(value = "/{id}")
  public ResponseEntity<Configuration> updateConfiguration(@PathVariable int id, @RequestBody @Valid Configuration configuration) {
    return ResponseEntity.ok(configurationService.updateConfiguration(id, configuration));
  }

}