package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.WinterTime;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.service.WinterTimeService;

public class ExcavationPricing extends Pricing {


  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private final Application application;
  private final ExcavationAnnouncement extension;
  private final WinterTime winterTime;

  private static final String HANDLING_FEE_TEXT = "Ilmoituksen käsittely- ja työn valvontamaksu";
  private static final String AREA_FEE_TEXT = "Alueenkäyttömaksu, maksuluokka %d, %s - %s";

  private static final double SMALL_AREA_LIMIT = 60.0;
  private static final double LARGE_AREA_LIMIT = 120.0;
  private static final int HANDLING_FEE = 18000;
  private static final int SMALL_AREA_DAILY_FEE = 5000;
  private static final int MEDIUM_AREA_DAILY_FEE = 6500;
  private static final int LARGE_AREA_DAILY_FEE = 8000;

  public ExcavationPricing(Application application, WinterTimeService winterTimeService) {
    this.application = application;
    this.winterTime = winterTimeService.getWinterTime();
    this.extension = (ExcavationAnnouncement)application.getExtension();
    setPriceInCents(HANDLING_FEE);
    addChargeBasisEntry(ChargeBasisTag.ExcavationAnnonuncementHandlingFee(), ChargeBasisUnit.PIECE, 1, HANDLING_FEE,
        HANDLING_FEE_TEXT, HANDLING_FEE);
  }


  @Override
  public void addLocationPrice(int locationKey, double locationArea, int paymentClass) {
    int dailyFee;
    if (locationArea < SMALL_AREA_LIMIT) {
      dailyFee = SMALL_AREA_DAILY_FEE;
    } else if (locationArea > LARGE_AREA_LIMIT) {
      dailyFee = LARGE_AREA_DAILY_FEE;
    } else {
      dailyFee = MEDIUM_AREA_DAILY_FEE;
    }
    // Factor in the payment class.
    if (paymentClass == 2) {
      dailyFee /= 2;
    } else if (paymentClass == 3) {
      dailyFee /= 4;
    }
    List<PricedPeriod> pricedPeriods = getPricedPeriods();
    if (pricedPeriods.size() > 0) {
      addChargeBasisEntryForPeriod(locationKey, paymentClass, dailyFee, pricedPeriods.get(0), ChargeBasisTag.ExcavationAnnouncementDailyFee(Integer.toString(locationKey)));
    }
    if (pricedPeriods.size() > 1) {
      addChargeBasisEntryForPeriod(locationKey, paymentClass, dailyFee, pricedPeriods.get(1), ChargeBasisTag.ExcavationAnnouncementDailyFeeAdd(Integer.toString(locationKey)));
    }

  }

  protected void addChargeBasisEntryForPeriod(int locationKey, int paymentClass, int dailyFee,
      PricedPeriod invoicedPeriod, ChargeBasisTag tag) {
    String rowText = String.format(AREA_FEE_TEXT, paymentClass, FORMATTER.format(invoicedPeriod.start), FORMATTER.format(invoicedPeriod.end));
    int totalPrice = invoicedPeriod.getNumberOfDays() * dailyFee;
    addChargeBasisEntry(tag, ChargeBasisUnit.DAY, invoicedPeriod.getNumberOfDays(),
        dailyFee, rowText, totalPrice);
    setPriceInCents(totalPrice + getPriceInCents());
  }

  private List<PricedPeriod> getPricedPeriods() {
    List<PricedPeriod> result = new ArrayList<>();
    if (extension.getWinterTimeOperation() != null) {
      addWinterTimePeriod(result);
      addSummerPeriodForWinterTimeOperation(result);
    } else {
      addApplicationPeriod(result);
    }
    return result;
  }

  // If winter time operation is finished after "summer end" adds additional
  // invoiced period between summer end and work finished / application end time
  private void addSummerPeriodForWinterTimeOperation(List<PricedPeriod> result) {
    ZonedDateTime winterTimeEnd = winterTime.getWinterTimeEnd(extension.getWinterTimeOperation()).atStartOfDay(TimeUtil.HelsinkiZoneId);
    ZonedDateTime applicationEnd = getEndTimeForApplication().truncatedTo(ChronoUnit.DAYS);
    if (applicationEnd != null && applicationEnd.isAfter(winterTimeEnd)) {
      result.add(new PricedPeriod(winterTimeEnd.plusDays(1), applicationEnd));
    }
  }

  private void addApplicationPeriod(List<PricedPeriod> periods) {
    ZonedDateTime end = getEndTimeForApplication();
    if (end != null) {
      periods.add(new PricedPeriod(application.getStartTime(), end.withZoneSameInstant(TimeUtil.HelsinkiZoneId)));
    }
  }

  private ZonedDateTime getEndTimeForApplication() {
    return extension.getWorkFinished() != null ? extension.getWorkFinished() : application.getEndTime();
  }

  private void addWinterTimePeriod(List<PricedPeriod> periods) {
    ZonedDateTime endTime;
    ZonedDateTime winterTimeOperation = extension.getWinterTimeOperation().withZoneSameInstant(TimeUtil.HelsinkiZoneId);
    if (!winterTime.isInWinterTime(winterTimeOperation)) {
      // Operational condition before winter start -> charged until date before winter start or work finished
      endTime = winterTime.getWinterTimeStart(winterTimeOperation).atStartOfDay(TimeUtil.HelsinkiZoneId).minusDays(1);
      if (extension.getWorkFinished() != null && extension.getWorkFinished().isBefore(endTime)) {
        // Work finished before winter start
        endTime = extension.getWorkFinished().withZoneSameInstant(TimeUtil.HelsinkiZoneId);
      }
    } else {
      endTime = winterTimeOperation;
    }
    periods.add(new PricedPeriod(application.getStartTime(), endTime));
  }

  private static class PricedPeriod {
    ZonedDateTime start;
    ZonedDateTime end;
    public PricedPeriod(ZonedDateTime start, ZonedDateTime end) {
      this.start = start;
      this.end = end;
    }
    int getNumberOfDays() {
      return (int) CalendarUtil.startingUnitsBetween(start, end, ChronoUnit.DAYS);
    }
  }
}
