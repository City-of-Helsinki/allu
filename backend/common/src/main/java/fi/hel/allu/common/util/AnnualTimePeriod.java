package fi.hel.allu.common.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.google.common.collect.Range;

public class AnnualTimePeriod {

  private final LocalDate periodStart;
  private final LocalDate periodEnd;


  public AnnualTimePeriod(LocalDate periodStart, LocalDate periodEnd) {
    this.periodStart = periodStart;
    this.periodEnd = periodEnd;
  }

  /**
   * Gets annual period end time for given date. If date is not in period, returns end date of next annual period
   * @param date
   */
  public LocalDate getAnnualPeriodEnd(ZonedDateTime date) {
    int periodEndYear = getAnnualPeriodEndYear(date);
    return periodEnd.withYear(periodEndYear);
  }

  private int getAnnualPeriodEndYear(ZonedDateTime date) {
    int periodEndYear = date.getYear();
    if (date.getMonthValue() > periodEnd.getMonthValue()
        || (date.getMonthValue() == periodEnd.getMonthValue() && date.getDayOfMonth() > periodEnd.getDayOfMonth())) {
      periodEndYear++;
    }
    return periodEndYear;
  }

  /**
   * Gets annual period start for given date. If date is not in period, returns start date of next annual period
   * @return
   */
  public LocalDate getAnnualPeriodStart(ZonedDateTime date) {
    LocalDate periodEndDate = getAnnualPeriodEnd(date);
    return getAnnualPeriodStartForPeriodEnd(periodEndDate);
  }

  /**
   * Checks whether given date is in annual period.
   */
  public boolean isInAnnualPeriod(ZonedDateTime dateToCheck) {
    LocalDate periodEndDate = getAnnualPeriodEnd(dateToCheck);
    LocalDate periodStartDate = getAnnualPeriodStartForPeriodEnd(periodEndDate);
    return Range.closed(periodStartDate, periodEndDate).contains(LocalDate.from(dateToCheck));
  }

  private LocalDate getAnnualPeriodStartForPeriodEnd(LocalDate periodEndDate) {
    LocalDate periodStartDate;
    if (periodStart.getMonthValue() > periodEnd.getMonthValue()) {
      periodStartDate = periodStart.withYear(periodEndDate.getYear() - 1);
    } else {
      periodStartDate = periodStart.withYear(periodEndDate.getYear());
    }
    return periodStartDate;
  }
}
