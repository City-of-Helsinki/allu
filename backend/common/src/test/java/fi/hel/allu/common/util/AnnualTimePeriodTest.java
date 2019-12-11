package fi.hel.allu.common.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class AnnualTimePeriodTest {

  @Test
  public void shouldReturnAnnualPeriodEnd() {
    LocalDate annualPeriodEnd = LocalDate.parse("1972-05-14");
    AnnualTimePeriod annualTimePeriod = new AnnualTimePeriod(LocalDate.parse("1972-12-01"), annualPeriodEnd);
    ZonedDateTime testDate =  LocalDate.parse("2018-12-01").atStartOfDay(TimeUtil.HelsinkiZoneId);
    assertEquals(annualPeriodEnd.withYear(2019), annualTimePeriod.getAnnualPeriodEnd(testDate));
    testDate =  LocalDate.parse("2019-01-01").atStartOfDay(TimeUtil.HelsinkiZoneId);
    assertEquals(annualPeriodEnd.withYear(2019), annualTimePeriod.getAnnualPeriodEnd(testDate));
    testDate =  LocalDate.parse("2019-05-14").atStartOfDay(TimeUtil.HelsinkiZoneId);
    assertEquals(annualPeriodEnd.withYear(2019), annualTimePeriod.getAnnualPeriodEnd(testDate));
  }

  @Test
  public void shouldCheckIsInAnnualTimePeriod() {
    AnnualTimePeriod annualTimePeriod1 = new AnnualTimePeriod(LocalDate.parse("1972-12-01"), LocalDate.parse("1972-05-14"));
    AnnualTimePeriod annualTimePeriod2 = new AnnualTimePeriod(LocalDate.parse("1972-01-03"), LocalDate.parse("1972-05-14"));
    List<ZonedDateTime> datesInPeriod1 = Arrays.asList(
        LocalDate.parse("2018-12-01").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2018-12-30").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-01-01").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2018-03-01").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2018-05-14").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    List<ZonedDateTime> datesNotInPeriod1 = Arrays.asList(
        LocalDate.parse("2018-11-30").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-05-15").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-06-01").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    List<ZonedDateTime> datesInPeriod2 = Arrays.asList(
        LocalDate.parse("2019-01-03").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-03-30").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-05-14").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    List<ZonedDateTime> datesNotInPeriod2 = Arrays.asList(
        LocalDate.parse("2019-01-02").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-05-15").atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse("2019-12-01").atStartOfDay(TimeUtil.HelsinkiZoneId)
    );
    datesInPeriod1.forEach(d -> assertTrue(annualTimePeriod1.isInAnnualPeriod(d)));
    datesNotInPeriod1.forEach(d -> assertFalse(annualTimePeriod1.isInAnnualPeriod(d)));
    datesInPeriod2.forEach(d -> assertTrue(annualTimePeriod2.isInAnnualPeriod(d)));
    datesNotInPeriod2.forEach(d -> assertFalse(annualTimePeriod2.isInAnnualPeriod(d)));
  }
}
