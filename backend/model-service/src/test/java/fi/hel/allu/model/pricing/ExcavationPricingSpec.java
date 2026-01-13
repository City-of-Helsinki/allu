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
    describe("Excavation Announcement Pricing Before 1.3.2026", () -> {
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

        context("On price class 2, with area of 65 sqm", () -> it("should cost 3 * 32.50 +  180 EUR", () -> {
          exc.addLocationPrice(getLocation(1, 65.0, "2", start, end), start);
          assertEquals(3 * 3250 + 18000, exc.getPriceInCents());
        }));

        context("On price class 1, with area of 121 sqm", () -> it("should cost 3 * 80 +  180 EUR", () -> {
          exc.addLocationPrice(getLocation(1, 121.0, "1", start, end), start);
          assertEquals(3 * 8000 + 18000, exc.getPriceInCents());
        }));

        context("On price class 3, with area of 21 sqm", () -> it("should cost 3 * 12.50 +  180 EUR", () -> {
          exc.addLocationPrice(getLocation(1, 21.0, "3", start, end), start);
          assertEquals(3 * 1250 + 18000, exc.getPriceInCents());
        }));
      });
    });

    describe("Excavation Announcement Pricing After 1.3.2026", () -> {
      final List<InvoicingPeriod> periods = List.of(new InvoicingPeriod(1, StatusType.OPERATIONAL_CONDITION));

      context("Handling fee duration logic", () -> {
        beforeEach(() -> {
          winterTimeService = Mockito.mock(WinterTimeService.class);
          pricingExplanator = Mockito.mock(PricingExplanator.class);
          pricingDao = Mockito.mock(PricingDao.class);

          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.HANDLING_FEE), any())).thenReturn(1234);
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.HANDLING_FEE_LT_6_MONTHS), any())).thenReturn(24000);
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.HANDLING_FEE_GE_6_MONTHS), any())).thenReturn(40000);
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT), eq(PricingKey.HANDLING_FEE_SELF_SUPERVISION), any())).thenReturn(6000);
        });

        it("uses LT_6_MONTHS when duration is less than 6 months", () -> {
          ZonedDateTime start = ZonedDateTime.parse("2026-03-01T00:00:00+02:00");
          ZonedDateTime end   = ZonedDateTime.parse("2026-08-01T00:00:00+02:00"); // < 6 kk

          Application app = new Application();
          ExcavationAnnouncement ea = new ExcavationAnnouncement();
          app.setExtension(ea);
          app.setStartTime(start);
          app.setEndTime(end);

          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          // area ja payment class ovat yhdentekeviä, koska daily fee ei ole stubattu → vain handling fee jää
          exc.addLocationPrice(getLocation(1, 10.0, "1", start, end), start);

          assertEquals(24000, exc.getPriceInCents());

          Mockito.verify(pricingDao).findValue(
            eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
            eq(PricingKey.HANDLING_FEE_LT_6_MONTHS),
            any()
          );
        });

        it("uses GE_6_MONTHS when duration is exactly 6 months", () -> {
          ZonedDateTime start = ZonedDateTime.parse("2026-03-01T00:00:00+02:00");
          ZonedDateTime end   = ZonedDateTime.parse("2026-09-01T00:00:00+02:00"); // tasan 6 kk

          Application app = new Application();
          ExcavationAnnouncement ea = new ExcavationAnnouncement();
          app.setExtension(ea);
          app.setStartTime(start);
          app.setEndTime(end);

          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          // area ja payment class ovat yhdentekeviä, koska daily fee ei ole stubattu → vain handling fee jää
          exc.addLocationPrice(getLocation(1, 10.0, "1", start, end), start);

          assertEquals(40000, exc.getPriceInCents());

          Mockito.verify(pricingDao).findValue(
            eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
            eq(PricingKey.HANDLING_FEE_GE_6_MONTHS),
            any()
          );
        });

        it("uses GE_6_MONTHS when duration is longer than 6 months", () -> {
          ZonedDateTime start = ZonedDateTime.parse("2026-03-01T00:00:00+02:00");
          ZonedDateTime end   = ZonedDateTime.parse("2026-10-01T00:00:00+02:00"); // > 6 kk

          Application app = new Application();
          ExcavationAnnouncement ea = new ExcavationAnnouncement();
          app.setExtension(ea);
          app.setStartTime(start);
          app.setEndTime(end);

          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          // area ja payment class ovat yhdentekeviä, koska daily fee ei ole stubattu → vain handling fee jää
          exc.addLocationPrice(getLocation(1, 10.0, "1", start, end), start);

          assertEquals(40000, exc.getPriceInCents());
        });

        it("self supervision overrides duration based handling fee", () -> {
          ZonedDateTime start = ZonedDateTime.parse("2026-03-01T00:00:00+02:00");
          ZonedDateTime end   = ZonedDateTime.parse("2026-12-31T00:00:00+02:00");

          Application app = new Application();
          ExcavationAnnouncement ea = new ExcavationAnnouncement();
          ea.setSelfSupervision(true);
          app.setExtension(ea);
          app.setStartTime(start);
          app.setEndTime(end);

          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          // area ja payment class ovat yhdentekeviä, koska daily fee ei ole stubattu → vain handling fee jää
          exc.addLocationPrice(getLocation(1, 10.0, "1", start, end), start);

          assertEquals(6000, exc.getPriceInCents());

          Mockito.verify(pricingDao).findValue(
            eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
            eq(PricingKey.HANDLING_FEE_SELF_SUPERVISION),
            any()
          );
        });

        it("uses HANDLING_FEE when the start time is before 1.3.2026", () -> {
          ZonedDateTime start = ZonedDateTime.parse("2026-02-28T00:00:00+02:00");
          ZonedDateTime end   = ZonedDateTime.parse("2026-08-01T00:00:00+02:00");

          Application app = new Application();
          ExcavationAnnouncement ea = new ExcavationAnnouncement();
          app.setExtension(ea);
          app.setStartTime(start);
          app.setEndTime(end);

          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          // area ja payment class ovat yhdentekeviä, koska daily fee ei ole stubattu → vain handling fee jää
          exc.addLocationPrice(getLocation(1, 10.0, "1", start, end), start);

          assertEquals(1234, exc.getPriceInCents());

          Mockito.verify(pricingDao).findValue(
            eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
            eq(PricingKey.HANDLING_FEE),
            any()
          );
        });

        it("returns correct explanation text for handling fee less than 6 months", () -> {
          ZonedDateTime start = ZonedDateTime.parse("2026-03-02T00:00:00+02:00");
          ZonedDateTime end   = ZonedDateTime.parse("2026-08-01T00:00:00+02:00"); // < 6 kk

          Application app = new Application();
          ExcavationAnnouncement ea = new ExcavationAnnouncement();
          app.setExtension(ea);
          app.setStartTime(start);
          app.setEndTime(end);

          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, List.of());

          List<String> explanation = exc.getHandlingFeeExplanation(ea);

          assertEquals(List.of("Alle kuusi (6) kuukautta kestävä työ"), explanation);
        });

        it("returns correct explanation text for handling fee greater than 6 months", () -> {
          ZonedDateTime start = ZonedDateTime.parse("2026-03-01T00:00:00+02:00");
          ZonedDateTime end   = ZonedDateTime.parse("2026-10-01T00:00:00+02:00"); // > 6 kk

          Application app = new Application();
          ExcavationAnnouncement ea = new ExcavationAnnouncement();
          app.setExtension(ea);
          app.setStartTime(start);
          app.setEndTime(end);

          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, List.of());

          List<String> explanation = exc.getHandlingFeeExplanation(ea);

          assertEquals(List.of("Vähintään kuusi (6) kuukautta kestävä työ"), explanation);
        });
      });
    });
  }
}
