package fi.hel.allu.model.service;


import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InvoicingDateServiceTest {

  Application summerApplication;
  Application winterApplication;
  List<Configuration> summerInvoiceDate = new ArrayList<>();
  List<Configuration> winterInvoiceDate= new ArrayList<>();
  ZoneId zoneId = ZoneId.of("Europe/Helsinki");

  @Mock
  ConfigurationDao configurationDao;
  InvoicingDateService invoicingDateService;

  static HashMap<ConfigurationKey,Configuration> configurations = new HashMap<>();

  @BeforeAll
  static void oneTime() {
    putOnConfigurations(ConfigurationKey.SUMMER_TERRACE_TIME_START, "1973-04-01");
    putOnConfigurations(ConfigurationKey.SUMMER_TERRACE_TIME_END, "1973-10-31");
    putOnConfigurations(ConfigurationKey.WINTER_TERRACE_TIME_START, "1973-11-01");
    putOnConfigurations(ConfigurationKey.WINTER_TERRACE_TIME_END, "1973-03-3");
    putOnConfigurations(ConfigurationKey.SUMMER_TERRACE_INVOICING_DATE,  "1973-07-15");
    putOnConfigurations(ConfigurationKey.WINTER_TERRACE_INVOICING_DATE, "1973-09-15");
    }

  @BeforeEach
  void setUp() {
    summerApplication = getApplication(ApplicationKind.SUMMER_TERRACE);
    winterApplication = getApplication(ApplicationKind.WINTER_TERRACE);
    summerInvoiceDate.add(configurations.get(ConfigurationKey.SUMMER_TERRACE_INVOICING_DATE));
    winterInvoiceDate.add(configurations.get(ConfigurationKey.WINTER_TERRACE_INVOICING_DATE));
  }

  @Test
  void getTerraceInvoicingDateOnSummer() {
    ZonedDateTime expected = ZonedDateTime.parse(
      "2020-07-15T00:00+03:00[Europe/Helsinki]");
    invoicingDateService = new InvoicingDateService(configurationDao);
    ZonedDateTime startTime = ZonedDateTime.parse(
      "2020-05-01T08:00:00+00:00[Europe/Helsinki]");
    summerApplication.setStartTime(startTime);
    ZonedDateTime endTime = ZonedDateTime.parse("2020-08-20T08:00:00+02:00[Europe/Helsinki]");
    summerApplication.setEndTime(endTime);
    when(configurationDao.findByKey(any())).thenReturn(summerInvoiceDate);
    ZonedDateTime result = invoicingDateService.getInvoicingDate(summerApplication);
    assertEquals(expected, result);
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void summerInvoiceDateIsEndDate() {
    LocalDateTime ldt = LocalDateTime.of(2020, 9, 24, 0, 0);
    ZonedDateTime expected = ZonedDateTime.of(ldt, zoneId);
    invoicingDateService = new InvoicingDateService(configurationDao);
    ZonedDateTime startTime = ZonedDateTime.parse("2020-08-24T08:00:00+02:00[Europe/Helsinki]");
    summerApplication.setStartTime(startTime);
    summerApplication.setEndTime(expected);
    when(configurationDao.findByKey(any())).thenReturn(summerInvoiceDate);
    ZonedDateTime result = invoicingDateService.getInvoicingDate(summerApplication);
    assertEquals(expected, result);
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void winterInvoiceDateNow() {
    LocalDateTime ldt = LocalDateTime.of(2021, 4, 21, 0, 0);
    ZonedDateTime expected = ZonedDateTime.of(ldt,zoneId);
    invoicingDateService = new InvoicingDateService(configurationDao);
    ZonedDateTime startTime = ZonedDateTime.parse("2020-11-02T08:00:00+02:00[Europe/Helsinki]");
    winterApplication.setStartTime(startTime);
    winterApplication.setEndTime(expected);
    when(configurationDao.findByKey(any())).thenReturn(winterInvoiceDate);
    ZonedDateTime result = invoicingDateService.getInvoicingDate(winterApplication);
    assertEquals(expected, result);
    verify(configurationDao, times(1)).findByKey(any());
  }

  Application getApplication(ApplicationKind kind){
    Application application = new Application();
    application.setExtension(new ShortTermRental());
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setKind(kind);
    return application;
  }

  static void putOnConfigurations(ConfigurationKey key, String value){
    Configuration configuration = new Configuration(ConfigurationType.CALENDAR_DATE, key, value);
    configurations.put(key, configuration);
  }

}
