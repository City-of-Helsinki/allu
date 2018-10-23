package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

  @RequestMapping(value = "/{key}", method = RequestMethod.GET)
  public ResponseEntity<List<Configuration>> find(@PathVariable ConfigurationKey key) {
    return ResponseEntity.ok(configurationDao.findByKey(key));
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
  public ResponseEntity<Configuration> update(@PathVariable int id, @RequestBody Configuration configuration) {
    return ResponseEntity.ok(configurationDao.update(id, configuration));
  }
}
