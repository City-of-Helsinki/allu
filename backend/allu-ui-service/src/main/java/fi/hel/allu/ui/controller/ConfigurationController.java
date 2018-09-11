package fi.hel.allu.ui.controller;

import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.servicecore.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Configuration>> getConfigurations() {
    return ResponseEntity.ok(configurationService.getAllConfigurations());
  }
}
