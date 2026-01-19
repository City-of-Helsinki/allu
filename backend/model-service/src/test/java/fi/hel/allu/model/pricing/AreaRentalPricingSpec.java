package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PricingKey;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(Spectrum.class)
public class AreaRentalPricingSpec extends LocationBasedPricing {

  Application app;
  AreaRentalPricing arp;
  PricingDao pricingDao;
  PricingExplanator pricingExplanator;

  private void createAreaRentalPricing(
    ZonedDateTime start,
    ZonedDateTime end,
    ApplicationKind kind
  ) {
    app.setStartTime(start);
    app.setEndTime(end);
    app.setKindsWithSpecifiers(
      Collections.singletonMap(kind, Collections.emptyList()));
    arp = new AreaRentalPricing(app, pricingDao, pricingExplanator, Collections.emptyList());
  }

  {
    describe("Area rental Pricing", () -> {
      beforeEach(() -> {
        app = new Application();
        app.setExtension(new AreaRental());
        pricingDao = Mockito.mock(PricingDao.class);
        pricingExplanator = Mockito.mock(PricingExplanator.class);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.MINOR_DISTURBANCE_HANDLING_FEE), any())).thenReturn(6000);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.MAJOR_DISTURBANCE_HANDLING_FEE), any())).thenReturn(18000);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.UNIT_PRICE), eq("3"), any())).thenReturn(130);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.UNIT_PRICE), eq("2"), any())).thenReturn(300);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.UNIT_PRICE), eq("1"), any())).thenReturn(600);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.UNDERPASS_DICOUNT_PERCENTAGE), any())).thenReturn(50);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.AREA_UNIT_M2), any())).thenReturn(15);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.HANDLING_FEE_LT_8_DAYS), any())).thenReturn(8000);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.HANDLING_FEE_LT_6_MONTHS), any())).thenReturn(24000);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.AREA_RENTAL), eq(PricingKey.HANDLING_FEE_GE_6_MONTHS), any())).thenReturn(40000);
      });

      context("with Five-day snow work", () -> {
        final ZonedDateTime start = ZonedDateTime.parse("2017-04-20T08:00:00+03:00");
        final ZonedDateTime end = ZonedDateTime.parse("2017-04-24T17:00:00+03:00");

        beforeEach(() -> createAreaRentalPricing(start, end, ApplicationKind.SNOW_WORK));

        context("On price class 2, with area of 85 sqm", () -> {
          it("Should cost 5 * 6 * 3.00 EUR +  60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 85.0, "2", start, end), start);
            assertEquals(5 * 6 * 300 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 3, with area of 45 sqm", () -> {
          it("Should cost 5 * 3 * 1.30 EUR +  60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 45.0, "3", start, end), start);
            assertEquals(5 * 3 * 130 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 1, with area of 45.1 sqm", () -> {
          it("Should cost 5 * 4 * 6.00 EUR +  60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 45.1, "1", start, end), start);
            assertEquals(5 * 4 * 600 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 1, with area of 45.1 sqm, with underpass", () -> {
          it("Should cost 0.5 * 5 * 4 * 6.00 EUR +  60 EUR", () -> {
            Location location = getLocation(1, 45.1, "1", start, end);
            location.setUnderpass(true);
            arp.addLocationPrice(location, start);
            assertEquals((int)Math.round(0.5 * 5 * 4 * 600) + 6000, arp.getPriceInCents());
          });
        });

      });

      context("with Thirty-day construction work", () -> {
        final ZonedDateTime start = ZonedDateTime.parse("2017-06-01T08:00:00+03:00");
        final ZonedDateTime end = ZonedDateTime.parse("2017-06-30T17:00:00+03:00");

        beforeEach(() -> createAreaRentalPricing(start, end, ApplicationKind.NEW_BUILDING_CONSTRUCTION));

        context("On price class 2, with area of 1000 sqm", () -> {
          it("Should cost 30 * 67 * 3.00 EUR + 60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 1000.0, "2", start, end), start);
            assertEquals(30 * 67 * 300 + 6000, arp.getPriceInCents());
          });
        });
      });

      context("with major disturbance", () -> {
        final ZonedDateTime start = ZonedDateTime.parse("2017-06-01T08:00:00+03:00");
        final ZonedDateTime end = ZonedDateTime.parse("2017-06-30T17:00:00+03:00");

        beforeEach(() -> {
          AreaRental areaRental = new AreaRental();
          areaRental.setMajorDisturbance(true);
          app.setExtension(areaRental);
          createAreaRentalPricing(start, end, ApplicationKind.NEW_BUILDING_CONSTRUCTION);
        });

        context("On price class 2, with area of 1000 sqm", () -> {
          it("Should cost 30 * 67 * 3.00 EUR + 180 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 1000.0, "2", start, end), start);
            assertEquals(30 * 67 * 300 + 18000, arp.getPriceInCents());
          });
        });
      });

      context("handling fee logic after 1.3.2026", () -> {

        context("work lasting less than 8 days", () -> {
          final ZonedDateTime start = ZonedDateTime.parse("2026-03-10T08:00:00+02:00");
          final ZonedDateTime end   = ZonedDateTime.parse("2026-03-16T17:00:00+02:00"); // 7 days

          beforeEach(() -> createAreaRentalPricing(start, end, ApplicationKind.NEW_BUILDING_CONSTRUCTION));

          it("should apply HANDLING_FEE_LT_8_DAYS pricing", () -> assertEquals(8000, arp.getPriceInCents()));
        });

        context("work lasting at least 8 days but less than 6 months", () -> {
          final ZonedDateTime start = ZonedDateTime.parse("2026-03-01T08:00:00+02:00");
          final ZonedDateTime end   = ZonedDateTime.parse("2026-04-15T17:00:00+02:00");

          beforeEach(() -> createAreaRentalPricing(start, end, ApplicationKind.NEW_BUILDING_CONSTRUCTION));

          it("should apply HANDLING_FEE_LT_6_MONTHS pricing", () -> assertEquals(24000, arp.getPriceInCents()));
        });

        context("work lasting at least 6 months", () -> {
          final ZonedDateTime start = ZonedDateTime.parse("2026-03-01T08:00:00+02:00");
          final ZonedDateTime end   = ZonedDateTime.parse("2026-09-01T08:00:00+02:00");

          beforeEach(() -> createAreaRentalPricing(start, end, ApplicationKind.NEW_BUILDING_CONSTRUCTION));

          it("should apply HANDLING_FEE_GE_6_MONTHS pricing", () -> assertEquals(40000, arp.getPriceInCents()));
        });
      });

      context("duration edge cases", () -> context("exactly 8 days", () -> {
        final ZonedDateTime start = ZonedDateTime.parse("2026-03-01T00:00:00+02:00");
        final ZonedDateTime end   = ZonedDateTime.parse("2026-03-08T23:59:59+02:00");

        beforeEach(() -> createAreaRentalPricing(start, end, ApplicationKind.NEW_BUILDING_CONSTRUCTION));

        it("should not be treated as less than 8 days", () -> assertEquals(24000, arp.getPriceInCents()));
      }));

      context("handling fee logic before 1.3.2026", () -> {

        final ZonedDateTime start = ZonedDateTime.parse("2026-02-28T08:00:00+02:00");
        final ZonedDateTime end   = ZonedDateTime.parse("2026-03-30T17:00:00+02:00");

        beforeEach(() -> {
          AreaRental areaRental = new AreaRental();
          areaRental.setMajorDisturbance(false);
          app.setExtension(areaRental);
          createAreaRentalPricing(start, end, ApplicationKind.NEW_BUILDING_CONSTRUCTION);
        });

        it("should still use MINOR_DISTURBANCE_HANDLING_FEE", () -> assertEquals(6000, arp.getPriceInCents()));
      });
    });
  }
}
