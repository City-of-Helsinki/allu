package fi.hel.allu.model.service;

import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.dao.LocationDao;
import fi.hel.allu.model.dao.UserDao;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

/**
 * Insert demo data on development and test environments.
 */
@Profile(value = {"DEV", "TEST"})
@Service
public class DemoDataInserter {
  private static final String SUPERVISOR_USERNAME = "alluvalv";
  private static final String DECISION_MAKER_USERNAME = "allute";
  private final List<ConfigurationKey> DECISION_MAKER_KEYS = Arrays.asList(
      ConfigurationKey.EXCAVATION_ANNOUNCEMENT_DECISION_MAKER,
      ConfigurationKey.AREA_RENTAL_DECISION_MAKER,
      ConfigurationKey.TEMPORARY_TRAFFIC_ARRANGEMENTS_DECISION_MAKER,
      ConfigurationKey.PLACEMENT_CONTRACT_DECISION_MAKER,
      ConfigurationKey.EVENT_DECISION_MAKER,
      ConfigurationKey.SHORT_TERM_RENTAL_DECISION_MAKER);

  private final LocationDao locationDao;
  private final UserDao userDao;
  private final ConfigurationDao configurationDao;

  public DemoDataInserter(LocationDao locationDao, UserDao userDao, ConfigurationDao configurationDao) {
    this.locationDao = locationDao;
    this.userDao = userDao;
    this.configurationDao = configurationDao;
  }

  @PostConstruct
  public void init() {
    addSupervisorsForCityDistricts();
    addDefaultDecisionMakers();
  }

  private void addSupervisorsForCityDistricts() {
    userDao.findByUserName(SUPERVISOR_USERNAME).ifPresent(u -> {
      u.setCityDistrictIds(
          locationDao.getCityDistrictList().stream().map(c -> c.getId()).collect(Collectors.toList()));
      userDao.update(u);
    });
  }

  private void addDefaultDecisionMakers() {
    userDao.findByUserName(DECISION_MAKER_USERNAME).ifPresent(u -> {
      DECISION_MAKER_KEYS.stream().forEach(k -> {
        final Configuration configuration = configurationDao.findByKey(k).get(0);
        configuration.setValue(u.getUserName());
        configurationDao.update(configuration.getId(), configuration);
      });
    });
  }
}
