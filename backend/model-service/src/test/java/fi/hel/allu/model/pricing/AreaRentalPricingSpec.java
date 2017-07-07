package fi.hel.allu.model.pricing;

import com.greghaskins.spectrum.Spectrum;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.model.domain.Application;
import org.junit.runner.RunWith;

import java.time.ZonedDateTime;

import static com.greghaskins.spectrum.dsl.specification.Specification.describe;
import static com.greghaskins.spectrum.dsl.specification.Specification.context;
import static com.greghaskins.spectrum.dsl.specification.Specification.it;
import static com.greghaskins.spectrum.dsl.specification.Specification.beforeEach;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class AreaRentalPricingSpec {

  Application app;
  AreaRentalPricing arp;

  {
    describe("Area rental Pricing", () -> {

      beforeEach(() -> app = new Application());

      context("with Five-day snow work", () -> {
        beforeEach(() -> {
          app.setStartTime(ZonedDateTime.parse("2017-04-20T08:00:00+03:00"));
          app.setEndTime(ZonedDateTime.parse("2017-04-24T17:00:00+03:00"));
          app.setKind(ApplicationKind.SNOW_WORK);
          arp = new AreaRentalPricing(app);
        });

        context("On price class 2, with area of 85 sqm", () -> {
          it("Should cost 5 * 6 * 3.00 EUR +  60 EUR", () -> {
            arp.addLocationPrice(85.0, 2);
            assertEquals(5 * 6 * 300 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 3, with area of 45 sqm", () -> {
          it("Should cost 5 * 3 * 1.30 EUR +  60 EUR", () -> {
            arp.addLocationPrice(45.0, 3);
            assertEquals(5 * 3 * 130 + 6000, arp.getPriceInCents());
          });
        });

        context("On price class 1, with area of 45.1 sqm", () -> {
          it("Should cost 5 * 4 * 6.00 EUR +  60 EUR", () -> {
            arp.addLocationPrice(45.1, 1);
            assertEquals(5 * 4 * 600 + 6000, arp.getPriceInCents());
          });
        });

      });

      context("with Thirty-day construction work", () -> {
        beforeEach(() -> {
          app.setStartTime(ZonedDateTime.parse("2017-06-01T08:00:00+03:00"));
          app.setEndTime(ZonedDateTime.parse("2017-06-30T17:00:00+03:00"));
          app.setKind(ApplicationKind.NEW_BUILDING_CONSTRUCTION);
          arp = new AreaRentalPricing(app);
        });

        context("On price class 2, with area of 1000 sqm", () -> {
          it("Should cost 30 * 67 * 3.00 EUR +  1800 EUR", () -> {
            arp.addLocationPrice(1000.0, 2);
            assertEquals(30 * 67 * 300 + 18000, arp.getPriceInCents());
          });
        });

      });
    });
  }
}
