package fi.hel.allu.model.pricing;

import static com.greghaskins.spectrum.dsl.specification.Specification.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.greghaskins.spectrum.Spectrum;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.domain.PricingKey;
import fi.hel.allu.model.service.WinterTimeService;

/**
 * Pricing of excavation announcements using the calendar-month invoicing model.
 */
@RunWith(Spectrum.class)
public class ExcavationMonthlyPricingSpec extends LocationBasedPricing {

  private static final int HANDLING_FEE_FIRST_PART = 24000;
  private static final int HANDLING_FEE_SECOND_PART = 16000;
  private static final int DAILY_FEE = 1000;

  WinterTimeService winterTimeService;
  PricingExplanator pricingExplanator;
  PricingDao pricingDao;
  ExcavationPricing exc;
  Application app;
  List<InvoicingPeriod> periods;

  {
    describe("Monthly invoicing of excavation announcements", () -> {
      beforeEach(() -> {
        winterTimeService = Mockito.mock(WinterTimeService.class);
        pricingExplanator = Mockito.mock(PricingExplanator.class);
        pricingDao = Mockito.mock(PricingDao.class);
        Mockito.when(winterTimeService.getWinterTime())
            .thenReturn(new AnnualTimePeriod(LocalDate.of(2026, 12, 1), LocalDate.of(2027, 5, 14)));
        Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
            eq(PricingKey.HANDLING_FEE_LT_6_MONTHS), any())).thenReturn(HANDLING_FEE_FIRST_PART);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
            eq(PricingKey.HANDLING_FEE_GE_6_MONTHS), any()))
            .thenReturn(HANDLING_FEE_FIRST_PART + HANDLING_FEE_SECOND_PART);
        Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
            eq(PricingKey.LESS_THAN_60M2), eq("2"), any())).thenReturn(DAILY_FEE);
        periods = new ArrayList<>();
      });

      context("work without operational condition date", () -> {
        beforeEach(() -> {
          createApplication("2026-11-11", "2027-06-06", null);
          periods = buildPeriods("2026-11-11", "2027-06-06", (String) null);
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          exc.addLocationPrice(getLocation(1, 10.0, "2", app.getStartTime(), app.getEndTime()),
              app.getStartTime());
        });

        it("creates one area fee row per calendar month", () -> {
          assertEquals(8, areaFeeEntries().size());
        });

        it("assigns each area fee row to its own invoicing period", () -> {
          assertEquals(8, areaFeeEntries().stream()
              .map(ChargeBasisEntry::getInvoicingPeriodId).distinct().count());
        });

        it("creates a unique tag per period", () -> {
          assertEquals(8, areaFeeEntries().stream()
              .map(ChargeBasisEntry::getTag).distinct().count());
        });

        it("charges the daily fee for the days of each month", () -> {
          int totalDays = areaFeeEntries().stream().mapToInt(e -> (int) e.getQuantity()).sum();
          assertEquals(208, totalDays); // 11.11.2026 - 6.6.2027
        });

        it("sums monthly rows to the whole period price", () -> {
          int areaTotal = areaFeeEntries().stream().mapToInt(ChargeBasisEntry::getNetPrice).sum();
          assertEquals(208 * DAILY_FEE, areaTotal);
        });

        it("charges the first part of the handling fee on the first period", () -> {
          ChargeBasisEntry handlingFee = entryByTag("EAHF");
          assertEquals(HANDLING_FEE_FIRST_PART, handlingFee.getNetPrice());
          assertEquals(firstPeriodId(), handlingFee.getInvoicingPeriodId());
        });

        it("charges the second part of the handling fee on the six month period", () -> {
          ChargeBasisEntry additional = entryByTag("EAHFADD");
          assertEquals(HANDLING_FEE_SECOND_PART, additional.getNetPrice());
          assertEquals(periodContaining("2027-05-11").getId(), additional.getInvoicingPeriodId());
        });

        it("charges 400 EUR handling fee in total", () -> {
          assertEquals(HANDLING_FEE_FIRST_PART + HANDLING_FEE_SECOND_PART,
              entryByTag("EAHF").getNetPrice() + entryByTag("EAHFADD").getNetPrice());
        });
      });

      context("winter work with operational condition date", () -> {
        beforeEach(() -> {
          createApplication("2026-11-11", "2027-06-06", "2027-02-02");
          periods = buildPeriods("2026-11-11", "2027-06-06", "2027-02-02");
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          exc.addLocationPrice(getLocation(1, 10.0, "2", app.getStartTime(), app.getEndTime()),
              app.getStartTime());
        });

        it("charges operational condition months with the first area fee tag", () -> {
          List<ChargeBasisEntry> operational = areaFeeEntries().stream()
              .filter(e -> e.getTag().startsWith("EADF#"))
              .collect(Collectors.toList());
          assertEquals(4, operational.size());
          assertEquals("Alueenkäyttömaksu (11.11.2026-30.11.2026)", operational.get(0).getText());
          assertEquals("Alueenkäyttömaksu (1.2.2027-2.2.2027)",
              operational.get(operational.size() - 1).getText());
        });

        it("charges work finished months from the operational condition date onwards", () -> {
          List<ChargeBasisEntry> workFinished = areaFeeEntries().stream()
              .filter(e -> e.getTag().startsWith("EADFADD#"))
              .collect(Collectors.toList());
          // February remainder plus March, April, May and June
          assertEquals(5, workFinished.size());
          assertEquals("Alueenkäyttömaksu (3.2.2027-28.2.2027)", workFinished.get(0).getText());
        });

        it("assigns area fees only to periods of the matching phase", () -> {
          areaFeeEntries().forEach(entry -> {
            InvoicingPeriod period = periodById(entry.getInvoicingPeriodId());
            boolean isWorkFinishedTag = entry.getTag().startsWith("EADFADD#");
            assertEquals(isWorkFinishedTag, period.getInvoicableStatus() == StatusType.FINISHED);
          });
        });

        it("charges the second handling fee part on the period containing the six month date", () -> {
          ChargeBasisEntry additional = entryByTag("EAHFADD");
          assertEquals(periodContaining("2027-05-11").getId(), additional.getInvoicingPeriodId());
        });
      });

      context("work shorter than six months", () -> {
        beforeEach(() -> {
          createApplication("2026-04-01", "2026-07-31", null);
          periods = buildPeriods("2026-04-01", "2026-07-31", (String) null);
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          exc.addLocationPrice(getLocation(1, 10.0, "2", app.getStartTime(), app.getEndTime()),
              app.getStartTime());
        });

        it("charges only the first handling fee part", () -> {
          assertEquals(HANDLING_FEE_FIRST_PART, entryByTag("EAHF").getNetPrice());
          assertTrue(entriesByTag("EAHFADD").isEmpty());
        });
      });

      context("work of exactly six months", () -> {
        beforeEach(() -> {
          createApplication("2026-04-01", "2026-10-01", null);
          periods = buildPeriods("2026-04-01", "2026-10-01", (String) null);
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          exc.addLocationPrice(getLocation(1, 10.0, "2", app.getStartTime(), app.getEndTime()),
              app.getStartTime());
        });

        it("charges both handling fee parts", () -> {
          assertEquals(HANDLING_FEE_FIRST_PART, entryByTag("EAHF").getNetPrice());
          assertEquals(HANDLING_FEE_SECOND_PART, entryByTag("EAHFADD").getNetPrice());
        });
      });

      context("no area usage fee", () -> {
        beforeEach(() -> {
          createApplication("2026-11-11", "2027-06-06", null);
          ((ExcavationAnnouncement) app.getExtension()).setNoAreaUsageFee(true);
          periods = buildPeriods("2026-11-11", "2027-06-06", (String) null);
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao, periods);
          exc.addLocationPrice(getLocation(1, 10.0, "2", app.getStartTime(), app.getEndTime()),
              app.getStartTime());
        });

        it("creates no area usage fee rows", () -> {
          assertTrue(areaFeeEntries().isEmpty());
        });

        it("still charges both handling fee parts", () -> {
          assertEquals(HANDLING_FEE_FIRST_PART, entryByTag("EAHF").getNetPrice());
          assertEquals(HANDLING_FEE_SECOND_PART, entryByTag("EAHFADD").getNetPrice());
        });
      });

      context("application starting before 1.3.2026", () -> {
        beforeEach(() -> {
          Mockito.when(pricingDao.findValue(eq(ApplicationType.EXCAVATION_ANNOUNCEMENT),
              eq(PricingKey.HANDLING_FEE), any())).thenReturn(18000);
          createApplication("2026-02-01", "2026-06-30", null);
          exc = new ExcavationPricing(app, winterTimeService, pricingExplanator, pricingDao,
              List.of(new InvoicingPeriod(1, StatusType.FINISHED)));
          exc.addLocationPrice(getLocation(1, 10.0, "2", app.getStartTime(), app.getEndTime()),
              app.getStartTime());
        });

        it("keeps the legacy single handling fee", () -> {
          assertEquals(18000, entryByTag("EAHF").getNetPrice());
          assertTrue(entriesByTag("EAHFADD").isEmpty());
        });

        it("keeps a single area fee row", () -> {
          assertEquals(1, areaFeeEntries().size());
        });
      });
    });
  }

  private void createApplication(String start, String end, String operationalCondition) {
    app = new Application();
    app.setId(1);
    ExcavationAnnouncement extension = new ExcavationAnnouncement();
    if (operationalCondition != null) {
      extension.setWinterTimeOperation(date(operationalCondition));
    }
    app.setExtension(extension);
    app.setStartTime(date(start));
    app.setEndTime(date(end));
  }

  /**
   * Builds dated monthly periods the same way {@code ExcavationMonthlyPeriodCalculator} does:
   * operational condition up to the operational condition date, and work finished from the day
   * after it, split at calendar month boundaries.
   */
  private List<InvoicingPeriod> buildPeriods(String start, String end, String operationalCondition) {
    List<InvoicingPeriod> result = new ArrayList<>();
    int id = 1;
    if (operationalCondition == null) {
      result.addAll(months(start, end, StatusType.FINISHED, id));
    } else {
      List<InvoicingPeriod> operational = months(start, operationalCondition, StatusType.OPERATIONAL_CONDITION, id);
      result.addAll(operational);
      id += operational.size();
      LocalDate afterOperational = LocalDate.parse(operationalCondition).plusDays(1);
      result.addAll(months(afterOperational.toString(), end, StatusType.FINISHED, id));
    }
    return result;
  }

  private List<InvoicingPeriod> months(String start, String end, StatusType status, int firstId) {
    List<InvoicingPeriod> result = new ArrayList<>();
    LocalDate current = LocalDate.parse(start);
    LocalDate last = LocalDate.parse(end);
    int id = firstId;
    while (!current.isAfter(last)) {
      LocalDate monthEnd = current.withDayOfMonth(current.lengthOfMonth());
      LocalDate periodEnd = monthEnd.isBefore(last) ? monthEnd : last;
      InvoicingPeriod period = new InvoicingPeriod(1,
          current.atStartOfDay(TimeUtil.HelsinkiZoneId),
          periodEnd.atStartOfDay(TimeUtil.HelsinkiZoneId));
      period.setId(id++);
      period.setInvoicableStatus(status);
      result.add(period);
      current = periodEnd.plusDays(1);
    }
    return result;
  }

  private List<ChargeBasisEntry> areaFeeEntries() {
    return exc.getChargeBasisEntries().stream()
        .filter(e -> e.getTag() != null && (e.getTag().startsWith("EADF#") || e.getTag().startsWith("EADFADD#")))
        .sorted(Comparator.comparing(e -> periodById(e.getInvoicingPeriodId()).getStartTime()))
        .collect(Collectors.toList());
  }

  private List<ChargeBasisEntry> entriesByTag(String tag) {
    return exc.getChargeBasisEntries().stream()
        .filter(e -> tag.equals(e.getTag()))
        .collect(Collectors.toList());
  }

  private ChargeBasisEntry entryByTag(String tag) {
    return entriesByTag(tag).stream().findFirst()
        .orElseThrow(() -> new AssertionError("No charge basis entry with tag " + tag + ", entries: "
            + exc.getChargeBasisEntries().stream().map(ChargeBasisEntry::getTag).collect(Collectors.toList())));
  }

  private Integer firstPeriodId() {
    return periods.stream().min(Comparator.comparing(InvoicingPeriod::getStartTime))
        .map(InvoicingPeriod::getId).orElseThrow();
  }

  private InvoicingPeriod periodById(Integer id) {
    return periods.stream().filter(p -> p.getId().equals(id)).findFirst()
        .orElseThrow(() -> new AssertionError("No period with id " + id));
  }

  private InvoicingPeriod periodContaining(String day) {
    LocalDate date = LocalDate.parse(day);
    return periods.stream()
        .filter(p -> !p.getStartTime().toLocalDate().isAfter(date)
            && !p.getEndTime().toLocalDate().isBefore(date))
        .findFirst()
        .orElseThrow(() -> new AssertionError("No period contains " + day + ", periods: " + periods.stream()
            .map(p -> p.getStartTime().toLocalDate() + ".." + p.getEndTime().toLocalDate())
            .collect(Collectors.toList())));
  }

  private ZonedDateTime date(String day) {
    return LocalDate.parse(day).atStartOfDay(TimeUtil.HelsinkiZoneId);
  }
}
