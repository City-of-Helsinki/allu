package fi.hel.allu.common.util;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ExcavationAnnouncementDates {

  private static final int EXCAVATION_ANNOUNCEMENT_WARRANTY_YEARS = 2;

  public static ZonedDateTime warrantySupervisionDate(ZonedDateTime workFinishedDate) {
    // Warranty supervision week before warranty end
    return guaranteeEndDate(workFinishedDate).minusWeeks(1);
  }

  public static ZonedDateTime finalSupervisionDate(ZonedDateTime workFinishedDate) {
    return isBeforeToday(workFinishedDate) ? ZonedDateTime.now() : workFinishedDate.plusDays(1);
  }

  public static ZonedDateTime operationalConditionSupervisionDate(ZonedDateTime operationalConditionDate) {
    return isBeforeToday(operationalConditionDate) ? ZonedDateTime.now() : operationalConditionDate.plusDays(1);
  }

  private static boolean isBeforeToday(ZonedDateTime date) {
    return TimeUtil.homeTime(date).truncatedTo(ChronoUnit.DAYS)
        .isBefore(TimeUtil.homeTime(ZonedDateTime.now()).truncatedTo(ChronoUnit.DAYS));
  }

  public static ZonedDateTime guaranteeEndDate(ZonedDateTime workFinishedDate) {
    return workFinishedDate.plusYears(EXCAVATION_ANNOUNCEMENT_WARRANTY_YEARS);
  }

}
