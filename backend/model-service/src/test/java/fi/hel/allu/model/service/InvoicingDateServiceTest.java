package fi.hel.allu.model.service;


import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoicingDateServiceTest {

  Application summerApplication;
  Application winterApplication;
  List<Configuration> summerInvoiceDate = new ArrayList<>();
  List<Configuration> winterInvoiceDate= new ArrayList<>();
  List<Configuration> winterInvoiceDateOnSpring= new ArrayList<>();

  @Mock
  ConfigurationDao configurationDao;
  @InjectMocks
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
    winterInvoiceDateOnSpring.add(getConfiguration(ConfigurationKey.WINTER_TERRACE_INVOICING_DATE, "1973-03-15"));
  }

  @Test
  void summerInvoicingOnInvoicingDate() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(7).withDayOfMonth(15);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(4).withDayOfMonth(1);
    summerApplication.setStartTime(startTime);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(8).withDayOfMonth(20);
    summerApplication.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(summerInvoiceDate);
    ZonedDateTime actual = invoicingDateService.getInvoicingDate(summerApplication);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void summerInvoicingPeriodOnInvoicingDate() {
    ZonedDateTime expected = TimeUtil.startOfDay(ZonedDateTime.now().withMonth(7).withDayOfMonth(15));
    ZonedDateTime startTime = TimeUtil.startOfDay(ZonedDateTime.now().withMonth(4).withDayOfMonth(1)).withZoneSameInstant(ZoneId.of("UTC"));
    summerApplication.setStartTime(startTime);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(8).withDayOfMonth(20);
    summerApplication.setEndTime(endTime);
    InvoicingPeriod period = new InvoicingPeriod();
    period.setApplicationId(summerApplication.getId());
    period.setStartTime(startTime);
    period.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(summerInvoiceDate);
    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(summerApplication, period);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void summerInvoiceDateIsEndDate() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(7).withDayOfMonth(15);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(8).withDayOfMonth(24);

    summerApplication.setStartTime(startTime);
    summerApplication.setEndTime(expected);

    when(configurationDao.findByKey(any())).thenReturn(summerInvoiceDate);
    ZonedDateTime actual = invoicingDateService.getInvoicingDate(summerApplication);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void winterInvoiceDate() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(9).withDayOfMonth(15);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(11).withDayOfMonth(2);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(20).plusYears(1);

    winterApplication.setStartTime(startTime);
    winterApplication.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(winterInvoiceDate);
    ZonedDateTime actual = invoicingDateService.getInvoicingDate(winterApplication);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void winterInvoiceDateNextYear() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(9).withDayOfMonth(15);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(2).plusYears(1);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(20).plusYears(1);

    winterApplication.setStartTime(startTime);
    winterApplication.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(winterInvoiceDate);
    ZonedDateTime actual = invoicingDateService.getInvoicingDate(winterApplication);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void winterInvoicingPeriodInvoiceDateOnFall() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(9).withDayOfMonth(15);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(11).withDayOfMonth(2);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(20).plusYears(1);

    winterApplication.setStartTime(startTime);
    winterApplication.setEndTime(endTime);
    InvoicingPeriod period = new InvoicingPeriod();
    period.setApplicationId(summerApplication.getId());
    period.setStartTime(startTime);
    period.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(winterInvoiceDate);
    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(winterApplication, period);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void winterInvoicingPeriodInvoiceDateOnFallLastYear() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(9).withDayOfMonth(15).minusYears(1);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(2);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(20);

    winterApplication.setStartTime(startTime);
    winterApplication.setEndTime(endTime);
    InvoicingPeriod period = new InvoicingPeriod();
    period.setApplicationId(summerApplication.getId());
    period.setStartTime(startTime);
    period.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(winterInvoiceDate);
    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(winterApplication, period);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void winterInvoicingPeriodInvoiceDateOnSpring() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(3).withDayOfMonth(15);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(2);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(2).withDayOfMonth(20);

    winterApplication.setStartTime(startTime);
    winterApplication.setEndTime(endTime);
    InvoicingPeriod period = new InvoicingPeriod();
    period.setApplicationId(summerApplication.getId());
    period.setStartTime(startTime);
    period.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(winterInvoiceDateOnSpring);
    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(winterApplication, period);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
    verify(configurationDao, times(1)).findByKey(any());
  }

  @Test
  void winterInvoicingPeriodInvoiceDateOnSpringNextYear() {
    ZonedDateTime expected = ZonedDateTime.now().withMonth(3).withDayOfMonth(15).plusYears(1);
    ZonedDateTime startTime = ZonedDateTime.now().withMonth(12).withDayOfMonth(2);
    ZonedDateTime endTime = ZonedDateTime.now().withMonth(1).withDayOfMonth(20).plusYears(1);

    winterApplication.setStartTime(startTime);
    winterApplication.setEndTime(endTime);
    InvoicingPeriod period = new InvoicingPeriod();
    period.setApplicationId(summerApplication.getId());
    period.setStartTime(startTime);
    period.setEndTime(endTime);

    when(configurationDao.findByKey(any())).thenReturn(winterInvoiceDateOnSpring);
    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(winterApplication, period);
    assertEquals(expected.toLocalDate(), actual.toLocalDate());
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
  static Configuration getConfiguration(ConfigurationKey key, String value) {
    return new Configuration(ConfigurationType.CALENDAR_DATE, key, value);
  }
}
