package fi.hel.allu.model.domain.util;

import fi.hel.allu.common.util.CalendarUtil;

import java.time.ZonedDateTime;

/**
 * Utility for calculating how many days and build days an event has.
 */
public class EventDayUtil {

  public static int eventDays(ZonedDateTime eventStartTime, ZonedDateTime eventEndTime) {
    return CalendarUtil.daysBetween(eventStartTime, eventEndTime);
  }

  public static int buildDays(ZonedDateTime eventStartTime, ZonedDateTime eventEndTime,
        ZonedDateTime applicationStartTime, ZonedDateTime applicationEndTime) {
    
    int buildDays = CalendarUtil.daysBetween(applicationStartTime, eventStartTime);
    buildDays += CalendarUtil.daysBetween(eventEndTime, applicationEndTime);
    return buildDays;
  }
}
