package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.service.WinterTimeService;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ExcavationPricingSpec {

  WinterTimeService winterTimeService;
  PricingExplanator pricingExplanator;
  Application app;
  ExcavationPricing exc;

  {
    describe("Excavation Announcement Pricing", () -> {

      context("with a three-day application", () -> {
        beforeEach(()-> {
          winterTimeService = Mockito.mock(WinterTimeService.class);
          pricingExplanator = Mockito.mock(PricingExplanator.class);

          app = new Application();
          app.setExtension(new ExcavationAnnouncement());
          app.setStartTime(ZonedDateTime.parse("2017-04-20T08:00:00+03:00"));
          app.setEndTime(ZonedDateTime.parse("2017-04-22T17:00:00+03:00"));
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator);
        });
        context("On price class 2, with area of 65 sqm", () -> {
          it("should cost 3 * 32.50 +  180 EUR", () -> {
            exc.addLocationPrice(1, 65.0, 2);
            assertEquals(3 * 3250 + 18000, exc.getPriceInCents());
          });
        });

        context("On price class 1, with area of 121 sqm", () -> {
          it("should cost 3 * 80 +  180 EUR", () -> {
            exc.addLocationPrice(1, 121.0, 1);
            assertEquals(3 * 8000 + 18000, exc.getPriceInCents());
          });
        });

        context("On price class 3, with area of 21 sqm", () -> {
          it("should cost 3 * 12.50 +  180 EUR", () -> {
            exc.addLocationPrice(1, 21.0, 3);
            assertEquals(3 * 1250 + 18000, exc.getPriceInCents());
          });
        });

      });

    });
  }
}
