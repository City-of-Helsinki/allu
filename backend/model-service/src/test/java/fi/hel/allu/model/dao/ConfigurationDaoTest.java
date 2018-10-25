package fi.hel.allu.model.dao;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.model.domain.ConfigurationType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ConfigurationDaoTest {

  @Autowired
  ConfigurationDao configurationDao;

  @Test
  public void updateConfiguration() {
    List<Configuration> configs = configurationDao.findByKey(ConfigurationKey.WINTER_TIME_END);
    assertEquals(1, configs.size());
    Configuration config = configs.get(0);
    final String value = config.getValue() + "Updated";
    config.setValue(value);
    configurationDao.update(config.getId(), config);

    configs = configurationDao.findByKey(ConfigurationKey.WINTER_TIME_END);
    assertEquals(1, configs.size());
    config = configs.get(0);
    assertEquals(value, config.getValue());
  }

  @Test
  public void configurationKeyCantBeChanged() {
    List<Configuration> configs = configurationDao.findByKey(ConfigurationKey.WINTER_TIME_END);
    assertEquals(1, configs.size());
    Configuration config = configs.get(0);
    assertEquals(ConfigurationKey.WINTER_TIME_END, config.getKey());
    config.setKey(ConfigurationKey.WINTER_TIME_START);
    configurationDao.update(config.getId(), config);

    Optional<Configuration> configOpt = configurationDao.findById(config.getId());
    assertTrue(configOpt.isPresent());
    assertEquals(ConfigurationKey.WINTER_TIME_END, configOpt.get().getKey());
  }

  @Test(expected = IllegalArgumentException.class)
  public void readonlyConfigurationCantBeUpdated() {
    final Configuration config = new Configuration(
        ConfigurationType.TEXT,
        ConfigurationKey.PLACEMENT_CONTRACT_SECTION_NUMBER_YEAR,
        "2018");
    config.setReadonly(true);
    final Configuration savedConfig = configurationDao.insert(config);
    config.setValue("2020");
    configurationDao.update(savedConfig.getId(), config);

  }
}
