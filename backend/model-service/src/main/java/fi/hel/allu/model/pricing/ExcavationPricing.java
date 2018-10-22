package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.WinterTime;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.PricingKey;
import fi.hel.allu.model.domain.util.Printable;
import fi.hel.allu.model.service.WinterTimeService;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ExcavationPricing extends Pricing {

  private final Application application;
  private final ExcavationAnnouncement extension;
  private final WinterTime winterTime;
  private final PricingExplanator pricingExplanator;
  private final PricingDao pricingDao;

  private static final String HANDLING_FEE_TEXT = "Ilmoituksen käsittely- ja työn valvontamaksu";
  private static final String AREA_FEE_TEXT = "Alueenkäyttömaksu, maksuluokka %s";

  private static final double SMALL_AREA_LIMIT = 60.0;
  private static final double LARGE_AREA_LIMIT = 120.0;

  public ExcavationPricing(Application application,
      WinterTimeService winterTimeService, PricingExplanator pricingExplanator, PricingDao pricingDao) {
    this.application = application;
    this.winterTime = winterTimeService.getWinterTime();
    this.extension = (ExcavationAnnouncement)application.getExtension();
    this.pricingExplanator = pricingExplanator;
    this.pricingDao = pricingDao;
    final int handlingFee = pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.HANDLING_FEE);
    setPriceInCents(handlingFee);
    addChargeBasisEntry(ChargeBasisTag.ExcavationAnnonuncementHandlingFee(), ChargeBasisUnit.PIECE, 1, handlingFee,
        HANDLING_FEE_TEXT, handlingFee);
  }

  @Override
  public void addLocationPrice(int locationKey, double locationArea, String paymentClass) {
    int dailyFee;
    if (locationArea < SMALL_AREA_LIMIT) {
      dailyFee = pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.SMALL_AREA_DAILY_FEE, paymentClass);
    } else if (locationArea > LARGE_AREA_LIMIT) {
      dailyFee = pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.LARGE_AREA_DAILY_FEE, paymentClass);
    } else {
      dailyFee = pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.MEDIUM_AREA_DAILY_FEE, paymentClass);
    }
    List<PricedPeriod> pricedPeriods = getPricedPeriods();
    if (pricedPeriods.size() > 0) {
      addChargeBasisEntryForPeriod(locationKey, paymentClass, dailyFee, pricedPeriods.get(0), ChargeBasisTag.ExcavationAnnouncementDailyFee(Integer.toString(locationKey)));
    }
    if (pricedPeriods.size() > 1) {
      addChargeBasisEntryForPeriod(locationKey, paymentClass, dailyFee, pricedPeriods.get(1), ChargeBasisTag.ExcavationAnnouncementDailyFeeAdd(Integer.toString(locationKey)));
    }
  }

  private void addChargeBasisEntryForPeriod(int locationKey, String paymentClass, int dailyFee,
      PricedPeriod invoicedPeriod, ChargeBasisTag tag) {
    String rowText = String.format(AREA_FEE_TEXT, paymentClass);
    int totalPrice = invoicedPeriod.getNumberOfDays() * dailyFee;
    addChargeBasisEntry(tag, ChargeBasisUnit.DAY, invoicedPeriod.getNumberOfDays(),
        dailyFee, rowText, totalPrice,
        pricingExplanator.getExplanationWithCustomPeriod(
          application, Printable.forDayPeriod(invoicedPeriod.start, invoicedPeriod.end)));
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
    ZonedDateTime endTime = extension.getWorkFinished() != null ? extension.getWorkFinished() : application.getEndTime();
    return endTime != null ? endTime.withZoneSameInstant(TimeUtil.HelsinkiZoneId) : null;
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
