package fi.hel.allu.model.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.InvoicingPeriod;

public class ExcavationMonthlyPeriodCalculatorTest {

  private static final Integer APPLICATION_ID = 99;
  private static final AnnualTimePeriod WINTER_TIME =
      new AnnualTimePeriod(LocalDate.of(2026, 12, 1), LocalDate.of(2027, 5, 14));

  // --- Calendar month splitting ---

  @Test
  public void shouldSplitDateRangeAtCalendarMonthBoundaries() {
    List<InvoicingPeriod> periods = calculate("2026-11-11", "2027-06-06");

    assertEquals(8, periods.size());
    assertPeriod(periods.get(0), "2026-11-11", "2026-11-30");
    assertPeriod(periods.get(1), "2026-12-01", "2026-12-31");
    assertPeriod(periods.get(2), "2027-01-01", "2027-01-31");
    assertPeriod(periods.get(3), "2027-02-01", "2027-02-28");
    assertPeriod(periods.get(4), "2027-03-01", "2027-03-31");
    assertPeriod(periods.get(5), "2027-04-01", "2027-04-30");
    assertPeriod(periods.get(6), "2027-05-01", "2027-05-31");
    assertPeriod(periods.get(7), "2027-06-01", "2027-06-06");
  }

  @Test
  public void shouldSupportLeapYearFebruary() {
    List<InvoicingPeriod> periods = calculate("2024-01-15", "2024-03-02");

    assertEquals(3, periods.size());
    assertPeriod(periods.get(0), "2024-01-15", "2024-01-31");
    assertPeriod(periods.get(1), "2024-02-01", "2024-02-29");
    assertPeriod(periods.get(2), "2024-03-01", "2024-03-02");
  }

  @Test
  public void shouldCreateOnePeriodForDatesInSameMonth() {
    List<InvoicingPeriod> periods = calculate("2027-03-01", "2027-03-31");

    assertEquals(1, periods.size());
    assertPeriod(periods.get(0), "2027-03-01", "2027-03-31");
  }

  @Test
  public void shouldCreateOnePeriodForOneDayRange() {
    List<InvoicingPeriod> periods = calculate("2027-03-15", "2027-03-15");

    assertEquals(1, periods.size());
    assertPeriod(periods.get(0), "2027-03-15", "2027-03-15");
  }

  @Test
  public void shouldNormalizeInputToHelsinkiCalendarDate() {
    ZonedDateTime start = ZonedDateTime.parse("2026-02-28T23:30:00Z");
    ZonedDateTime end = ZonedDateTime.parse("2026-03-01T00:30:00Z");

    List<InvoicingPeriod> periods =
        ExcavationMonthlyPeriodCalculator.calculate(APPLICATION_ID, start, end, StatusType.FINISHED);

    assertEquals(1, periods.size());
    assertPeriod(periods.get(0), "2026-03-01", "2026-03-01");
  }

  @Test
  public void shouldSetApplicationIdAndStatusOnEachPeriod() {
    List<InvoicingPeriod> periods = ExcavationMonthlyPeriodCalculator.calculate(APPLICATION_ID,
        date("2027-03-01"), date("2027-05-01"), StatusType.OPERATIONAL_CONDITION);

    assertEquals(3, periods.size());
    periods.forEach(period -> {
      assertEquals(APPLICATION_ID, period.getApplicationId());
      assertEquals(StatusType.OPERATIONAL_CONDITION, period.getInvoicableStatus());
    });
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldRejectEndBeforeStart() {
    ExcavationMonthlyPeriodCalculator.calculate(APPLICATION_ID, date("2027-04-01"), date("2027-03-31"),
        StatusType.FINISHED);
  }

  @Test
  public void shouldReturnEmptyListWhenDateIsMissing() {
    assertTrue(ExcavationMonthlyPeriodCalculator.calculate(APPLICATION_ID, null, date("2027-03-31"),
        StatusType.FINISHED).isEmpty());
    assertTrue(ExcavationMonthlyPeriodCalculator.calculate(APPLICATION_ID, date("2027-03-01"), null,
        StatusType.FINISHED).isEmpty());
  }

  // --- Excavation announcement phases ---

  @Test
  public void shouldAssignWholeApplicationToWorkFinishedWithoutOperationalCondition() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-06-06", null);

    assertEquals(8, periods.size());
    periods.forEach(period -> assertEquals(StatusType.FINISHED, period.getInvoicableStatus()));
    assertPeriod(periods.get(0), "2026-11-11", "2026-11-30");
    assertPeriod(periods.get(7), "2027-06-01", "2027-06-06");
  }

  @Test
  public void shouldSplitWinterWorkIntoOperationalAndWorkFinishedPeriods() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-06-06", "2027-02-02");

    // Work finished phase starts right after the operational condition date and runs to the end,
    // leaving no gaps where area usage fee would not be charged at all
    assertStatuses(periods,
        StatusType.OPERATIONAL_CONDITION,
        StatusType.OPERATIONAL_CONDITION,
        StatusType.OPERATIONAL_CONDITION,
        StatusType.OPERATIONAL_CONDITION,
        StatusType.FINISHED,
        StatusType.FINISHED,
        StatusType.FINISHED,
        StatusType.FINISHED,
        StatusType.FINISHED);
    assertPeriod(periods.get(0), "2026-11-11", "2026-11-30");
    assertPeriod(periods.get(1), "2026-12-01", "2026-12-31");
    assertPeriod(periods.get(2), "2027-01-01", "2027-01-31");
    assertPeriod(periods.get(3), "2027-02-01", "2027-02-02");
    assertPeriod(periods.get(4), "2027-02-03", "2027-02-28");
    assertPeriod(periods.get(5), "2027-03-01", "2027-03-31");
    assertPeriod(periods.get(6), "2027-04-01", "2027-04-30");
    assertPeriod(periods.get(7), "2027-05-01", "2027-05-31");
    assertPeriod(periods.get(8), "2027-06-01", "2027-06-06");
    assertNotOverlapping(periods);
  }

  @Test
  public void shouldEndOperationalConditionBeforeWinterStartWhenOperationalConditionInSummer() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-03-01", "2027-06-06", "2026-10-15");

    // Operational condition charged until the day before winter time start
    assertPeriod(periods.get(0), "2026-03-01", "2026-03-31");
    assertEquals(9, periods.stream()
        .filter(period -> period.getInvoicableStatus() == StatusType.OPERATIONAL_CONDITION).count());
    assertPeriod(periods.get(8), "2026-11-01", "2026-11-30");
    // Work finished phase charged from the day after the operational condition date
    Optional<InvoicingPeriod> workFinishedInOctober = periods.stream()
        .filter(period -> toDate(period.getStartTime()).equals(LocalDate.parse("2026-12-01")))
        .findFirst();
    assertTrue("Periods: " + describe(periods), workFinishedInOctober.isPresent());
    assertEquals(StatusType.FINISHED, workFinishedInOctober.get().getInvoicableStatus());
    assertNotOverlapping(periods);
  }

  @Test
  public void shouldEndOperationalConditionOnWorkFinishedWhenWorkFinishedBeforeWinterStart() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-03-01", "2026-10-10", "2026-10-15", "2026-10-10");

    assertEquals(8, periods.size());
    periods.forEach(period -> assertEquals(StatusType.OPERATIONAL_CONDITION, period.getInvoicableStatus()));
    assertPeriod(periods.get(0), "2026-03-01", "2026-03-31");
    assertPeriod(periods.get(7), "2026-10-01", "2026-10-10");
  }

  @Test
  public void shouldCreateOnlyOperationalConditionPeriodsWhenApplicationEndsBeforeWorkFinishedPhase() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-02-02", "2027-02-02");

    assertEquals(4, periods.size());
    periods.forEach(period -> assertEquals(StatusType.OPERATIONAL_CONDITION, period.getInvoicableStatus()));
    assertPeriod(periods.get(3), "2027-02-01", "2027-02-02");
  }

  // --- Second handling fee part ---

  @Test
  public void shouldCreateDedicatedPeriodWhenSixMonthsDateFallsInGapBetweenPhases() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-15", "2027-12-31", "2027-02-02");

    // Six months from 15.11.2026 is 15.5.2027, which is covered by the May work finished period
    Optional<InvoicingPeriod> covered = ExcavationMonthlyPeriodCalculator
        .getAdditionalHandlingFeePeriod(date("2026-11-15"), date("2027-12-31"), periods);
    assertTrue(covered.isPresent());
    assertPeriod(covered.get(), "2027-05-01", "2027-05-31");
  }

  @Test
  public void shouldChargeAreaUsageFeeForWholePeriodWithoutGaps() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-06-06", "2027-02-02");

    // From the operational condition date to the end of the application every day belongs to a period
    for (int i = 1; i < periods.size(); i++) {
      assertEquals("Gap between periods",
          toDate(periods.get(i - 1).getEndTime()).plusDays(1), toDate(periods.get(i).getStartTime()));
    }
    assertEquals(toDate(date("2027-06-06")), toDate(periods.get(periods.size() - 1).getEndTime()));
  }

  @Test
  public void shouldUseExistingPeriodWhenSixMonthsDateIsInsidePhase() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-06-06", null);

    Optional<InvoicingPeriod> additionalPeriod = ExcavationMonthlyPeriodCalculator
        .getAdditionalHandlingFeePeriod(date("2026-11-11"), date("2027-06-06"), periods);
    assertTrue(additionalPeriod.isPresent());
    assertPeriod(additionalPeriod.get(), "2027-05-01", "2027-05-31");
    assertEquals(8, periods.size());
  }

  @Test
  public void shouldNotCreateDedicatedPeriodWhenWorkIsShorterThanSixMonths() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-04-30", null);

    assertTrue(ExcavationMonthlyPeriodCalculator
        .getAdditionalHandlingFeePeriod(date("2026-11-11"), date("2027-04-30"), periods).isEmpty());
    assertEquals(6, periods.size());
  }

  @Test
  public void shouldNotCreateDedicatedPeriodWhenSixMonthsDateIsOutsideApplication() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-05-10", null);

    assertTrue(ExcavationMonthlyPeriodCalculator
        .getAdditionalHandlingFeePeriod(date("2026-11-11"), date("2027-05-10"), periods).isEmpty());
  }

  // --- No area usage fee ---

  @Test
  public void shouldCreateOnlyHandlingFeePeriodsWhenNoAreaUsageFee() {
    List<InvoicingPeriod> periods = ExcavationMonthlyPeriodCalculator.calculateForExcavation(APPLICATION_ID,
        date("2026-11-11"), date("2027-06-06"), date("2027-02-02"), WINTER_TIME, true);

    // First period for the first handling fee part and the six month date period for the second one
    assertEquals("Periods: " + describe(periods), 2, periods.size());
    assertPeriod(periods.get(0), "2026-11-11", "2026-11-30");
    assertEquals(StatusType.OPERATIONAL_CONDITION, periods.get(0).getInvoicableStatus());
    // Six months from 11.11.2026 is 11.5.2027
    assertPeriod(periods.get(1), "2027-05-01", "2027-05-31");
    assertEquals(StatusType.FINISHED, periods.get(1).getInvoicableStatus());
  }

  @Test
  public void shouldCreateOnlyFirstPeriodWhenNoAreaUsageFeeAndWorkIsShorterThanSixMonths() {
    List<InvoicingPeriod> periods = ExcavationMonthlyPeriodCalculator.calculateForExcavation(APPLICATION_ID,
        date("2026-10-01"), date("2027-02-28"), null, WINTER_TIME, true);

    assertEquals(1, periods.size());
    assertPeriod(periods.get(0), "2026-10-01", "2026-10-31");
    assertEquals(StatusType.FINISHED, periods.get(0).getInvoicableStatus());
  }

  // --- Helpers ---

  @Test
  public void shouldReturnFirstPeriodByStartTime() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-06-06", "2027-02-02");

    InvoicingPeriod first = ExcavationMonthlyPeriodCalculator.getFirstPeriod(periods).orElseThrow();
    assertPeriod(first, "2026-11-11", "2026-11-30");
    assertTrue(ExcavationMonthlyPeriodCalculator.getFirstPeriod(List.of()).isEmpty());
  }

  @Test
  public void shouldKeepPeriodsContiguousForWinterWork() {
    List<InvoicingPeriod> periods = calculateForExcavation("2026-11-11", "2027-06-06", "2027-02-02");

    assertEquals(List.of("2026-11-11", "2026-12-01", "2027-01-01", "2027-02-01", "2027-02-03", "2027-03-01",
        "2027-04-01", "2027-05-01", "2027-06-01"), startDates(periods));
    assertNotOverlapping(periods);
  }

  private List<InvoicingPeriod> calculate(String start, String end) {
    return ExcavationMonthlyPeriodCalculator.calculate(APPLICATION_ID, date(start), date(end), StatusType.FINISHED);
  }

  private List<InvoicingPeriod> calculateForExcavation(String start, String end, String operationalCondition) {
    return calculateForExcavation(start, end, operationalCondition, null);
  }

  private List<InvoicingPeriod> calculateForExcavation(String start, String end, String operationalCondition,
      String workFinished) {
    return ExcavationMonthlyPeriodCalculator.calculateForExcavation(APPLICATION_ID, date(start),
        date(workFinished != null ? workFinished : end),
        operationalCondition == null ? null : date(operationalCondition), WINTER_TIME, false);
  }

  private ZonedDateTime date(String date) {
    return LocalDate.parse(date).atStartOfDay(TimeUtil.HelsinkiZoneId);
  }

  private InvoicingPeriod period(String start, String end, StatusType status) {
    InvoicingPeriod period = new InvoicingPeriod(APPLICATION_ID, date(start), date(end));
    period.setInvoicableStatus(status);
    return period;
  }

  private LocalDate toDate(ZonedDateTime date) {
    return date.withZoneSameInstant(TimeUtil.HelsinkiZoneId).toLocalDate();
  }

  private String describe(List<InvoicingPeriod> periods) {
    return periods.stream()
        .map(period -> toDate(period.getStartTime()) + ".." + toDate(period.getEndTime()) + "/"
            + period.getInvoicableStatus())
        .collect(Collectors.joining(", "));
  }

  private List<String> startDates(List<InvoicingPeriod> periods) {
    return periods.stream()
        .map(period -> period.getStartTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId).toLocalDate().toString())
        .collect(Collectors.toList());
  }

  private void assertPeriod(InvoicingPeriod period, String start, String end) {
    assertEquals(date(start), period.getStartTime());
    assertEquals(date(end), period.getEndTime());
  }

  private void assertStatuses(List<InvoicingPeriod> periods, StatusType... statuses) {
    assertEquals(List.of(statuses),
        periods.stream().map(InvoicingPeriod::getInvoicableStatus).collect(Collectors.toList()));
  }

  private void assertNotOverlapping(List<InvoicingPeriod> periods) {
    for (int i = 1; i < periods.size(); i++) {
      ZonedDateTime previousEnd = periods.get(i - 1).getEndTime();
      ZonedDateTime currentStart = periods.get(i).getStartTime();
      assertFalse("Periods overlap: " + previousEnd + " / " + currentStart, previousEnd.isAfter(currentStart));
    }
  }
}
