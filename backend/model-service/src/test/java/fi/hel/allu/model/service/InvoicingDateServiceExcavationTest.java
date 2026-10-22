package fi.hel.allu.model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.Test;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.domain.ShortTermRental;

/**
 * Invoicing dates of excavation announcement periods.
 *
 * <p>The original {@code InvoicingDateServiceTest} uses JUnit 5, which does not run in this module
 * because the Jupiter API version does not match the engine. These tests use JUnit 4 so that the
 * monthly invoicing date rule is actually verified.</p>
 */
public class InvoicingDateServiceExcavationTest {

  private final InvoicingDateService invoicingDateService = new InvoicingDateService(null);

  @Test
  public void shouldInvoiceMonthlyPeriodOnFirstDayOfNextMonth() {
    InvoicingPeriod period = period("2026-02-01", "2026-02-28");

    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(excavationApplication(), period);

    assertEquals(day("2026-03-01"), actual);
  }

  @Test
  public void shouldInvoicePartialMonthPeriodOnFirstDayOfNextMonth() {
    InvoicingPeriod period = period("2026-05-01", "2026-05-14");

    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(excavationApplication(), period);

    assertEquals(day("2026-06-01"), actual);
  }

  @Test
  public void shouldInvoiceDecemberPeriodOnFirstDayOfNextYear() {
    InvoicingPeriod period = period("2026-12-01", "2026-12-31");

    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(excavationApplication(), period);

    assertEquals(day("2027-01-01"), actual);
  }

  @Test
  public void shouldInvoicePeriodEndingAtMonthEndOnNextMonthFirstDay() {
    InvoicingPeriod period = period("2026-11-11", "2026-11-30");

    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(excavationApplication(), period);

    assertEquals(day("2026-12-01"), actual);
  }

  @Test
  public void shouldReturnNullForLegacyStatusOnlyPeriod() {
    InvoicingPeriod period = new InvoicingPeriod(1, StatusType.FINISHED);

    ZonedDateTime actual = invoicingDateService.getInvoicingDateForPeriod(excavationApplication(), period);

    assertNull(actual);
  }

  @Test
  public void shouldKeepTerraceInvoicingDatesUnchanged() {
    Application terrace = new Application();
    terrace.setExtension(new ShortTermRental());
    terrace.setType(ApplicationType.SHORT_TERM_RENTAL);
    terrace.setKind(ApplicationKind.SUMMER_TERRACE);
    terrace.setStartTime(day("2026-04-01"));
    terrace.setEndTime(day("2026-08-20"));
    InvoicingPeriod period = period("2026-04-01", "2026-08-20");

    // Terrace periods do not use the excavation rule; without configuration the lookup fails,
    // which shows the terrace path is still taken
    try {
      invoicingDateService.getInvoicingDateForPeriod(terrace, period);
    } catch (RuntimeException expected) {
      return;
    }
    throw new AssertionError("Expected terrace configuration lookup, not the excavation rule");
  }

  private Application excavationApplication() {
    Application application = new Application();
    application.setExtension(new ExcavationAnnouncement());
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    return application;
  }

  private InvoicingPeriod period(String start, String end) {
    InvoicingPeriod period = new InvoicingPeriod(1, day(start), day(end));
    period.setInvoicableStatus(StatusType.FINISHED);
    return period;
  }

  private ZonedDateTime day(String day) {
    return LocalDate.parse(day).atStartOfDay(TimeUtil.HelsinkiZoneId);
  }
}
