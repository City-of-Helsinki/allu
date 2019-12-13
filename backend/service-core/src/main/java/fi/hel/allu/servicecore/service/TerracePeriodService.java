package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.model.domain.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TerracePeriodService {

  private final ConfigurationService configurationService;

  @Autowired
  public TerracePeriodService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  public AnnualTimePeriod getSummerTerracePeriod() {
    return getSummerPeriod();
  }

  public AnnualTimePeriod getWinterTerracePeriod() {
    return new AnnualTimePeriod(
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.WINTER_TERRACE_TIME_START)),
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.WINTER_TERRACE_TIME_END)));
  }

  public AnnualTimePeriod getParkletPeriod() {
    return getSummerPeriod();
  }

  private AnnualTimePeriod getSummerPeriod() {
    return new AnnualTimePeriod(
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.SUMMER_TERRACE_TIME_START)),
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.SUMMER_TERRACE_TIME_END)));
  }
}
