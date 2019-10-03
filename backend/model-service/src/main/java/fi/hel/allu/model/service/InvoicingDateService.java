package fi.hel.allu.model.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

  private Map<ApplicationKind, ConfigurationKey> TERRACE_INVOICINGDATE_CONFIG_BY_KIND = new HashMap<ApplicationKind, ConfigurationKey>() {{
    put(ApplicationKind.SUMMER_TERRACE, ConfigurationKey.SUMMER_TERRACE_INVOICING_DATE);
    put(ApplicationKind.WINTER_TERRACE, ConfigurationKey.WINTER_TERRACE_INVOICING_DATE);
    put(ApplicationKind.PARKLET, ConfigurationKey.PARKLET_INVOICING_DATE);
  }};

  @Autowired
  private ConfigurationDao configurationDao;

  public ZonedDateTime getInvoicingDate(Application application) {
    if (isTerrace(application)) {
      return getInvoicingDateForTerracePeriod(application, application.getStartTime(), application.getEndTime(),
          application.getKind());
    } else {
      return application.getInvoicingDate();
    }
  }

  public ZonedDateTime getInvoicingDateForPeriod(Application application, InvoicingPeriod period) {
    if (isTerrace(application)) {
      return getInvoicingDateForTerracePeriod(application, period.getStartTime(), period.getEndTime(),
          application.getKind());
    } else {
      return period.getEndTime();
    }
  }

  private ZonedDateTime getInvoicingDateForTerracePeriod(Application application,
      ZonedDateTime terracePeriodStartTime, ZonedDateTime terracePeriodEndTime,
      ApplicationKind kind) {
    ZonedDateTime invoicingDate = getTerraceInvoicingDateForYear(terracePeriodStartTime.getYear(), kind);
    if (invoicingDate.isAfter(terracePeriodEndTime)) {
      invoicingDate = invoicingDate.minusYears(1);
    }
    if (ZonedDateTime.now().isAfter(invoicingDate)) {
      return TimeUtil.startOfDay(ZonedDateTime.now());
    }
    return invoicingDate;
  }

  private ZonedDateTime getTerraceInvoicingDateForYear(int year, ApplicationKind kind) {
    return getDateFromConfiguration(TERRACE_INVOICINGDATE_CONFIG_BY_KIND.get(kind)).withYear(year);
  }

  private ZonedDateTime getDateFromConfiguration(ConfigurationKey key) {
    return configurationDao.findByKey(key).stream()
        .findFirst()
        .map(Configuration::getValue)
        .map(value -> LocalDate.parse(value))
        .map(d -> TimeUtil.startOfDay(d))
        .orElseThrow(() -> new NoSuchEntityException("Required configuration " + key + " not found."));

  }

  private boolean isTerrace(Application application) {
    return application.getType() == ApplicationType.SHORT_TERM_RENTAL && application.getKind().isTerrace();
  }


}
