package fi.hel.allu.model.controller;

import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

  private final ConfigurationDao configurationDao;

  @Autowired
  public ConfigurationController(ConfigurationDao configurationDao) {
    this.configurationDao = configurationDao;
  }

  @RequestMapping(value = "/{type}", method = RequestMethod.GET)
  public ResponseEntity<List<Configuration>> find(@PathVariable ConfigurationType type) {
    return new ResponseEntity<>(configurationDao.findByType(type), HttpStatus.OK);
  }
}
