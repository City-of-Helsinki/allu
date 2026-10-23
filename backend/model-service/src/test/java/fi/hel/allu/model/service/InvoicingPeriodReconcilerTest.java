package fi.hel.allu.model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Test;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.InvoicingPeriod;

public class InvoicingPeriodReconcilerTest {

  private static final Integer APPLICATION_ID = 99;

  @Test
  public void shouldKeepMatchingPeriods() {
    InvoicingPeriod existing = period("2026-04-01", "2026-04-30", StatusType.FINISHED);
    existing.setId(1);

    PeriodReconciliation result = InvoicingPeriodReconciler.reconcile(
        List.of(existing), List.of(period("2026-04-01", "2026-04-30", StatusType.FINISHED)));

    assertFalse(result.hasChanges());
    assertTrue(result.getPeriodsToInsert().isEmpty());
    assertTrue(result.getPeriodsToDelete().isEmpty());
  }

  @Test
  public void shouldInsertPeriodWhenNoneExists() {
    PeriodReconciliation result = InvoicingPeriodReconciler.reconcile(
        List.of(), List.of(
            period("2026-04-01", "2026-04-30", StatusType.FINISHED),
            period("2026-05-01", "2026-05-31", StatusType.FINISHED)));

    assertEquals(2, result.getPeriodsToInsert().size());
    assertTrue(result.getPeriodsToDelete().isEmpty());
    assertTrue(result.hasChanges());
  }

  @Test
  public void shouldDeletePeriodWhenItIsNoLongerDesired() {
    InvoicingPeriod existing = period("2026-06-01", "2026-06-30", StatusType.FINISHED);
    existing.setId(5);

    PeriodReconciliation result = InvoicingPeriodReconciler.reconcile(List.of(existing), List.of());

    assertTrue(result.getPeriodsToInsert().isEmpty());
    assertEquals(1, result.getPeriodsToDelete().size());
    assertEquals(Integer.valueOf(5), result.getPeriodsToDelete().get(0).getId());
  }

  @Test
  public void shouldReplacePeriodWhenDatesDiffer() {
    InvoicingPeriod existing = period("2026-04-01", "2026-04-15", StatusType.FINISHED);
    existing.setId(1);

    PeriodReconciliation result = InvoicingPeriodReconciler.reconcile(
        List.of(existing), List.of(period("2026-04-01", "2026-04-30", StatusType.FINISHED)));

    assertEquals(1, result.getPeriodsToInsert().size());
    assertEquals(1, result.getPeriodsToDelete().size());
  }

  @Test
  public void shouldReplacePeriodWhenStatusDiffers() {
    InvoicingPeriod existing = period("2026-04-01", "2026-04-30", StatusType.OPERATIONAL_CONDITION);
    existing.setId(1);

    PeriodReconciliation result = InvoicingPeriodReconciler.reconcile(
        List.of(existing), List.of(period("2026-04-01", "2026-04-30", StatusType.FINISHED)));

    assertEquals(1, result.getPeriodsToInsert().size());
    assertEquals(1, result.getPeriodsToDelete().size());
  }

  @Test
  public void shouldOnlyReplaceChangedPeriodsAndKeepIdsOfUnchangedOnes() {
    InvoicingPeriod keep = period("2026-04-01", "2026-04-30", StatusType.FINISHED);
    keep.setId(1);
    InvoicingPeriod change = period("2026-05-01", "2026-05-15", StatusType.FINISHED);
    change.setId(2);

    PeriodReconciliation result = InvoicingPeriodReconciler.reconcile(
        List.of(keep, change),
        List.of(
            period("2026-04-01", "2026-04-30", StatusType.FINISHED),
            period("2026-05-01", "2026-05-31", StatusType.FINISHED)));

    assertEquals(1, result.getPeriodsToInsert().size());
    assertEquals(1, result.getPeriodsToDelete().size());
    // The period that matched keeps its id and is not in either list
    assertEquals(Integer.valueOf(2), result.getPeriodsToDelete().get(0).getId());
  }

  @Test
  public void shouldCompareDatesIgnoringTimeAndZone() {
    InvoicingPeriod existing = new InvoicingPeriod(APPLICATION_ID,
        ZonedDateTime.parse("2026-03-31T22:00:00Z"),   // 2026-04-01 in Helsinki
        ZonedDateTime.parse("2026-04-30T12:00:00+03:00"));
    existing.setInvoicableStatus(StatusType.FINISHED);

    PeriodReconciliation result = InvoicingPeriodReconciler.reconcile(
        List.of(existing), List.of(period("2026-04-01", "2026-04-30", StatusType.FINISHED)));

    assertFalse(result.hasChanges());
  }

  private InvoicingPeriod period(String start, String end, StatusType status) {
    InvoicingPeriod period = new InvoicingPeriod(APPLICATION_ID,
        LocalDate.parse(start).atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse(end).atStartOfDay(TimeUtil.HelsinkiZoneId));
    period.setInvoicableStatus(status);
    return period;
  }
}
