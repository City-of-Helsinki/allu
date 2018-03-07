package fi.hel.allu.model.domain.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class EventDayUtilTest {

  private static final ZonedDateTime testStartDate = ZonedDateTime.of(2018, 3, 1, 0, 0, 0, 0, ZoneId.systemDefault());
  private static final ZonedDateTime testEndDate = ZonedDateTime.of(2018, 3, 3, 23, 59, 59, 999, ZoneId.systemDefault());

  @Test
  public void zeroBuildDays() {
    final ZonedDateTime eventStartDate = null;
    final ZonedDateTime eventEndDate = null;
    final ZonedDateTime applicationStartDate = ZonedDateTime.from(testStartDate);
    final ZonedDateTime applicationEndDate = ZonedDateTime.from(testEndDate);
    assertEquals(0, EventDayUtil.buildDays(eventStartDate, eventEndDate, applicationStartDate, applicationEndDate));
  }

  @Test
  public void oneBuildDay() {
    final ZonedDateTime eventStartDate = ZonedDateTime.from(testStartDate);
    final ZonedDateTime eventEndDate = ZonedDateTime.from(testEndDate);
    final ZonedDateTime applicationStartDate = ZonedDateTime.from(testStartDate.minusDays(1));
    final ZonedDateTime applicationEndDate = ZonedDateTime.from(testEndDate);
    assertEquals(1, EventDayUtil.buildDays(eventStartDate, eventEndDate, applicationStartDate, applicationEndDate));
  }

  @Test
  public void twoBuildDays() {
    final ZonedDateTime eventStartDate = ZonedDateTime.from(testStartDate);
    final ZonedDateTime eventEndDate = ZonedDateTime.from(testEndDate);
    final ZonedDateTime applicationStartDate = ZonedDateTime.from(testStartDate.minusDays(1));
    final ZonedDateTime applicationEndDate = ZonedDateTime.from(testEndDate.plusDays(1));
    assertEquals(2, EventDayUtil.buildDays(eventStartDate, eventEndDate, applicationStartDate, applicationEndDate));
  }

  @Test
  public void fiveBuildDays() {
    final ZonedDateTime eventStartDate = ZonedDateTime.from(testStartDate);
    final ZonedDateTime eventEndDate = ZonedDateTime.from(testEndDate);
    final ZonedDateTime applicationStartDate = ZonedDateTime.from(testStartDate.minusDays(2));
    final ZonedDateTime applicationEndDate = ZonedDateTime.from(testEndDate.plusDays(3));
    assertEquals(5, EventDayUtil.buildDays(eventStartDate, eventEndDate, applicationStartDate, applicationEndDate));
  }
}
