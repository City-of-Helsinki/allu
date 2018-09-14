package fi.hel.allu.common.util;

import java.time.ZonedDateTime;

import org.junit.Test;

import static org.junit.Assert.*;

public class TimeUtilTest {

  final ZonedDateTime testDate = ZonedDateTime.of(2018, 8, 9, 12, 33, 1, 789, TimeUtil.HelsinkiZoneId);

  @Test
  public void startOfDate() {
    final ZonedDateTime dateStart = TimeUtil.startOfDay(testDate);
    assertEquals(2018, dateStart.getYear());
    assertEquals(8, dateStart.getMonthValue());
    assertEquals(9, dateStart.getDayOfMonth());
    assertEquals(0, dateStart.getHour());
    assertEquals(0, dateStart.getMinute());
    assertEquals(0, dateStart.getSecond());
    assertEquals(0, dateStart.getNano());
    assertEquals(TimeUtil.HelsinkiZoneId, dateStart.getZone());
  }

  @Test
  public void endOfDate() {
    final ZonedDateTime dateEnd = TimeUtil.endOfDay(testDate);
    assertEquals(2018, dateEnd.getYear());
    assertEquals(8, dateEnd.getMonthValue());
    assertEquals(9, dateEnd.getDayOfMonth());
    assertEquals(23, dateEnd.getHour());
    assertEquals(59, dateEnd.getMinute());
    assertEquals(59, dateEnd.getSecond());
    assertEquals(999999999, dateEnd.getNano());
    assertEquals(TimeUtil.HelsinkiZoneId, dateEnd.getZone());
  }

  @Test
  public void isSameDate() {
    ZonedDateTime today = ZonedDateTime.now();
    ZonedDateTime date1 = today.withHour(2);
    ZonedDateTime date2 = today.withHour(5);
    assertTrue(TimeUtil.isSameDate(date1, date2));
    date2 = date2.minusDays(1);
    assertFalse(TimeUtil.isSameDate(date1, date2));
  }
}
