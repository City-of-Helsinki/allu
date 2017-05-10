package fi.hel.allu.common.util;

import java.time.ZonedDateTime;

/**
 * Recurring events are modeled as milliseconds in the year 1972 (it's a leap year). Every recurring period is first moved to year 1972
 * before storing to ElasticSearch. If period overlaps with two calendar years, the period is split into two 1972 periods: beginning
 * of given period is stored as x -> end of year 1972 and end of period is set to beginning of 1972 -> y.
 */
public class RecurringApplication {

  public static final ZonedDateTime MAX_END_TIME = ZonedDateTime.parse("9999-12-31T23:59:59+00:00");
  public static final ZonedDateTime BEGINNING_1972_DATE = ZonedDateTime.parse("1972-01-01T00:00:00+00:00");
  public static final ZonedDateTime END_1972_DATE = ZonedDateTime.parse("1973-01-01T00:00:00+00:00").minusNanos(1000);
  public static final long BEGINNING_1972 = BEGINNING_1972_DATE.toInstant().toEpochMilli();
  public static final long END_1972 = END_1972_DATE.toInstant().toEpochMilli();

  private long period1Start;
  private long period1End;
  private long period2Start;
  private long period2End;
  private long startTime;
  private long endTime;

  public RecurringApplication() {
    // for JSON serialization
  }

  /**
   * Constructor.
   * For example, if an application has recurring period of 1.12.2016 - 31.1.2017 and last time the period should recur is 1.12.2020, you
   * should give begin period as 1.12.2016, end period as 31.1.2017 and recurringEndTime 31.1.2021.
   *
   * @param startPeriod       Begin time of recurring period.
   * @param endPeriod         End time of recurring period.
   * @param recurringEndTime  The time of last recurrence.
   */
  public RecurringApplication(ZonedDateTime startPeriod, ZonedDateTime endPeriod, ZonedDateTime recurringEndTime) {
    startTime = startPeriod.toInstant().toEpochMilli();
    endTime = recurringEndTime.toInstant().toEpochMilli();

    long differenceTo1972 = startPeriod.getYear() - 1972;
    startPeriod = startPeriod.minusYears(differenceTo1972);
    endPeriod = endPeriod.minusYears(differenceTo1972);

    if (startPeriod.getYear() == endPeriod.getYear()) {
      // given period is within one calendar year
      period1Start = startPeriod.toInstant().toEpochMilli();
      period1End = endPeriod.toInstant().toEpochMilli();
    } else {
      // Given period must overlap at most two calendar years. If given duration is more than one year, it will be converted to one year.
      // Although setting recurring period longer than one year makes no sense, it still makes sense when such periods are searched.
      endPeriod = endPeriod.minusYears(endPeriod.getYear() - startPeriod.getYear());
      period1Start = startPeriod.toInstant().toEpochMilli();
      period1End = END_1972;
      period2Start = BEGINNING_1972;
      // this may actually go past 1972, but in that case, the search period is over year so every recurring period should match anyway
      period2End = endPeriod.toInstant().toEpochMilli();
    }
  }

  /**
   * @return Beginning of period1 in the year 1972.
   */
  public long getPeriod1Start() {
    return period1Start;
  }

  public void setPeriod1Start(long period1Start) {
    this.period1Start = period1Start;
  }

  /**
   * @return End of period 1 in the year 1972.
   */
  public long getPeriod1End() {
    return period1End;
  }

  public void setPeriod1End(long period1End) {
    this.period1End = period1End;
  }

  /**
   * @return Beginning of period2 in the year 1972 or 0, if period is not used. If not 0, start period2 is always before start period1.
   */
  public long getPeriod2Start() {
    return period2Start;
  }

  public void setPeriod2Start(long period2Start) {
    this.period2Start = period2Start;
  }

  /**
   * @return  End of period2. If total length of recurring period is longer than a year, returned value might be after end of 1972. Returns
   *          0 in case period2 is not used.
   */
  public long getPeriod2End() {
    return period2End;
  }

  public void setPeriod2End(long period2End) {
    this.period2End = period2End;
  }

  /**
   * @return Start time of the recurring period (real calendar time).
   */
  public long getStartTime() {
    return startTime;
  }

  public void setStartTime(long startTime) {
    this.startTime = startTime;
  }

  /**
   * @return End time of the recurring period (real calendar time).
   */
  public long getEndTime() {
    return endTime;
  }

  public void setEndTime(long endTime) {
    this.endTime = endTime;
  }
}
