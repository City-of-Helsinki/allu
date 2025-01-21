package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.util.PriceUtil;
import fi.hel.allu.model.domain.util.Printable;
import fi.hel.allu.model.service.WinterTimeService;

public class ExcavationPricing extends Pricing {

  private final Application application;
  private final ExcavationAnnouncement extension;
  private final List<InvoicingPeriod> invoicingPeriods;
  private final AnnualTimePeriod winterTime;
  private final PricingExplanator pricingExplanator;
  private final PricingDao pricingDao;

  private static final String HANDLING_FEE_TEXT = "Ilmoituksen käsittely- ja työn valvontamaksu";
  private static final String AREA_FEE_TEXT = "Alueenkäyttömaksu, maksuluokka %s";
  private static final String SELF_SUPERVISION_TEXT = "Omavalvonta";

  public ExcavationPricing(Application application,
      WinterTimeService winterTimeService, PricingExplanator pricingExplanator, PricingDao pricingDao,
      List<InvoicingPeriod> invoicingPeriods) {
    this.application = application;
    this.winterTime = winterTimeService.getWinterTime();
    this.extension = (ExcavationAnnouncement)application.getExtension();
    this.pricingExplanator = pricingExplanator;
    this.pricingDao = pricingDao;
    this.invoicingPeriods = invoicingPeriods;
    final int handlingFee = getHandlingFee(extension);
    setPriceInCents(handlingFee);
    addChargeBasisEntry(ChargeBasisTag.ExcavationAnnonuncementHandlingFee(), ChargeBasisUnit.PIECE, 1, handlingFee,
        HANDLING_FEE_TEXT, handlingFee, getHandlingFeeExplanation(extension), getFirstOpenPeriodId());
  }

  private Integer getFirstOpenPeriodId() {
    return invoicingPeriods.stream()
      .filter(p -> !p.isClosed()).sorted()
      .findFirst()
      .map(InvoicingPeriod::getId)
      .orElse(null);
  }

  private Integer getWorkFinishedPeriodId() {
    return invoicingPeriods.stream()
        .filter(p -> p.getInvoicableStatus() == StatusType.FINISHED)
        .findFirst()
        .map(InvoicingPeriod::getId)
        .orElse(null);
  }

  @Override
  public void addLocationPrice(Location location) {
    final String paymentClass = location.getEffectivePaymentTariff();
    final Integer locationKey = location.getLocationKey();
    final int dailyFee = getDailyFee(location.getEffectiveArea(), paymentClass);

    final List<PricedPeriod> pricedPeriods = getPricedPeriods();
    if (pricedPeriods.size() > 0) {
      addChargeBasisEntryForPeriod(paymentClass, dailyFee, pricedPeriods.get(0), ChargeBasisTag.ExcavationAnnouncementDailyFee(Integer.toString(locationKey)));
    }
    if (pricedPeriods.size() > 1) {
      addChargeBasisEntryForPeriod(paymentClass, dailyFee, pricedPeriods.get(1), ChargeBasisTag.ExcavationAnnouncementDailyFeeAdd(Integer.toString(locationKey)));
    }
  }

  private int getDailyFee(double locationArea, String paymentClass) {
    if (locationArea <= 60.0) {
      return getPrice(PricingKey.LESS_THAN_60M2, paymentClass);
    } else if (locationArea <= 120.0) {
      return getPrice(PricingKey.FROM_60_TO_120M2, paymentClass);
    } else if (locationArea <= 250.0) {
      return getPrice(PricingKey.FROM_121_TO_250M2, paymentClass);
    } else if (locationArea <= 500.0) {
      return getPrice(PricingKey.FROM_251_TO_500M2, paymentClass);
    } else if (locationArea <= 1000.0) {
      return getPrice(PricingKey.FROM_501_TO_1000M2, paymentClass);
    } else {
      return getPrice(PricingKey.MORE_THAN_1000M2, paymentClass);
    }
  }

  private void addChargeBasisEntryForPeriod(String paymentClass, int dailyFee,
      PricedPeriod invoicedPeriod, ChargeBasisTag tag) {
    String rowText = String.format(AREA_FEE_TEXT, PriceUtil.getPaymentClassText(paymentClass));
    int totalPrice = invoicedPeriod.getNumberOfDays() * dailyFee;
    addChargeBasisEntry(tag, ChargeBasisUnit.DAY, invoicedPeriod.getNumberOfDays(),
        dailyFee, rowText, totalPrice,
        pricingExplanator.getExplanationWithCustomPeriod(
          application, Printable.forDayPeriod(invoicedPeriod.start, invoicedPeriod.end)), invoicedPeriod.invoicingPeriodId);
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
    ZonedDateTime winterTimeEnd = winterTime.getAnnualPeriodEnd(extension.getWinterTimeOperation()).atStartOfDay(TimeUtil.HelsinkiZoneId);
    ZonedDateTime applicationEnd = getEndTimeForApplication().truncatedTo(ChronoUnit.DAYS);
    if (applicationEnd != null && applicationEnd.isAfter(winterTimeEnd)) {
      result.add(new PricedPeriod(winterTimeEnd.plusDays(1), applicationEnd, getWorkFinishedPeriodId()));
    }
  }

  private void addApplicationPeriod(List<PricedPeriod> periods) {
    ZonedDateTime end = getEndTimeForApplication();
    if (end != null) {
      periods.add(new PricedPeriod(application.getStartTime(), end.withZoneSameInstant(TimeUtil.HelsinkiZoneId), getWorkFinishedPeriodId()));
    }
  }

  private ZonedDateTime getEndTimeForApplication() {
    ZonedDateTime endTime = extension.getWorkFinished() != null ? extension.getWorkFinished() : application.getEndTime();
    return endTime != null ? endTime.withZoneSameInstant(TimeUtil.HelsinkiZoneId) : null;
  }

  private void addWinterTimePeriod(List<PricedPeriod> periods) {
    ZonedDateTime endTime;
    ZonedDateTime winterTimeOperation = extension.getWinterTimeOperation().withZoneSameInstant(TimeUtil.HelsinkiZoneId);
    if (!winterTime.isInAnnualPeriod(winterTimeOperation)) {
      // Operational condition before winter start -> charged until date before winter start or work finished
      endTime = winterTime.getAnnualPeriodStart(winterTimeOperation).atStartOfDay(TimeUtil.HelsinkiZoneId).minusDays(1);
      if (extension.getWorkFinished() != null && extension.getWorkFinished().isBefore(endTime)) {
        // Work finished before winter start
        endTime = extension.getWorkFinished().withZoneSameInstant(TimeUtil.HelsinkiZoneId);
      }
    } else {
      endTime = winterTimeOperation;
    }
    periods.add(new PricedPeriod(application.getStartTime(), endTime, getFirstOpenPeriodId()));
  }

  private static class PricedPeriod {
    ZonedDateTime start;
    ZonedDateTime end;
    Integer invoicingPeriodId;

    public PricedPeriod(ZonedDateTime start, ZonedDateTime end, Integer invoicingPeriodId) {
      this.start = start;
      this.end = end;
      this.invoicingPeriodId = invoicingPeriodId;
    }
    int getNumberOfDays() {
      return (int) CalendarUtil.startingUnitsBetween(start, end, ChronoUnit.DAYS);
    }
  }

  private int getPrice(PricingKey key, String paymentClass) {
    if (paymentClass.equals(PriceUtil.UNDEFINED_PAYMENT_CLASS) || paymentClass.equalsIgnoreCase("h1")) {
      return 0;
    }
    return pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, key, paymentClass);
  }

  private int getHandlingFee(ExcavationAnnouncement excavationAnnouncement) {
    if (Boolean.TRUE.equals(excavationAnnouncement.getSelfSupervision())) {
      return pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.HANDLING_FEE_SELF_SUPERVISION);
    } else {
      return pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, PricingKey.HANDLING_FEE);
    }
  }

  private List<String> getHandlingFeeExplanation(ExcavationAnnouncement excavationAnnouncement) {
    if (Boolean.TRUE.equals(excavationAnnouncement.getSelfSupervision())) {
      return Arrays.asList(SELF_SUPERVISION_TEXT);
    } else {
      return Collections.emptyList();
    }
  }
}
