package fi.hel.allu.model.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.EnumMap;

import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Configuration;
import fi.hel.allu.model.domain.ConfigurationKey;
import fi.hel.allu.model.domain.InvoicingPeriod;

@Service
public class InvoicingDateService {

  private final EnumMap<ApplicationKind, ConfigurationKey> TERRACE_DATE_CONFIG_BY_KIND
    = new EnumMap<>(ApplicationKind.class);

  private final ConfigurationDao configurationDao;

  public InvoicingDateService(ConfigurationDao configurationDao) {
    this.configurationDao = configurationDao;
    TERRACE_DATE_CONFIG_BY_KIND.put(ApplicationKind.SUMMER_TERRACE,
      ConfigurationKey.SUMMER_TERRACE_INVOICING_DATE);
    TERRACE_DATE_CONFIG_BY_KIND.put(ApplicationKind.WINTER_TERRACE,
      ConfigurationKey.WINTER_TERRACE_INVOICING_DATE);
    TERRACE_DATE_CONFIG_BY_KIND.put(ApplicationKind.PARKLET, ConfigurationKey.PARKLET_INVOICING_DATE);
  }

  public ZonedDateTime getInvoicingDate(Application application) {
    if (isTerrace(application)) {
      return getInvoicingDateForTerracePeriod(application.getStartTime(), application.getKind());
    } else {
      return application.getInvoicingDate();
    }
  }

  public ZonedDateTime getInvoicingDateForPeriod(Application application, InvoicingPeriod period) {
    if (isTerrace(application)) {
      return getInvoicingDateForTerracePeriod( period.getStartTime(), application.getKind());
    } else {
      return period.getEndTime();
    }
  }

  private ZonedDateTime getInvoicingDateForTerracePeriod(ZonedDateTime terracePeriodStartTime, ApplicationKind kind) {
    ZonedDateTime adjustedTerracePeriodStartTime = terracePeriodStartTime.withZoneSameInstant(TimeUtil.HelsinkiZoneId);
    ZonedDateTime invoicingDate = getTerraceInvoicingDateForYear(adjustedTerracePeriodStartTime.getYear(), kind);
    if (kind.equals(ApplicationKind.SUMMER_TERRACE) || kind.equals(ApplicationKind.PARKLET)) {
      return invoicingDate;
    }
    // else is winterTerrace
    ZonedDateTime winterInvoicingDayLimitInSummer = invoicingDate.withMonth(6).withDayOfMonth(15);
    if (invoicingDate.isAfter(winterInvoicingDayLimitInSummer)) {
      return adjustedTerracePeriodStartTime.getMonthValue() < 6 ? invoicingDate.minusYears(1) : invoicingDate;
    }
    return adjustedTerracePeriodStartTime.getMonthValue() > 6 ? invoicingDate.plusYears(1) : invoicingDate;
  }

  private ZonedDateTime getTerraceInvoicingDateForYear(int year, ApplicationKind kind) {
    return getDateFromConfiguration(TERRACE_DATE_CONFIG_BY_KIND.get(kind)).withYear(year);
  }

  private ZonedDateTime  getDateFromConfiguration(ConfigurationKey key) {
    return configurationDao.findByKey(key).stream()
        .findFirst()
        .map(Configuration::getValue)
        .map(LocalDate::parse)
        .map(TimeUtil::startOfDay)
        .orElseThrow(() -> new NoSuchEntityException("Required configuration " + key + " not found."));
  }

  private boolean isTerrace(Application application) {
    return application.getType() == ApplicationType.SHORT_TERM_RENTAL && application.getKind().isTerrace();
  }

}
