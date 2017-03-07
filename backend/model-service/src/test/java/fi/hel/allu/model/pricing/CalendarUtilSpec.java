package fi.hel.allu.model.pricing;

import com.greghaskins.spectrum.Spectrum;

import org.junit.runner.RunWith;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(Spectrum.class)
public class CalendarUtilSpec {

  {
    describe("Time length calculations", () -> {

      describe("Special cases", () -> {
        it("should throw for end before start", () -> {
          try {
            long days = CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-02-28T00:00:00+02:00"),
                ZonedDateTime.parse("2017-02-27T23:59:59+02:00"), ChronoUnit.DAYS);
            fail("Did not throw, set days=" + days);
          } catch (IllegalArgumentException e) {
          } catch (Exception e) {
            fail("Threw wrong expeption");
          }
        });

        it("should return 0 if start time is null", () -> {
          assertEquals(0, CalendarUtil.startingUnitsBetween(null, ZonedDateTime.parse("2017-03-01T15:15:30+02:00"),
              ChronoUnit.YEARS));
        });

        it("should return 0 if end time is null", () -> {
          assertEquals(0, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-03-01T15:15:30+02:00"), null,
              ChronoUnit.MONTHS));
        });
      });

      describe("For DAYS", () -> {
        it("should count 1 day for 1.3.-1.3.", () -> {
          assertEquals(1, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-03-01T10:15:30+02:00"),
              ZonedDateTime.parse("2017-03-01T15:15:30+02:00"), ChronoUnit.DAYS));
        });

        it("should count 2 days for 28.2.-1.3.", () -> {
          assertEquals(2, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-02-28T00:00:00+02:00"),
              ZonedDateTime.parse("2017-03-01T23:59:59+02:00"), ChronoUnit.DAYS));
        });
      });

      describe("For WEEKS", () -> {
        it("should count 1 week for 1.3.-1.3.", () -> {
          assertEquals(1, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-03-01T23:59:59+02:00"),
              ZonedDateTime.parse("2017-03-01T23:59:59+02:00"), ChronoUnit.WEEKS));
        });

        it("should count 1 week for 27.2.-5.3. (Mon-Sun)", () -> {
          assertEquals(1, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-02-27T00:00:00+02:00"),
              ZonedDateTime.parse("2017-03-05T23:59:59+02:00"), ChronoUnit.WEEKS));
        });

        it("should count 2 weeks for 28.2.-6.3. (Tue-Mon)", () -> {
          assertEquals(2, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-02-28T00:00:00+02:00"),
              ZonedDateTime.parse("2017-03-06T00:00:00+02:00"), ChronoUnit.WEEKS));
        });

        it("should count 3 weeks for 26.2.-6.3. (Sun-Mon)", () -> {
          assertEquals(3, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-02-26T23:59:59+02:00"),
              ZonedDateTime.parse("2017-03-06T00:00:00+02:00"), ChronoUnit.WEEKS));
        });
      });

      describe("For MONTHS", () -> {
        it("should count 1 month for 1.3.-1.3.", () -> {
          assertEquals(1, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-03-01T23:59:59+02:00"),
              ZonedDateTime.parse("2017-03-01T23:59:59+02:00"), ChronoUnit.MONTHS));
        });

        it("should count 1 month for 1.3.-31.3.", () -> {
          assertEquals(1, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-03-01T00:00:00+02:00"),
              ZonedDateTime.parse("2017-03-31T23:59:59+02:00"), ChronoUnit.MONTHS));
        });

        it("should count 2 months for 28.2.-31.3.", () -> {
          assertEquals(2, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-02-28T23:59:59+02:00"),
              ZonedDateTime.parse("2017-03-31T23:59:59+02:00"), ChronoUnit.MONTHS));
        });

        it("should count 3 months for 28.2.-1.4.", () -> {
          assertEquals(3, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-02-28T23:59:59+02:00"),
              ZonedDateTime.parse("2017-04-01T00:00:00+02:00"), ChronoUnit.MONTHS));
        });
      });

      describe("For YEARS", () -> {
        it("should count 1 year for 1.1.-1.1.", () -> {
          assertEquals(1, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-01-01T23:59:59+02:00"),
              ZonedDateTime.parse("2017-01-01T23:59:59+02:00"), ChronoUnit.YEARS));
        });

        it("should count 1 year for 1.1.-31.12.", () -> {
          assertEquals(1, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-01-01T00:00:00+02:00"),
              ZonedDateTime.parse("2017-12-31T23:59:59+02:00"), ChronoUnit.YEARS));
        });

        it("should count 2 years for 1.1.2017-1.1.2018", () -> {
          assertEquals(2, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2017-01-01T00:00:00+02:00"),
              ZonedDateTime.parse("2018-01-01T00:00:00+02:00"), ChronoUnit.YEARS));
        });

        it("should count 3 years for 31.12.2016.-1.1.2018.", () -> {
          assertEquals(3, CalendarUtil.startingUnitsBetween(ZonedDateTime.parse("2016-12-31T23:59:59+02:00"),
              ZonedDateTime.parse("2018-01-01T00:00:00+02:00"), ChronoUnit.YEARS));
        });
      });

    });
  }
}
