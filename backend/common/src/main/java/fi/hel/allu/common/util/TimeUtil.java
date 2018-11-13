package fi.hel.allu.common.util;

import java.time.Instant;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Time related utlitity methods.
 */
public class TimeUtil {

  private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d.M.uuuu");
  private static final DateTimeFormatter timeStampFormatter = DateTimeFormatter.ofPattern("d.M.uuuu 'kello' HH.mm");

  public static final ZoneId HelsinkiZoneId = ZoneId.of("Europe/Helsinki");

  /**
   * Convert epocs milliseconds (in UTC) to Helsinki time.
   *
   * @param   millis  Milliseconds to be converted.
   * @return  Helsinki time.
   */
  public static ZonedDateTime millisToZonedDateTime(long millis) {
    Instant i = Instant.ofEpochMilli( millis  );
    return ZonedDateTime.ofInstant(i, HelsinkiZoneId);
  }

  /**
   * Returns date converted to milliseconds since epoch.
   *
   * @param date  Date to be converted or <code>null</code>.
   * @return  Date in milliseconds or <code>null</code> in case given date was <code>null</code>.
   */
  public static Long dateToMillis(ZonedDateTime date) {
    return date == null ? null : date.toInstant().toEpochMilli();
  }

  /**
   * Move the given time to home timezone, keeping the same instant.
   *
   * @param zonedDateTime
   * @return the same time instant in home time zone.
   */
  public static ZonedDateTime homeTime(ZonedDateTime zonedDateTime) {
    return zonedDateTime == null ? null : zonedDateTime.withZoneSameInstant(HelsinkiZoneId);
  }

  public static ZonedDateTime startOfDay(ZonedDateTime time) {
    return time.toLocalDate().atStartOfDay(HelsinkiZoneId);
  }

  public static ZonedDateTime endOfDay(ZonedDateTime time) {
    return startOfDay(time).plusDays(1).minusNanos(1);
  }

  public static String dateAsString(ZonedDateTime date) {
    return date.withZoneSameInstant(HelsinkiZoneId).format(dateTimeFormatter);
  }

  public static String dateAsDateTimeString(ZonedDateTime dateTime) {
    return dateTime.withZoneSameInstant(HelsinkiZoneId).format(timeStampFormatter);
  }

  public static boolean isSameDateOrLater(ZonedDateTime dateToCheck, ZonedDateTime limitDate) {
    return isSameDate(dateToCheck, limitDate) || dateToCheck.isAfter(limitDate);
  }

  public static boolean isSameDate(ZonedDateTime date1, ZonedDateTime date2) {
    return homeTime(date1).truncatedTo(ChronoUnit.DAYS).equals(homeTime(date2).truncatedTo(ChronoUnit.DAYS));
  }

  public static ZonedDateTime nextDay(ZonedDateTime dateTime) {
    return dateTime != null ? dateTime.plusDays(1) : null;
  }

  public static ZonedDateTime last(ZonedDateTime...dateTimes ) {
    return Stream.of(dateTimes).sorted(Comparator.reverseOrder()).findFirst().get();
  }

  public static ZonedDateTime first(ZonedDateTime...dateTimes ) {
    return Stream.of(dateTimes).sorted().findFirst().get();
  }

  public static boolean datePeriodsOverlap(ZonedDateTime period1Start, ZonedDateTime period1End, ZonedDateTime period2Start,
      ZonedDateTime period2End) {
    return !startOfDay(period1End).isBefore(startOfDay(period2Start))
        && !startOfDay(period1Start).isAfter(startOfDay(period2End));
  }
}
