package fi.hel.allu.model.domain.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeUtil {

  private static final ZoneId HOME_ZONE = ZoneId.of("Europe/Helsinki");

  /**
   * Move the given time to home timezone, keeping the same instant.
   *
   * @param zonedDateTime
   * @return the same time instant in home time zone.
   */
  public static ZonedDateTime homeTime(ZonedDateTime zonedDateTime) {
    return zonedDateTime == null ? null : zonedDateTime.withZoneSameInstant(HOME_ZONE);
  }
}
