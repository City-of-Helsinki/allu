package fi.hel.allu.model.pricing;

import java.time.LocalDateTime;
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
  private static final String HANDLING_FEE_LT_6_MONTHS_TEXT = "Alle kuusi (6) kuukautta kestävä työ";
  private static final String HANDLING_FEE_GE_6_MONTHS_TEXT = "Vähintään kuusi (6) kuukautta kestävä työ";

  private static final double SMALL_AREA_LIMIT = 60.0;
  private static final double LARGE_AREA_LIMIT = 120.0;

  private static final LocalDateTime POST_2025_PAYMENT_DATE = LocalDateTime.of(2025, 3, 1, 0, 0, 0, 0);
  private static final LocalDateTime POST_2026_PAYMENT_DATE = LocalDateTime.of(2026, 3, 1, 0, 0, 0, 0);


  public ExcavationPricing(
    Application application,
    WinterTimeService winterTimeService,
    PricingExplanator pricingExplanator,
    PricingDao pricingDao,
    List<InvoicingPeriod> invoicingPeriods
  ) {
    this.application = application;
    this.winterTime = winterTimeService.getWinterTime();
    this.extension = (ExcavationAnnouncement)application.getExtension();
    this.pricingExplanator = pricingExplanator;
    this.pricingDao = pricingDao;
    this.invoicingPeriods = invoicingPeriods;
    final int handlingFee = getHandlingFee(extension, application.getStartTime());
    setPriceInCents(handlingFee);
    addChargeBasisEntry(
      ChargeBasisTag.ExcavationAnnonuncementHandlingFee(),
      ChargeBasisUnit.PIECE,
      1,
      handlingFee,
      HANDLING_FEE_TEXT,
      handlingFee,
      getHandlingFeeExplanation(extension),
      getFirstOpenPeriodId()
    );
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
  public void addLocationPrice(Location location, ZonedDateTime startTime) {
    final String paymentClass = location.getEffectivePaymentTariff();
    final Integer locationKey = location.getLocationKey();
    final int dailyFee = getDailyFee(location.getEffectiveArea(), paymentClass, startTime);

    final List<PricedPeriod> pricedPeriods = getPricedPeriods();
    if (pricedPeriods.size() > 0) {
      addChargeBasisEntryForPeriod(paymentClass, dailyFee, pricedPeriods.get(0), ChargeBasisTag.ExcavationAnnouncementDailyFee(Integer.toString(locationKey)));
    }
    if (pricedPeriods.size() > 1) {
      addChargeBasisEntryForPeriod(paymentClass, dailyFee, pricedPeriods.get(1), ChargeBasisTag.ExcavationAnnouncementDailyFeeAdd(Integer.toString(locationKey)));
    }
  }

  protected boolean isPost2025ExcavationPayment(ZonedDateTime startTime) {
    return startTime != null && !startTime.withZoneSameInstant(TimeUtil.HelsinkiZoneId).isBefore(ZonedDateTime.of(POST_2025_PAYMENT_DATE, TimeUtil.HelsinkiZoneId));
  }

  /**
   * Determines whether a given excavation payment corresponds to a post-2026 timeline.
   * This is evaluated based on the start time of the excavation work in the
   * Helsinki time zone and compared against a predefined post-2026 payment date.
   *
   * Currently used only for processing handling fee data inference
   *
   * @param startTime The start date and time of the excavation work. Can be null.
   *                  If null, the method will return false.
   * @return true if the excavation work starts on or after the predefined post-2026
   *         payment date, false otherwise, or if the start time is null.
   */
  protected boolean isPost2026ExcavationPayment(ZonedDateTime startTime) {
    return startTime != null && !startTime.withZoneSameInstant(TimeUtil.HelsinkiZoneId).isBefore(ZonedDateTime.of(POST_2026_PAYMENT_DATE, TimeUtil.HelsinkiZoneId));
  }

  private int getDailyFee(double LocationArea, String paymentClass, ZonedDateTime startTime) {
    return isPost2025ExcavationPayment(startTime) ?
      getDailyFeePost2025(LocationArea, paymentClass, startTime) :
      getDailyFeePre2025(LocationArea, paymentClass, startTime);
  }

  private int getDailyFeePre2025(double locationArea, String paymentClass, ZonedDateTime startTime) {
    if (locationArea < SMALL_AREA_LIMIT) {
      return getPrice(PricingKey.SMALL_AREA_DAILY_FEE, paymentClass, startTime);
    } else if (locationArea > LARGE_AREA_LIMIT) {
      return getPrice(PricingKey.LARGE_AREA_DAILY_FEE, paymentClass, startTime);
    } else {
      return getPrice(PricingKey.MEDIUM_AREA_DAILY_FEE, paymentClass, startTime);
    }
  }

  private int getDailyFeePost2025(double locationArea, String paymentClass, ZonedDateTime startTime) {
    if (locationArea < 60.0) {
      return getPrice(PricingKey.LESS_THAN_60M2, paymentClass, startTime);
    } else if (locationArea <= 120.0) {
      return getPrice(PricingKey.FROM_60_TO_120M2, paymentClass, startTime);
    } else if (locationArea <= 250.0) {
      return getPrice(PricingKey.FROM_121_TO_250M2, paymentClass, startTime);
    } else if (locationArea <= 500.0) {
      return getPrice(PricingKey.FROM_251_TO_500M2, paymentClass, startTime);
    } else if (locationArea <= 1000.0) {
      return getPrice(PricingKey.FROM_501_TO_1000M2, paymentClass, startTime);
    } else {
      return getPrice(PricingKey.MORE_THAN_1000M2, paymentClass, startTime);
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

  private int getPrice(PricingKey key, String paymentClass, ZonedDateTime startTime) {
    if (paymentClass.equals(PriceUtil.UNDEFINED_PAYMENT_CLASS) || paymentClass.equalsIgnoreCase("h1")) {
      return 0;
    }
    return pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT, key, paymentClass, startTime);
  }

  /**
   * Calculates the handling fee for an excavation announcement based on a supervision type
   * and the duration of the work.
   *
   * @param excavationAnnouncement The excavation announcement object containing details
   *                               of the excavation, including whether self-supervision
   *                               is applied.
   * @param startTime              The start date and time of the excavation work, used for
   *                               pricing and duration determination.
   * @return The handling fee as an integer, based on the supervision type and the work
   *         duration thresholds (e.g., less than 6 months or at least 6 months).
   */
  private int getHandlingFee(ExcavationAnnouncement excavationAnnouncement, ZonedDateTime startTime) {
    if (Boolean.TRUE.equals(excavationAnnouncement.getSelfSupervision())) {
      return pricingDao.findValue(
        ApplicationType.EXCAVATION_ANNOUNCEMENT,
        PricingKey.HANDLING_FEE_SELF_SUPERVISION,
        startTime);
    }

    PricingKey key = resolveHandlingFeeKey(startTime);

    return pricingDao.findValue(
      ApplicationType.EXCAVATION_ANNOUNCEMENT,
      key,
      startTime);
  }

  /**
   * Provides a list of textual explanations for the handling fee of an excavation announcement.
   * The explanation is derived based on whether self-supervision is applied or the duration
   * of the excavation work.
   *
   * @param excavationAnnouncement The excavation announcement object containing details
   *                               such as the supervision type and other relevant information
   *                               for determining the handling fee.
   * @return A list of strings containing the handling fee explanation. If self-supervision
   *         is applied, a specific explanation is returned. Otherwise, the explanation is based
   *         on whether the work duration is less than 6 months, at least 6 months, or before 1.3.2026.
   */
  public List<String> getHandlingFeeExplanation(ExcavationAnnouncement excavationAnnouncement) {
    if (Boolean.TRUE.equals(excavationAnnouncement.getSelfSupervision())) {
      return List.of(SELF_SUPERVISION_TEXT);
    }

    PricingKey key = resolveHandlingFeeKey(application.getStartTime());

    if (key == PricingKey.HANDLING_FEE_GE_6_MONTHS) {
      return List.of(HANDLING_FEE_GE_6_MONTHS_TEXT);
    }
    if (key == PricingKey.HANDLING_FEE_LT_6_MONTHS) {
      return List.of(HANDLING_FEE_LT_6_MONTHS_TEXT);
    }

    // Vanha HANDLING_FEE (ennen 1.3.2026)
    return Collections.emptyList();
  }

  /**
   * Resolves the appropriate handling fee pricing key based on the duration of the application.
   * Determines if the application duration is at least six months or less and returns
   * the corresponding pricing key.
   *
   * @return The pricing key representing the handling fee:
   *         PricingKey.HANDLING_FEE_GE_6_MONTHS if the duration is six months or more,
   *         or PricingKey.HANDLING_FEE_LT_6_MONTHS if the duration is less than six months,
   *         or PricingKey.HANDLING_FEE if the duration is before 1.3.2026.
   */
  private PricingKey resolveHandlingFeeKey(ZonedDateTime startTime) {
    // Vanha hinnasto ennen 1.3.2026
    if (!isPost2026ExcavationPayment(startTime)) {
      return PricingKey.HANDLING_FEE;
    }

    // Uusi hinnasto 1.3.2026 alkaen
    ZonedDateTime start = application.getStartTime();
    ZonedDateTime end = getEndTimeForApplication();

    boolean isSixMonthsOrMore = isAtLeastSixMonths(start, end);

    return isSixMonthsOrMore
      ? PricingKey.HANDLING_FEE_GE_6_MONTHS
      : PricingKey.HANDLING_FEE_LT_6_MONTHS;
  }

  /**
   * Determines if the difference between the given start and end times is at least six months.
   *
   * @param start the starting date and time; must not be null.
   * @param end the ending date and time; must not be null.
   * @return true if the duration from the start time to the end time is six months or more,
   *         false if it is less than six months or if either parameter is null.
   */
  private boolean isAtLeastSixMonths(ZonedDateTime start, ZonedDateTime end) {
    if (start == null || end == null) {
      return false;
    }
    ZonedDateTime sixMonthsLater = start.plusMonths(6);
    return !end.isBefore(sixMonthsLater);
  }
}
