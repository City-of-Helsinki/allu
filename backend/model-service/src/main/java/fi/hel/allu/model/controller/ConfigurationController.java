package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/configurations")
public class ConfigurationController {

  private final ConfigurationDao configurationDao;

  @Autowired
  public ConfigurationController(ConfigurationDao configurationDao) {
    this.configurationDao = configurationDao;
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<Configuration>> getAll() {
    return ResponseEntity.ok(configurationDao.findAll());
  }

  @RequestMapping(value = "/{type}", method = RequestMethod.GET)
  public ResponseEntity<List<Configuration>> find(@PathVariable ConfigurationKey type) {
    return ResponseEntity.ok(configurationDao.findByKey(type));
  }
}
