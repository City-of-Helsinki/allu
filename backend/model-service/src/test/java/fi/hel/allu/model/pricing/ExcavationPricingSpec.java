package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.domain.PricingKey;
import fi.hel.allu.model.service.WinterTimeService;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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
      final List<InvoicingPeriod> periods = Arrays.asList(new InvoicingPeriod(1, StatusType.OPERATIONAL_CONDITION),
          new InvoicingPeriod(1, StatusType.FINISHED));

      context("with a three-day application", () -> {
        beforeEach(()-> {
          winterTimeService = Mockito.mock(WinterTimeService.class);
          pricingExplanator = Mockito.mock(PricingExplanator.class);
          pricingDao = Mockito.mock(PricingDao.class);
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.HANDLING_FEE), any())).thenReturn(18000);
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.SMALL_AREA_DAILY_FEE), eq("3"), any())).thenReturn(1250);
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.MEDIUM_AREA_DAILY_FEE), eq("2"), any())).thenReturn(3250);
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.LARGE_AREA_DAILY_FEE), eq("1"), any())).thenReturn(8000);

          app = new Application();
          app.setExtension(new ExcavationAnnouncement());
          app.setStartTime(start);
          app.setEndTime(end);
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
        });
        context("On price class 2, with area of 65 sqm", () -> {
          it("should cost 3 * 32.50 +  180 EUR", () -> {
            exc.addLocationPrice(getLocation(1, 65.0, "2", start, end), start);
            assertEquals(3 * 3250 + 18000, exc.getPriceInCents());
          });
        });

        context("On price class 1, with area of 121 sqm", () -> {
          it("should cost 3 * 80 +  180 EUR", () -> {
            exc.addLocationPrice(getLocation(1, 121.0, "1", start, end), start);
            assertEquals(3 * 8000 + 18000, exc.getPriceInCents());
          });
        });

        context("On price class 3, with area of 21 sqm", () -> {
          it("should cost 3 * 12.50 +  180 EUR", () -> {
            exc.addLocationPrice(getLocation(1, 21.0, "3", start, end), start);
            assertEquals(3 * 1250 + 18000, exc.getPriceInCents());
          });
        });

      });

    });
  }


}
