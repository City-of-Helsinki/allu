package fi.hel.allu.model.pricing;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.model.domain.Application;

import org.junit.runner.RunWith;

import java.time.ZonedDateTime;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class AreaRentalPricingSpec {
  {
    describe("Area rental Pricing", () -> {

      describe ("Five-day snow work", () -> {
        Application app = new Application();
        app.setStartTime(ZonedDateTime.parse("2017-04-20T08:00:00+03:00"));
        app.setEndTime(ZonedDateTime.parse("2017-04-24T17:00:00+03:00"));
        app.setKind(ApplicationKind.SNOW_WORK);

        describe("On price class 2, with area of 85 sqm", () -> {
          AreaRentalPricing arp = new AreaRentalPricing(app);
          arp.addLocationPrice(85.0, 2);

          it("Should cost 5 * 6 * 3.00 EUR +  60 EUR", () -> {
            assertEquals(5 * 6 * 300 + 6000, arp.getPriceInCents());
          });
        });

        describe("On price class 3, with area of 45 sqm", () -> {
          AreaRentalPricing arp = new AreaRentalPricing(app);
          arp.addLocationPrice(45.0, 3);

          it("Should cost 5 * 3 * 1.30 EUR +  60 EUR", () -> {
            assertEquals(5 * 3 * 130 + 6000, arp.getPriceInCents());
          });
        });

        describe("On price class 1, with area of 45.1 sqm", () -> {
          AreaRentalPricing arp = new AreaRentalPricing(app);
          arp.addLocationPrice(45.1, 1);

          it("Should cost 5 * 4 * 6.00 EUR +  60 EUR", () -> {
            assertEquals(5 * 4 * 600 + 6000, arp.getPriceInCents());
          });
        });

      });

      describe("Thirty-day construction work", () -> {
        Application app = new Application();
        app.setStartTime(ZonedDateTime.parse("2017-06-01T08:00:00+03:00"));
        app.setEndTime(ZonedDateTime.parse("2017-06-30T17:00:00+03:00"));
        app.setKind(ApplicationKind.NEW_BUILDING_CONSTRUCTION);

        describe("On price class 2, with area of 1000 sqm", () -> {
          AreaRentalPricing arp = new AreaRentalPricing(app);
          arp.addLocationPrice(1000.0, 2);

          it("Should cost 30 * 67 * 3.00 EUR +  1800 EUR", () -> {
            assertEquals(30 * 67 * 300 + 18000, arp.getPriceInCents());
          });
        });

      });
    });
  }
}
