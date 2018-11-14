package fi.hel.allu.model.pricing;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AreaRental;

import org.junit.runner.RunWith;

import java.time.ZonedDateTime;
import java.util.Collections;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PricingKey;
import static org.junit.Assert.assertEquals;
import org.mockito.Mockito;

@RunWith(Spectrum.class)
public class AreaRentalPricingSpec extends LocationBasedPricing {

  Application app;
  AreaRentalPricing arp;
  PricingDao pricingDao;
  PricingExplanator pricingExplanator;

  {
    describe("Area rental Pricing", () -> {
      beforeEach(() -> {
        app = new Application();
        app.setExtension(new AreaRental());
        pricingDao = Mockito.mock(PricingDao.class);
        pricingExplanator = Mockito.mock(PricingExplanator.class);
        Mockito.when(pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MINOR_DISTURBANCE_HANDLING_FEE)).thenReturn(6000);
        Mockito.when(pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MAJOR_DISTURBANCE_HANDLING_FEE)).thenReturn(18000);
        Mockito.when(pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNIT_PRICE, "3")).thenReturn(130);
        Mockito.when(pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNIT_PRICE, "2")).thenReturn(300);
        Mockito.when(pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNIT_PRICE, "1")).thenReturn(600);
        Mockito.when(pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNDERPASS_DICOUNT_PERCENTAGE)).thenReturn(50);
        Mockito.when(pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.AREA_UNIT_M2)).thenReturn(15);
      });

      context("with Five-day snow work", () -> {
        final ZonedDateTime start = ZonedDateTime.parse("2017-04-20T08:00:00+03:00");
        final ZonedDateTime end = ZonedDateTime.parse("2017-04-24T17:00:00+03:00");

        beforeEach(() -> {
          app.setStartTime(start);
          app.setEndTime(end);
          app.setKindsWithSpecifiers(Collections.singletonMap(ApplicationKind.SNOW_WORK, Collections.emptyList()));
          arp = new AreaRentalPricing(app, pricingDao, pricingExplanator);
        });

        context("On price class 2, with area of 85 sqm", () -> {
          it("Should cost 5 * 6 * 3.00 EUR +  60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 85.0, "2", start, end));
            assertEquals(5 * 6 * 300 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 3, with area of 45 sqm", () -> {
          it("Should cost 5 * 3 * 1.30 EUR +  60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 45.0, "3", start, end));
            assertEquals(5 * 3 * 130 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 1, with area of 45.1 sqm", () -> {
          it("Should cost 5 * 4 * 6.00 EUR +  60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 45.1, "1", start, end));
            assertEquals(5 * 4 * 600 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 1, with area of 45.1 sqm, with underpass", () -> {
          it("Should cost 0.5 * 5 * 4 * 6.00 EUR +  60 EUR", () -> {
            Location location = getLocation(1, 45.1, "1", start, end);
            location.setUnderpass(true);
            arp.addLocationPrice(location);
            assertEquals((int)Math.round(0.5 * 5 * 4 * 600) + 6000, arp.getPriceInCents());
          });
        });

      });

      context("with Thirty-day construction work", () -> {
        final ZonedDateTime start = ZonedDateTime.parse("2017-06-01T08:00:00+03:00");
        final ZonedDateTime end = ZonedDateTime.parse("2017-06-30T17:00:00+03:00");

        beforeEach(() -> {
          app.setStartTime(start);
          app.setEndTime(end);
          app.setKindsWithSpecifiers(
              Collections.singletonMap(ApplicationKind.NEW_BUILDING_CONSTRUCTION, Collections.emptyList()));
          arp = new AreaRentalPricing(app, pricingDao, pricingExplanator);
        });

        context("On price class 2, with area of 1000 sqm", () -> {
          it("Should cost 30 * 67 * 3.00 EUR + 60 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 1000.0, "2", start, end));
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
          app.setStartTime(start);
          app.setEndTime(end);
          app.setKindsWithSpecifiers(
              Collections.singletonMap(ApplicationKind.NEW_BUILDING_CONSTRUCTION, Collections.emptyList()));
          arp = new AreaRentalPricing(app, pricingDao, pricingExplanator);
        });

        context("On price class 2, with area of 1000 sqm", () -> {
          it("Should cost 30 * 67 * 3.00 EUR + 180 EUR", () -> {
            arp.addLocationPrice(getLocation(1, 1000.0, "2", start, end));
            assertEquals(30 * 67 * 300 + 18000, arp.getPriceInCents());
          });
        });
      });

    });
  }
}
