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
}
