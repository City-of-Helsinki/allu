package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.model.domain.ConfigurationKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

@Service
public class TerracePeriodService {

  private final ConfigurationService configurationService;

  private AnnualTimePeriod summerTerracePeriod;
  private AnnualTimePeriod winterTerracePeriod;

  @Autowired
  public TerracePeriodService(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @PostConstruct
  public void init() {
    summerTerracePeriod = new AnnualTimePeriod(
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.SUMMER_TERRACE_TIME_START)),
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.SUMMER_TERRACE_TIME_END)));

    winterTerracePeriod = new AnnualTimePeriod(
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.WINTER_TERRACE_TIME_START)),
      LocalDate.parse(configurationService.getSingleValue(ConfigurationKey.WINTER_TERRACE_TIME_END)));
  }

  public AnnualTimePeriod getSummerTerracePeriod() {
    return summerTerracePeriod;
  }

  public AnnualTimePeriod getWinterTerracePeriod() {
    return winterTerracePeriod;
  }

  public AnnualTimePeriod getParkletPeriod() {
    return summerTerracePeriod;
  }
}
