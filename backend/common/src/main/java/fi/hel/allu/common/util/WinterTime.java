package fi.hel.allu.common.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.google.common.collect.Range;

public class WinterTime {

  private final LocalDate winterTimeStart;
  private final LocalDate winterTimeEnd;


  public WinterTime(LocalDate winterTimeStart, LocalDate winterTimeEnd) {
    this.winterTimeStart = winterTimeStart;
    this.winterTimeEnd = winterTimeEnd;
  }

  /**
   * Gets winter time end for given date. If date is not in winter, returns end date of next winter
   * @param date
   */
  public LocalDate getWinterTimeEnd(ZonedDateTime dateInWinter) {
    int winterTimeEndYear = getWinterEndYear(dateInWinter);
    return winterTimeEnd.withYear(winterTimeEndYear);
  }

  private int getWinterEndYear(ZonedDateTime date) {
    int winterTimeEndYear = date.getYear();
    if (date.getMonthValue() > winterTimeEnd.getMonthValue()
        || (date.getMonthValue() == winterTimeEnd.getMonthValue() && date.getDayOfMonth() > winterTimeEnd.getDayOfMonth())) {
      winterTimeEndYear++;
    }
    return winterTimeEndYear;
  }

  /**
   * Gets winter time start for given date. If date is not in winter, returns start date of next winter
   * @return
   */
  public LocalDate getWinterTimeStart(ZonedDateTime date) {
    LocalDate winterEndDate = getWinterTimeEnd(date);
    return getWinterStartForWinterEnd(winterEndDate);
  }

  /**
   * Checks whether given date is in winter time.
   */
  public boolean isInWinterTime(ZonedDateTime dateToCheck) {
    LocalDate winterEndDate = getWinterTimeEnd(dateToCheck);
    LocalDate winterStartDate = getWinterStartForWinterEnd(winterEndDate);
    return Range.closed(winterStartDate, winterEndDate).contains(LocalDate.from(dateToCheck));
  }

  private LocalDate getWinterStartForWinterEnd(LocalDate winterEndDate) {
    LocalDate winterStartDate;
    if (winterTimeStart.getMonthValue() > winterTimeEnd.getMonthValue()) {
      winterStartDate = winterTimeStart.withYear(winterEndDate.getYear() - 1);
    } else {
      winterStartDate = winterTimeStart.withYear(winterEndDate.getYear());
    }
    return winterStartDate;
  }
}
