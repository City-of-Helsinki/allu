package fi.hel.allu.model.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.domain.InvoicingPeriod;

/**
 * Creates calendar-month invoicing periods for excavation announcement pricing.
 *
 * <p>Area usage fee periods are created separately for the operational condition phase
 * (toiminnallinen kunto) and the work finished phase (työ valmis), split at calendar-month
 * boundaries. The last period of each phase is left open and is invoiced only when the phase
 * decision is made.</p>
 *
 * <p>The second handling fee part is invoiced with the period that contains the date six months
 * after the application start. When that date is not covered by any area usage fee period, a
 * dedicated period is created for the uncovered part of that calendar month.</p>
 *
 * <p>All dates are handled as Helsinki calendar dates and period start and end times are the start
 * of the day. The calculator only creates domain objects; persistence and the choice between the
 * monthly and the legacy model belong to the calling service.</p>
 */
public final class ExcavationMonthlyPeriodCalculator {

  private ExcavationMonthlyPeriodCalculator() {
  }

  /**
   * Splits an inclusive date range at calendar-month boundaries.
   */
  public static List<InvoicingPeriod> calculate(Integer applicationId, ZonedDateTime start,
      ZonedDateTime end, StatusType invoicableStatus) {
    if (start == null || end == null) {
      return Collections.emptyList();
    }
    LocalDate startDate = toDate(start);
    LocalDate endDate = toDate(end);
    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("End date can't be before start date");
    }
    return calculateDates(applicationId, startDate, endDate, invoicableStatus);
  }

  /**
   * Creates monthly periods for an excavation announcement.
   *
   * @param applicationId       application id
   * @param start               application start time
   * @param end                 effective end time (work finished or application end time)
   * @param winterTimeOperation operational condition date, or {@code null}
   * @param winterTime          winter time settings of Allu
   * @param noAreaUsageFee      true when no area usage fee is charged for the application
   * @return periods ordered by start time
   */
  public static List<InvoicingPeriod> calculateForExcavation(Integer applicationId, ZonedDateTime start,
      ZonedDateTime end, ZonedDateTime winterTimeOperation, AnnualTimePeriod winterTime,
      boolean noAreaUsageFee) {
    return calculateForExcavation(applicationId, start, end, winterTimeOperation, null, winterTime,
        noAreaUsageFee);
  }

  /**
   * Creates monthly periods for an excavation announcement.
   *
   * <p>Charging never continues past the work finished date. When the operational condition date is
   * before winter time start, the operational condition phase is normally charged until the day
   * before winter time starts, but it is cut at the work finished date when the work was finished
   * earlier.</p>
   *
   * @param applicationId       application id
   * @param start               application start time
   * @param end                 application end time
   * @param winterTimeOperation operational condition date, or {@code null}
   * @param workFinished        work finished date, or {@code null} when the work is not finished
   * @param winterTime          winter time settings of Allu
   * @param noAreaUsageFee      true when no area usage fee is charged for the application
   * @return periods ordered by start time
   */
  public static List<InvoicingPeriod> calculateForExcavation(Integer applicationId, ZonedDateTime start,
      ZonedDateTime end, ZonedDateTime winterTimeOperation, ZonedDateTime workFinished,
      AnnualTimePeriod winterTime, boolean noAreaUsageFee) {
    if (start == null || end == null) {
      return Collections.emptyList();
    }
    LocalDate startDate = toDate(start);
    LocalDate applicationEndDate = toDate(end);
    // Charging never continues past the work finished date
    LocalDate chargeEndDate = getEffectiveEndDate(end, workFinished);

    List<InvoicingPeriod> areaUsageFeePeriods = new ArrayList<>();
    if (winterTimeOperation == null) {
      areaUsageFeePeriods.addAll(calculateDates(applicationId, startDate, chargeEndDate, StatusType.FINISHED));
    } else {
      if (winterTime == null) {
        throw new IllegalArgumentException("Winter time is required with operational condition date");
      }
      // Operational condition phase ends at the operational condition date, or at the day before
      // winter time start when the operational condition date is in summer. Charging also stops at
      // the work finished date when the work was finished earlier.
      LocalDate operationalConditionEnd = minDate(
          minDate(getOperationalConditionEndDate(winterTimeOperation, winterTime), applicationEndDate),
          getWorkFinishedLimit(workFinished));
      areaUsageFeePeriods.addAll(calculateDates(applicationId, startDate, operationalConditionEnd,
          StatusType.OPERATIONAL_CONDITION));

      // Work finished phase starts on the day after the operational condition phase ends so that
      // the rest of that month is charged as work finished time
      LocalDate workFinishedStart = maxDate(operationalConditionEnd.plusDays(1), startDate);
      areaUsageFeePeriods.addAll(calculateDates(applicationId, workFinishedStart, chargeEndDate, StatusType.FINISHED));
    }
    areaUsageFeePeriods.sort(Comparator.comparing(InvoicingPeriod::getStartTime));
    areaUsageFeePeriods = withoutOverlappingPeriods(areaUsageFeePeriods);

    List<InvoicingPeriod> result = new ArrayList<>(areaUsageFeePeriods);
    Optional<InvoicingPeriod> additionalFeePeriod =
        calculateAdditionalFeePeriod(applicationId, startDate, chargeEndDate, areaUsageFeePeriods);
    additionalFeePeriod.ifPresent(result::add);
    result.sort(Comparator.comparing(InvoicingPeriod::getStartTime));
    if (!noAreaUsageFee) {
      return result;
    }
    return handlingFeePeriods(applicationId, startDate, chargeEndDate, result);
  }

  /**
   * Returns the first day of the work finished phase.
   *
   * <p>Normally the work finished phase starts on the day after winter time ends. When the
   * operational condition date is before winter time start, the work finished phase starts on the
   * day after the operational condition date.</p>
   */
  public static ZonedDateTime getWorkFinishedStart(ZonedDateTime winterTimeOperation, AnnualTimePeriod winterTime) {
    if (winterTimeOperation == null) {
      return null;
    }
    if (!winterTime.isInAnnualPeriod(winterTimeOperation)) {
      return winterTimeOperation.plusDays(1);
    }
    return winterTime.getAnnualPeriodEnd(winterTimeOperation)
        .plusDays(1)
        .atStartOfDay(TimeUtil.HelsinkiZoneId);
  }

  /**
   * Returns the last day that may be charged.
   *
   * <p>Charging never continues past the work finished date, even when the operational condition
   * date is before winter time start and the phase would otherwise run until winter time starts.</p>
   */
  private static LocalDate getEffectiveEndDate(ZonedDateTime end, ZonedDateTime workFinished) {
    LocalDate endDate = toDate(end);
    if (workFinished == null) {
      return endDate;
    }
    return minDate(endDate, toDate(workFinished));
  }

  /**
   * Returns the last day the operational condition phase may cover because of the work finished
   * date, or {@link LocalDate#MAX} when the work is not finished. Charging never continues past the
   * work finished date.
   */
  private static LocalDate getWorkFinishedLimit(ZonedDateTime workFinished) {
    if (workFinished == null) {
      return LocalDate.MAX;
    }
    return toDate(workFinished);
  }

  /**
   * Returns the last day of the operational condition phase.
   *
   * <p>If the operational condition date is before winter time start, area usage fee is charged
   * until the day before winter time starts.</p>
   */
  public static ZonedDateTime getOperationalConditionEnd(ZonedDateTime start, ZonedDateTime end,
      ZonedDateTime winterTimeOperation, AnnualTimePeriod winterTime) {
    if (winterTimeOperation == null) {
      return null;
    }
    ZonedDateTime operationalConditionEnd =
        getOperationalConditionEndDate(winterTimeOperation, winterTime).atStartOfDay(TimeUtil.HelsinkiZoneId);
    return toDate(operationalConditionEnd).isBefore(toDate(start)) ? start : operationalConditionEnd;
  }

  /**
   * Returns the period containing the date six months after the application start, if the work
   * lasts at least six months and a period contains that date.
   */
  public static Optional<InvoicingPeriod> getAdditionalHandlingFeePeriod(ZonedDateTime start,
      ZonedDateTime end, List<InvoicingPeriod> periods) {
    if (start == null || end == null || periods.isEmpty()) {
      return Optional.empty();
    }
    LocalDate sixMonthsDate = toDate(start).plusMonths(6);
    if (sixMonthsDate.isAfter(toDate(end))) {
      return Optional.empty();
    }
    return periodContaining(periods, sixMonthsDate);
  }

  /**
   * Returns the first period of the given periods, ordered by start time.
   */
  public static Optional<InvoicingPeriod> getFirstPeriod(List<InvoicingPeriod> periods) {
    return periods.stream()
        .filter(period -> period.getStartTime() != null)
        .min(Comparator.comparing(InvoicingPeriod::getStartTime));
  }

  /**
   * Returns the period containing the given date.
   */
  public static Optional<InvoicingPeriod> periodContaining(List<InvoicingPeriod> periods, LocalDate date) {
    return periods.stream()
        .filter(period -> period.getStartTime() != null && period.getEndTime() != null)
        .filter(period -> !toDate(period.getStartTime()).isAfter(date)
            && !toDate(period.getEndTime()).isBefore(date))
        .findFirst();
  }

  /**
   * Creates a dedicated period for the second handling fee part when the date six months after the
   * application start is not covered by area usage fee periods.
   *
   * <p>The period covers the part of the calendar month containing the six month date that is not
   * covered by other periods. For example, with operational condition periods ending on 2.2.2027
   * and work finished periods starting on 15.5.2027, the six month date 11.5.2027 gets a period
   * from 1.5.2027 to 14.5.2027.</p>
   */
  private static Optional<InvoicingPeriod> calculateAdditionalFeePeriod(Integer applicationId, LocalDate start,
      LocalDate end, List<InvoicingPeriod> areaUsageFeePeriods) {
    LocalDate sixMonthsDate = start.plusMonths(6);
    if (sixMonthsDate.isAfter(end) || periodContaining(areaUsageFeePeriods, sixMonthsDate).isPresent()) {
      return Optional.empty();
    }

    LocalDate monthStart = YearMonth.from(sixMonthsDate).atDay(1);
    LocalDate periodStart = maxDate(monthStart, start);
    LocalDate periodEnd = minDate(YearMonth.from(sixMonthsDate).atEndOfMonth(), end);

    for (InvoicingPeriod period : areaUsageFeePeriods) {
      LocalDate otherStart = toDate(period.getStartTime());
      LocalDate otherEnd = toDate(period.getEndTime());
      if (otherEnd.isBefore(periodStart) || otherStart.isAfter(periodEnd)) {
        continue;
      }
      // Overlapping period: keep the part of the month after the other period
      if (!otherEnd.isBefore(periodStart)) {
        periodStart = maxDate(periodStart, otherEnd.plusDays(1));
      }
    }
    for (InvoicingPeriod period : areaUsageFeePeriods) {
      LocalDate otherStart = toDate(period.getStartTime());
      LocalDate otherEnd = toDate(period.getEndTime());
      if (otherEnd.isBefore(periodStart) || otherStart.isAfter(periodEnd)) {
        continue;
      }
      if (!otherStart.isAfter(periodEnd)) {
        periodEnd = minDate(periodEnd, otherStart.minusDays(1));
      }
    }

    if (periodEnd.isBefore(periodStart) || sixMonthsDate.isBefore(periodStart) || sixMonthsDate.isAfter(periodEnd)) {
      return Optional.empty();
    }
    return Optional.of(createPeriod(applicationId, periodStart, periodEnd,
        getStatusForPeriod(areaUsageFeePeriods, periodStart)));
  }

  private static List<InvoicingPeriod> handlingFeePeriods(Integer applicationId, LocalDate start,
      LocalDate end, List<InvoicingPeriod> periods) {
    List<InvoicingPeriod> result = new ArrayList<>();
    getFirstPeriod(periods).ifPresent(result::add);
    getAdditionalHandlingFeePeriod(start.atStartOfDay(TimeUtil.HelsinkiZoneId),
        end.atStartOfDay(TimeUtil.HelsinkiZoneId), periods)
        .filter(period -> !result.contains(period))
        .ifPresent(result::add);
    result.sort(Comparator.comparing(InvoicingPeriod::getStartTime));
    return result;
  }

  /**
   * Removes periods overlapping an earlier period, so that every date belongs to exactly one
   * period.
   */
  private static List<InvoicingPeriod> withoutOverlappingPeriods(List<InvoicingPeriod> periods) {
    List<InvoicingPeriod> result = new ArrayList<>();
    periods.forEach(period -> {
      boolean overlaps = result.stream().anyMatch(previous -> overlaps(previous, period));
      if (!overlaps) {
        result.add(period);
      }
    });
    return result;
  }

  private static boolean overlaps(InvoicingPeriod first, InvoicingPeriod second) {
    return !toDate(first.getEndTime()).isBefore(toDate(second.getStartTime()))
        && !toDate(second.getEndTime()).isBefore(toDate(first.getStartTime()));
  }

  private static StatusType getStatusForPeriod(List<InvoicingPeriod> periods, LocalDate date) {
    return periods.stream()
        .filter(period -> period.getStartTime() != null && !toDate(period.getStartTime()).isAfter(date))
        .max(Comparator.comparing(InvoicingPeriod::getStartTime))
        .map(InvoicingPeriod::getInvoicableStatus)
        .orElse(StatusType.FINISHED);
  }

  private static List<InvoicingPeriod> calculateDates(Integer applicationId, LocalDate start,
      LocalDate end, StatusType invoicableStatus) {
    if (start.isAfter(end)) {
      return Collections.emptyList();
    }
    List<InvoicingPeriod> periods = new ArrayList<>();
    LocalDate periodStart = start;
    while (!periodStart.isAfter(end)) {
      LocalDate monthEnd = YearMonth.from(periodStart).atEndOfMonth();
      LocalDate periodEnd = monthEnd.isBefore(end) ? monthEnd : end;
      periods.add(createPeriod(applicationId, periodStart, periodEnd, invoicableStatus));
      periodStart = periodEnd.plusDays(1);
    }
    return periods;
  }

  /**
   * Splits a phase date range at calendar-month boundaries, treating the last day of the range as
   * fully charged. The last period of the phase is left open for the phase decision.
   */
  private static InvoicingPeriod createPeriod(Integer applicationId, LocalDate start, LocalDate end,
      StatusType invoicableStatus) {
    InvoicingPeriod period = new InvoicingPeriod(applicationId,
        start.atStartOfDay(TimeUtil.HelsinkiZoneId),
        end.atStartOfDay(TimeUtil.HelsinkiZoneId));
    period.setInvoicableStatus(invoicableStatus);
    return period;
  }

  private static LocalDate getOperationalConditionEndDate(ZonedDateTime winterTimeOperation,
      AnnualTimePeriod winterTime) {
    if (winterTime.isInAnnualPeriod(winterTimeOperation)) {
      return toDate(winterTimeOperation);
    }
    return winterTime.getAnnualPeriodStart(winterTimeOperation).minusDays(1);
  }

  private static LocalDate getWorkFinishedStartDate(ZonedDateTime winterTimeOperation,
      AnnualTimePeriod winterTime) {
    if (!winterTime.isInAnnualPeriod(winterTimeOperation)) {
      return toDate(winterTimeOperation).plusDays(1);
    }
    return winterTime.getAnnualPeriodEnd(winterTimeOperation).plusDays(1);
  }

  private static LocalDate minDate(LocalDate first, LocalDate second) {
    return first.isBefore(second) ? first : second;
  }

  private static LocalDate maxDate(LocalDate first, LocalDate second) {
    return first.isAfter(second) ? first : second;
  }

  private static LocalDate toDate(ZonedDateTime date) {
    return date.withZoneSameInstant(TimeUtil.HelsinkiZoneId).toLocalDate();
  }
}
