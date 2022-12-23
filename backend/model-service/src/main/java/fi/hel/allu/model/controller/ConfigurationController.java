package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.dao.NotificationConfigurationDao;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.model.domain.NotificationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/configurations")
public class ConfigurationController {

  private final ConfigurationDao configurationDao;
  private final NotificationConfigurationDao notificationConfigurationDao;

  @Autowired
  public ConfigurationController(ConfigurationDao configurationDao,
      NotificationConfigurationDao notificationConfigurationDao) {
    this.configurationDao = configurationDao;
    this.notificationConfigurationDao = notificationConfigurationDao;
  }

  @GetMapping
  public ResponseEntity<List<Configuration>> getAll() {
    return ResponseEntity.ok(configurationDao.findAll());
  }

  @GetMapping(value = "/{key}")
  public ResponseEntity<List<Configuration>> find(@PathVariable ConfigurationKey key) {
    return ResponseEntity.ok(configurationDao.findByKey(key));
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<Configuration> update(@PathVariable int id, @RequestBody Configuration configuration) {
    return ResponseEntity.ok(configurationDao.update(id, configuration));
  }

  @GetMapping(value = "/notification")
  public ResponseEntity<List<NotificationConfiguration>> getNotificationConfiguration() {
    return ResponseEntity.ok(notificationConfigurationDao.findAll());
  }

}