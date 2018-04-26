package fi.hel.allu.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Time related utlitity methods.
 */
public class TimeUtil {

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
}
