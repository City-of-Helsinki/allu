package fi.hel.allu.model.pricing;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.domain.Application;

import org.junit.runner.RunWith;

import java.time.ZonedDateTime;

import static com.greghaskins.spectrum.Spectrum.describe;
import static com.greghaskins.spectrum.Spectrum.it;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ExcavationPricingSpec {
  {
    describe("Excavation Announcement Pricing", () -> {

      describe("A three-day application", () -> {
        Application app = new Application();
        app.setStartTime(ZonedDateTime.parse("2017-04-20T08:00:00+03:00"));
        app.setEndTime(ZonedDateTime.parse("2017-04-22T17:00:00+03:00"));

        describe("On price class 2, with area of 65 sqm", () -> {
          ExcavationPricing exc = new ExcavationPricing(app);
          exc.addLocationPrice(65.0, 2);

          it("should cost 3 * 32.50 +  180 EUR", () -> {
            assertEquals(3 * 3250 + 18000, exc.getPriceInCents());
          });
        });

        describe("On price class 1, with area of 121 sqm", () -> {
          ExcavationPricing exc = new ExcavationPricing(app);
          exc.addLocationPrice(121.0, 1);

          it("should cost 3 * 80 +  180 EUR", () -> {
            assertEquals(3 * 8000 + 18000, exc.getPriceInCents());
          });
        });

        describe("On price class 3, with area of 21 sqm", () -> {
          ExcavationPricing exc = new ExcavationPricing(app);
          exc.addLocationPrice(21.0, 3);

          it("should cost 3 * 12.50 +  180 EUR", () -> {
            assertEquals(3 * 1250 + 18000, exc.getPriceInCents());
          });
        });

      });

    });
  }
}
