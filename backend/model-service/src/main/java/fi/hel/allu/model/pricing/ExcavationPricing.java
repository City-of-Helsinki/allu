package fi.hel.allu.model.pricing;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import fi.hel.allu.model.service.ExcavationMonthlyPeriodCalculator;
import fi.hel.allu.model.service.WinterTimeService;

public class ExcavationPricing extends Pricing {

  private final Application application;
  private final ExcavationAnnouncement extension;
  private final List<InvoicingPeriod> invoicingPeriods;
  private final AnnualTimePeriod winterTime;
  private final PricingExplanator pricingExplanator;
  private final PricingDao pricingDao;

  private static final String HANDLING_FEE_TEXT = "Ilmoituksen käsittely- ja työn valvontamaksu";
  private static final String AREA_FEE_TEXT = "Alueenkäyttömaksu (%s-%s)";
  private static final String AREA_FEE_EXPLANATION = "Maksuluokka %s, %sm2";
  private static final String SELF_SUPERVISION_TEXT = "Omavalvonta";
  private static final String HANDLING_FEE_LT_6_MONTHS_TEXT = "Alle kuusi (6) kuukautta kestävä työ";
  private static final String HANDLING_FEE_GE_6_MONTHS_TEXT = "Vähintään kuusi (6) kuukautta kestävä työ";

  private static final double SMALL_AREA_LIMIT = 60.0;
  private static final double LARGE_AREA_LIMIT = 120.0;

  private static final LocalDateTime POST_2025_PAYMENT_DATE = LocalDateTime.of(2025, 3, 1, 0, 0, 0, 0);
  private static final LocalDateTime POST_2026_PAYMENT_DATE = LocalDateTime.of(2026, 3, 1, 0, 0, 0, 0);

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d.M.uuuu");


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
    if (usesMonthlyPeriods()) {
      addMonthlyHandlingFeeEntries();
    } else {
      addLegacyHandlingFeeEntry();
    }
  }

  /**
   * Returns true when the application is priced with calendar-month invoicing periods.
   *
   * <p>The monthly model applies to applications starting on or after 1.3.2026 that have dated
   * invoicing periods. Legacy applications keep status-only periods, which have no dates.</p>
   */
  private boolean usesMonthlyPeriods() {
    return isPost2026ExcavationPayment(application.getStartTime())
        && invoicingPeriods.stream().anyMatch(period -> period.getStartTime() != null);
  }

  private void addLegacyHandlingFeeEntry() {
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

  /**
   * Adds the two parts of the handling fee.
   *
   * <p>The first part is always charged on the first period. The second part is charged on the
   * period where the work reaches six months, and only when the work lasts at least six months.
   * Self supervision does not affect the fee in the monthly model.</p>
   */
  private void addMonthlyHandlingFeeEntries() {
    final int firstPartFee = pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT,
        PricingKey.HANDLING_FEE_LT_6_MONTHS, application.getStartTime());
    addChargeBasisEntry(
      ChargeBasisTag.ExcavationAnnonuncementHandlingFee(),
      ChargeBasisUnit.PIECE,
      1,
      firstPartFee,
      HANDLING_FEE_TEXT,
      firstPartFee,
      List.of(HANDLING_FEE_LT_6_MONTHS_TEXT),
      getFirstOpenPeriodId()
    );
    setPriceInCents(firstPartFee);

    if (!isAtLeastSixMonths(application.getStartTime(), getEndTimeForApplication())) {
      return;
    }
    final int secondPartFee = getSecondHandlingFeePart();
    if (secondPartFee <= 0) {
      return;
    }
    addChargeBasisEntry(
      ChargeBasisTag.ExcavationAnnouncementHandlingFeeAdditional(),
      ChargeBasisUnit.PIECE,
      1,
      secondPartFee,
      HANDLING_FEE_TEXT,
      secondPartFee,
      List.of(HANDLING_FEE_GE_6_MONTHS_TEXT),
      getAdditionalHandlingFeePeriodId()
    );
    setPriceInCents(secondPartFee + getPriceInCents());
  }

  /**
   * Returns the second part of the handling fee, derived from the pricing table so that the two
   * parts together equal the price of work lasting six months or more.
   */
  private int getSecondHandlingFeePart() {
    int sixMonthsFee = pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT,
        PricingKey.HANDLING_FEE_GE_6_MONTHS, application.getStartTime());
    int lessThanSixMonthsFee = pricingDao.findValue(ApplicationType.EXCAVATION_ANNOUNCEMENT,
        PricingKey.HANDLING_FEE_LT_6_MONTHS, application.getStartTime());
    return sixMonthsFee - lessThanSixMonthsFee;
  }

  /**
   * Returns the id of the period containing the date six months after the application start.
   */
  private Integer getAdditionalHandlingFeePeriodId() {
    ZonedDateTime sixMonthsDate = TimeUtil.homeTime(application.getStartTime()).plusMonths(6);
    ZonedDateTime end = getEndTimeForApplication();
    if (end == null || sixMonthsDate.isAfter(end)) {
      return null;
    }
    return invoicingPeriods.stream()
        .filter(period -> period.getStartTime() != null && period.getEndTime() != null)
        .filter(period -> !TimeUtil.isDateBefore(sixMonthsDate, period.getStartTime()))
        .filter(period -> !TimeUtil.isDateAfter(sixMonthsDate, period.getEndTime()))
        .findFirst()
        .map(InvoicingPeriod::getId)
        .orElse(null);
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

    if (usesMonthlyPeriods()) {
      addMonthlyLocationPrice(location, paymentClass, locationKey, dailyFee);
      return;
    }

    final List<PricedPeriod> pricedPeriods = getPricedPeriods();
    if (pricedPeriods.size() > 0) {
      addChargeBasisEntryForPeriod(paymentClass, location.getEffectiveArea(), dailyFee, pricedPeriods.get(0), ChargeBasisTag.ExcavationAnnouncementDailyFee(Integer.toString(locationKey)));
    }
    if (pricedPeriods.size() > 1) {
      addChargeBasisEntryForPeriod(paymentClass, location.getEffectiveArea(), dailyFee, pricedPeriods.get(1), ChargeBasisTag.ExcavationAnnouncementDailyFeeAdd(Integer.toString(locationKey)));
    }
  }

  /**
   * Adds one area usage fee entry per dated invoicing period the location is charged for.
   *
   * <p>Phase spans are split at calendar-month boundaries by matching them against the dated
   * invoicing periods, so every entry belongs to exactly one month and carries the tag of its
   * period. When no area usage fee is charged for the application, no entries are created.</p>
   */
  private void addMonthlyLocationPrice(Location location, String paymentClass, Integer locationKey, int dailyFee) {
    if (Boolean.TRUE.equals(extension.getNoAreaUsageFee())) {
      return;
    }
    for (MonthlyPricedPeriod pricedPeriod : getMonthlyPricedPeriods()) {
      if (pricedPeriod.getNumberOfDays() <= 0) {
        continue;
      }
      ChargeBasisTag tag = pricedPeriod.isWorkFinished()
          ? ChargeBasisTag.ExcavationAnnouncementDailyFeeAdd(Integer.toString(locationKey), pricedPeriod.invoicingPeriodId)
          : ChargeBasisTag.ExcavationAnnouncementDailyFee(Integer.toString(locationKey), pricedPeriod.invoicingPeriodId);
      addChargeBasisEntryForPeriod(paymentClass, location.getEffectiveArea(), dailyFee,
          pricedPeriod.start, pricedPeriod.end, pricedPeriod.invoicingPeriodId, tag);
    }
  }

  /**
   * Splits the phase spans into the dated invoicing periods the application has.
   */
  private List<MonthlyPricedPeriod> getMonthlyPricedPeriods() {
    List<MonthlyPricedPeriod> result = new ArrayList<>();
    for (PricedPeriod span : getPricedPeriods()) {
      if (span.start == null || span.end == null) {
        continue;
      }
      for (InvoicingPeriod period : invoicingPeriods) {
        if (period.getStartTime() == null || period.getEndTime() == null) {
          continue;
        }
        ZonedDateTime overlapStart = TimeUtil.homeTime(span.start).isAfter(period.getStartTime())
            ? TimeUtil.homeTime(span.start) : period.getStartTime();
        ZonedDateTime overlapEnd = TimeUtil.homeTime(span.end).isBefore(period.getEndTime())
            ? TimeUtil.homeTime(span.end) : period.getEndTime();
        if (TimeUtil.isDateBefore(overlapEnd, overlapStart)) {
          continue;
        }
        result.add(new MonthlyPricedPeriod(overlapStart, overlapEnd, period.getId(),
            period.getInvoicableStatus() == StatusType.FINISHED));
      }
    }
    return result;
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

  private void addChargeBasisEntryForPeriod(String paymentClass, double area, int dailyFee,
      PricedPeriod invoicedPeriod, ChargeBasisTag tag) {
    addChargeBasisEntryForPeriod(paymentClass, area, dailyFee, invoicedPeriod.start, invoicedPeriod.end,
        invoicedPeriod.invoicingPeriodId, tag);
  }

  private void addChargeBasisEntryForPeriod(String paymentClass, double area, int dailyFee,
      ZonedDateTime periodStart, ZonedDateTime periodEnd, Integer invoicingPeriodId, ChargeBasisTag tag) {
    String rowText = String.format(AREA_FEE_TEXT, formatDate(periodStart), formatDate(periodEnd));
    List<String> explanation = List.of(String.format(AREA_FEE_EXPLANATION,
        PriceUtil.getPaymentClassText(paymentClass), (int) Math.ceil(area)));
    int numberOfDays = (int) CalendarUtil.startingUnitsBetween(periodStart, periodEnd, ChronoUnit.DAYS);
    int totalPrice = numberOfDays * dailyFee;
    addChargeBasisEntry(tag, ChargeBasisUnit.DAY, numberOfDays,
        dailyFee, rowText, totalPrice, explanation, invoicingPeriodId);
    setPriceInCents(totalPrice + getPriceInCents());
  }

  private String formatDate(ZonedDateTime date) {
    return date.withZoneSameInstant(TimeUtil.HelsinkiZoneId).format(DATE_FORMATTER);
  }

  private List<PricedPeriod> getPricedPeriods() {
    List<PricedPeriod> result = new ArrayList<>();
    if (usesMonthlyPeriods()) {
      addMonthlyPhaseSpans(result);
    } else if (extension.getWinterTimeOperation() != null) {
      addWinterTimePeriod(result);
      addSummerPeriodForWinterTimeOperation(result);
    } else {
      addApplicationPeriod(result);
    }
    return result;
  }

  /**
   * Adds the operational condition and work finished spans used by the monthly model.
   *
   * <p>These follow the same rules as the invoicing period calculation: the operational condition
   * phase ends on the operational condition date (or the day before winter time start when the date
   * is in summer), and the work finished phase starts on the following day so that no days are left
   * without a charge.</p>
   */
  private void addMonthlyPhaseSpans(List<PricedPeriod> result) {
    ZonedDateTime end = getEndTimeForApplication();
    if (end == null) {
      return;
    }
    ZonedDateTime winterTimeOperation = extension.getWinterTimeOperation();
    if (winterTimeOperation == null) {
      result.add(new PricedPeriod(application.getStartTime(), end, getWorkFinishedPeriodId()));
      return;
    }
    ZonedDateTime operationalConditionEnd = TimeUtil.homeTime(
        ExcavationMonthlyPeriodCalculator.getOperationalConditionEnd(application.getStartTime(), end,
            winterTimeOperation, winterTime));
    result.add(new PricedPeriod(TimeUtil.homeTime(application.getStartTime()), operationalConditionEnd,
        getFirstOpenPeriodId()));

    ZonedDateTime workFinishedStart = operationalConditionEnd.plusDays(1);
    if (!workFinishedStart.isAfter(end)) {
      result.add(new PricedPeriod(workFinishedStart, end, getWorkFinishedPeriodId()));
    }
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

  /**
   * Overlap of a phase span and a single dated invoicing period.
   */
  private static class MonthlyPricedPeriod {
    ZonedDateTime start;
    ZonedDateTime end;
    Integer invoicingPeriodId;
    boolean workFinished;

    MonthlyPricedPeriod(ZonedDateTime start, ZonedDateTime end, Integer invoicingPeriodId,
        boolean workFinished) {
      this.start = start;
      this.end = end;
      this.invoicingPeriodId = invoicingPeriodId;
      this.workFinished = workFinished;
    }

    boolean isWorkFinished() {
      return workFinished;
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
