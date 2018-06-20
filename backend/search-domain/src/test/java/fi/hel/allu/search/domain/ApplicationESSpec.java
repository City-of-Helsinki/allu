package fi.hel.allu.search.domain;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.util.RecurringApplication;
import org.junit.runner.RunWith;

import java.time.ZonedDateTime;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ApplicationESSpec {

  private RecurringApplication recurringApplication;

  {
    describe("Recurring event periods", () -> {
      it("should handle period within one year", () -> {
        recurringApplication = new RecurringApplication(
            ZonedDateTime.parse("2016-02-01T10:00:00+00:00"),
            ZonedDateTime.parse("2016-04-10T12:00:00+00:00"),
            ZonedDateTime.parse("2100-01-01T01:00:00+00:00"));
        assertEquals(ZonedDateTime.parse("2016-02-01T10:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getStartTime());
        assertEquals(ZonedDateTime.parse("1972-02-01T10:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getPeriod1Start());
        assertEquals(ZonedDateTime.parse("1972-04-10T12:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getPeriod1End());
        assertEquals(0, recurringApplication.getPeriod2Start());
        assertEquals(0, recurringApplication.getPeriod2End());
      });
      it("should handle period within two years", () -> {
        recurringApplication = new RecurringApplication(
            ZonedDateTime.parse("2016-12-01T10:00:00+00:00"),
            ZonedDateTime.parse("2017-01-10T12:00:00+00:00"),
            ZonedDateTime.parse("2100-01-01T01:00:00+00:00"));
        assertEquals(ZonedDateTime.parse("2016-12-01T10:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getStartTime());
        assertEquals(ZonedDateTime.parse("1972-12-01T10:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getPeriod1Start());
        assertEquals(RecurringApplication.END_1972, recurringApplication.getPeriod1End());
        assertEquals(RecurringApplication.BEGINNING_1972, recurringApplication.getPeriod2Start());
        assertEquals(ZonedDateTime.parse("1972-01-10T12:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getPeriod2End());
      });
      it("should handle periods lasting more than one year", () -> {
        recurringApplication = new RecurringApplication(
            ZonedDateTime.parse("2016-12-01T10:00:00+00:00"),
            ZonedDateTime.parse("2017-12-01T10:00:01+00:00"),
            ZonedDateTime.parse("2100-01-01T01:00:00+00:00"));
        assertEquals(ZonedDateTime.parse("2016-12-01T10:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getStartTime());
        // well, it's bizarre to have period1 and period2 to overlap, but makes sense when searching recurring applications
        assertEquals(ZonedDateTime.parse("1972-12-01T10:00:00+00:00").toInstant().toEpochMilli(), recurringApplication.getPeriod1Start());
        assertEquals(RecurringApplication.END_1972, recurringApplication.getPeriod1End());
        assertEquals(RecurringApplication.BEGINNING_1972, recurringApplication.getPeriod2Start());
        assertEquals(ZonedDateTime.parse("1972-12-01T10:00:01+00:00").toInstant().toEpochMilli(), recurringApplication.getPeriod2End());
      });
    });
  }
}
