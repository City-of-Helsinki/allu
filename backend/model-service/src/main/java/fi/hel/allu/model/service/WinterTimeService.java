package fi.hel.allu.model.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.WinterTime;
import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;

@Service
public class WinterTimeService {

  private final ConfigurationDao configurationDao;

  @Autowired
  public WinterTimeService(ConfigurationDao configurationDao) {
    this.configurationDao = configurationDao;
  }

  public WinterTime getWinterTime() {
    return new WinterTime(
        LocalDate.parse(getConfigurationValue(ConfigurationKey.WINTER_TIME_START)),
        LocalDate.parse(getConfigurationValue(ConfigurationKey.WINTER_TIME_END)));
  }

  private String getConfigurationValue(ConfigurationKey key) {
    return configurationDao.findByKey(key).stream().findFirst().map(Configuration::getValue)
        .orElseThrow(() -> new NoSuchEntityException("Required configuration " + key + " not found."));
  }

  public boolean isInWinterTime(ZonedDateTime dateToCheck) {
    return getWinterTime().isInWinterTime(dateToCheck.withZoneSameInstant(TimeUtil.HelsinkiZoneId));
  }


}
