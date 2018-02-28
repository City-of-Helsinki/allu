package fi.hel.allu.model.pricing;

import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;

/**
 * Utilities for working with calendar
 */
public class CalendarUtil {
  // How many seconds in a day?
  private static final int DAY_SECONDS = 24 * 60 * 60;

  /**
   * Return the amount of days between begin and end, rounded to full days.
   */
  public static int daysBetween(Temporal begin, Temporal end) {
    if (begin == null || end == null) {
      return 0;
    }
    return Math.round(begin.until(end, ChronoUnit.SECONDS) / (float) DAY_SECONDS);
  }

  /**
   * Return the amount of time between given timestamps, expressed in "starting time units", for
   * example "starting calendar weeks"
   *
   * @param begin starting time
   * @param end   end time
   * @param unit  time unit used
   */
  public static long startingUnitsBetween(ZonedDateTime begin, ZonedDateTime end, ChronoUnit unit) {
    if (begin == null || end == null) {
      return 0;
    }
    return 1 + unitsBetween(begin, end, unit);
  }

  /**
   * Return the amount of time between given timestamps, expressed in given units
   *
   * @param begin starting time
   * @param end   end time
   * @param unit  time unit used
   */
  public static long unitsBetween(ZonedDateTime begin, ZonedDateTime end, ChronoUnit unit) {
    if (begin == null || end == null) {
      return 0;
    }
    if (begin.isAfter(end)) {
      throw new IllegalArgumentException("End time can't be before start!");
    }
    ZonedDateTime adjustedBegin = begin.truncatedTo(ChronoUnit.DAYS);
    ZonedDateTime adjustedEnd = end.truncatedTo(ChronoUnit.DAYS);
    switch (unit) {
      case DAYS:
        break;
      case WEEKS:
        adjustedBegin = adjustedBegin.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        adjustedEnd = adjustedEnd.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        break;
      case MONTHS:
        adjustedBegin = adjustedBegin.with(TemporalAdjusters.firstDayOfMonth());
        adjustedEnd = adjustedEnd.with(TemporalAdjusters.firstDayOfMonth());
        break;
      case YEARS:
        adjustedBegin = adjustedBegin.with(TemporalAdjusters.firstDayOfYear());
        adjustedEnd = adjustedEnd.with(TemporalAdjusters.firstDayOfYear());
        break;
      default:
        adjustedBegin = adjustedBegin.truncatedTo(unit);
        adjustedEnd = adjustedEnd.truncatedTo(unit);
    }
    return adjustedBegin.until(adjustedEnd, unit);
  }
}
