package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.service.WinterTimeService;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.PricingKey;
import static org.junit.Assert.assertEquals;

@RunWith(Spectrum.class)
public class ExcavationPricingSpec extends LocationBasedPricing {

  WinterTimeService winterTimeService;
  PricingExplanator pricingExplanator;
  PricingDao pricingDao;
  Application app;
  ExcavationPricing exc;

  {
    describe("Excavation Announcement Pricing", () -> {
      final ZonedDateTime start = ZonedDateTime.parse("2017-04-20T08:00:00+03:00");
      final ZonedDateTime end = ZonedDateTime.parse("2017-04-22T17:00:00+03:00");

      context("with a three-day application", () -> {
        beforeEach(()-> {
          winterTimeService = Mockito.mock(WinterTimeService.class);
          pricingExplanator = Mockito.mock(PricingExplanator.class);
          pricingDao = Mockito.mock(PricingDao.class);
          Mockito.when(pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.HANDLING_FEE)).thenReturn(18000);
          Mockito.when(pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.SMALL_AREA_DAILY_FEE, "3")).thenReturn(1250);
          Mockito.when(pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.MEDIUM_AREA_DAILY_FEE, "2")).thenReturn(3250);
          Mockito.when(pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.LARGE_AREA_DAILY_FEE, "1")).thenReturn(8000);

          app = new Application();
          app.setExtension(new ExcavationAnnouncement());
          app.setStartTime(start);
          app.setEndTime(end);
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao);
        });
        context("On price class 2, with area of 65 sqm", () -> {
          it("should cost 3 * 32.50 +  180 EUR", () -> {
            exc.addLocationPrice(getLocation(1, 65.0, "2", start, end));
            assertEquals(3 * 3250 + 18000, exc.getPriceInCents());
          });
        });

        context("On price class 1, with area of 121 sqm", () -> {
          it("should cost 3 * 80 +  180 EUR", () -> {
            exc.addLocationPrice(getLocation(1, 121.0, "1", start, end));
            assertEquals(3 * 8000 + 18000, exc.getPriceInCents());
          });
        });

        context("On price class 3, with area of 21 sqm", () -> {
          it("should cost 3 * 12.50 +  180 EUR", () -> {
            exc.addLocationPrice(getLocation(1, 21.0, "3", start, end));
            assertEquals(3 * 1250 + 18000, exc.getPriceInCents());
          });
        });

      });

    });
  }


}
