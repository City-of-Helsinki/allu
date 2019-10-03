package fi.hel.allu.common.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class WinterTimeTest {

  @Test
  public void shouldReturnWinterTimeEnd() {
    LocalDate winterTimeEnd = LocalDate.parse("1972-05-14");
    WinterTime winterTime = new WinterTime(LocalDate.parse("1972-12-01"), winterTimeEnd);
    ZonedDateTime testDate =  LocalDate.parse("2018-12-01").atStartOfDay(TimeUtil.HelsinkiZoneId);
    assertEquals(winterTimeEnd.withYear(2019), winterTime.getWinterTimeEnd(testDate));
    testDate =  LocalDate.parse("2019-01-01").atStartOfDay(TimeUtil.HelsinkiZoneId);
    assertEquals(winterTimeEnd.withYear(2019), winterTime.getWinterTimeEnd(testDate));
    testDate =  LocalDate.parse("2019-05-14").atStartOfDay(TimeUtil.HelsinkiZoneId);
    assertEquals(winterTimeEnd.withYear(2019), winterTime.getWinterTimeEnd(testDate));
  }

  @Test
  public void shouldCheckIsInWinterTime() {
    WinterTime winterTime1 = new WinterTime(LocalDate.parse("1972-12-01"), LocalDate.parse("1972-05-14"));
    WinterTime winterTime2 = new WinterTime(LocalDate.parse("1972-01-03"), LocalDate.parse("1972-05-14"));
    List<ZonedDateTime> datesInWinter1 = Arrays.asList(
        LocalDate.parse("2018-12-01").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2018-12-30").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-01-01").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2018-03-01").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2018-05-14").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    List<ZonedDateTime> datesNotInWinter1 = Arrays.asList(
        LocalDate.parse("2018-11-30").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-05-15").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-06-01").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    List<ZonedDateTime> datesInWinter2 = Arrays.asList(
        LocalDate.parse("2019-01-03").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-03-30").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-05-14").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    List<ZonedDateTime> datesNotInWinter2 = Arrays.asList(
        LocalDate.parse("2019-01-02").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-05-15").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-12-01").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    datesInWinter1.forEach(d -> assertTrue(winterTime1.isInWinterTime(d)));
    datesNotInWinter1.forEach(d -> assertFalse(winterTime1.isInWinterTime(d)));
    datesInWinter2.forEach(d -> assertTrue(winterTime2.isInWinterTime(d)));
    datesNotInWinter2.forEach(d -> assertFalse(winterTime2.isInWinterTime(d)));
  }
}
