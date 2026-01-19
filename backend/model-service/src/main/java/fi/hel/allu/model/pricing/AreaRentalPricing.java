package fi.hel.allu.model.pricing;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.util.PriceUtil;

import static org.apache.commons.lang3.BooleanUtils.toBoolean;

/**
 * Implementation for area rental pricing. See
 * <a href="http://www.hel.fi/static/hkr/luvat/maksut_katutyoluvat.pdf">maksut_katutyoluvat.pdf</a>,
 * "Alueenkäyttömaksu", for specification.
 */
public class AreaRentalPricing extends Pricing {

  private static final String LOCATION_IDENTIFIER = "%s/%d";
  private static final String DAILY_PRICE_EXPLANATION = "Alueenkäyttömaksu, maksuluokka %s, " + LOCATION_IDENTIFIER;
  private static final String HANDLING_FEE_TEXT = "Ilmoituksen käsittely- ja valvontamaksu";
  private static final String HANDLING_FEE_LT_8_DAYS_EXPLANATION = "Alle kahdeksan (8) vuorokautta kestävä työ";
  private static final String HANDLING_FEE_LT_6_MONTHS_EXPLANATION = "Alle kuusi (6) kuukautta kestävä työ";
  private static final String HANDLING_FEE_GE_6_MONTHS_EXPLANATION = "Vähintään kuusi (6) kuukautta kestävä työ";
  private static final String MINOR_DISTURBANCE_EXPLANATION = "Vähäistä haittaa aiheuttava työ";
  private static final String MAJOR_DISTURBANCE_EXPLANATION = "Vähäistä suurempaa haittaa aiheuttava työ";
  private static final String UNDERPASS_TEXT = "Altakuljettava";

  private static final LocalDateTime POST_2026_PAYMENT_DATE = LocalDateTime.of(2026, 3, 1, 0, 0, 0, 0);

  private final Application application;
  private final PricingDao pricingDao;
  private final PricingExplanator pricingExplanator;
  private final List<InvoicingPeriod> invoicingPeriods;
  private final double AREA_UNIT;

  public AreaRentalPricing(
    Application application,
    PricingDao pricingDao,
    PricingExplanator pricingExplanator,
    List<InvoicingPeriod> invoicingPeriods
  ) {
    this.application = application;
    this.pricingDao = pricingDao;
    this.pricingExplanator = pricingExplanator;
    this.invoicingPeriods = invoicingPeriods.stream().sorted(Comparator.comparing(InvoicingPeriod::getStartTime)).collect(Collectors.toList());
    AREA_UNIT = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.AREA_UNIT_M2, application.getStartTime());
    setHandlingFee();
  }

  /**
   * Sets the handling fee for the current area rental application based on various pricing rules.
   *
   * This method determines the applicable handling fee by considering the rental period's start date
   * and specific application attributes. It evaluates whether the rental period falls before or on/after
   * the date defined by `POST_2026_PAYMENT_DATE`. Different pricing keys and explanations are applied
   * depending on various conditions:
   *
   * - For rental periods starting on or after 1.3.2026:
   *   - If the duration is less than 8 days, a specific fee and explanation are applied.
   *   - If the rental period spans at least six months, a different fee and explanation are used.
   *   - Otherwise, a fee and explanation for rental periods of less than six months are applied.
   *
   * - For rental periods starting before 1.3.2026:
   *   - If the application has a major disturbance, a specific fee and explanation are used.
   *   - If the application has a minor disturbance, a different fee and explanation are applied.
   *
   * The calculated fee is set as part of the application charges. Additionally, a charge basis entry
   * is added to capture details about the handling fee and its explanation.
   */
  private void setHandlingFee() {
    final Integer periodId = getFirstOpenPeriodId();

    // Handling fee and explanation starting from 1.3.2026
    if (isPost2026AreaRentalPayment()) {

      PricingKey key;
      String explanation;

      long days = getDurationInDays();

      if (days < 8) {
        key = PricingKey.HANDLING_FEE_LT_8_DAYS;
        explanation = HANDLING_FEE_LT_8_DAYS_EXPLANATION;
      }
      else if (isAtLeastSixMonths()) {
        key = PricingKey.HANDLING_FEE_GE_6_MONTHS;
        explanation = HANDLING_FEE_GE_6_MONTHS_EXPLANATION;
      }
      else {
        key = PricingKey.HANDLING_FEE_LT_6_MONTHS;
        explanation = HANDLING_FEE_LT_6_MONTHS_EXPLANATION;
      }

      int price = pricingDao.findValue(
        ApplicationType.AREA_RENTAL,
        key,
        application.getStartTime()
      );

      setPriceInCents(price);

      addChargeBasisEntry(
        ChargeBasisTag.AreaRentalHandlingFee(),
        ChargeBasisUnit.PIECE,
        1,
        price,
        HANDLING_FEE_TEXT,
        price,
        List.of(explanation),
        null,
        periodId,
        null
      );

      return;
    }

    // Handling fee and explanation before 1.3.2026
    AreaRental areaRental = (AreaRental)application.getExtension();

    if (toBoolean(areaRental.getMajorDisturbance())) {
      int price = pricingDao.findValue(
        ApplicationType.AREA_RENTAL,
        PricingKey.MAJOR_DISTURBANCE_HANDLING_FEE,
        application.getStartTime()
      );
      setPriceInCents(price);
      addChargeBasisEntry(
        ChargeBasisTag.AreaRentalHandlingFee(),
        ChargeBasisUnit.PIECE,
        1,
        price,
        HANDLING_FEE_TEXT,
        price,
        List.of(MAJOR_DISTURBANCE_EXPLANATION),
        null,
        periodId,
        null
      );
    }
    else {
      int price = pricingDao.findValue(
        ApplicationType.AREA_RENTAL,
        PricingKey.MINOR_DISTURBANCE_HANDLING_FEE,
        application.getStartTime()
      );
      setPriceInCents(price);
      addChargeBasisEntry(
        ChargeBasisTag.AreaRentalHandlingFee(),
        ChargeBasisUnit.PIECE,
        1,
        price,
        HANDLING_FEE_TEXT,
        price,
        List.of(MINOR_DISTURBANCE_EXPLANATION),
        null,
        periodId,
        null
      );
    }
  }

  /**
   * Calculates the total duration of the rental period for the current application in days.
   * The duration includes both the start and end dates, adding one day to ensure inclusivity.
   *
   * @return the duration of the rental period in days, inclusive of both start and end dates.
   */
  private long getDurationInDays() {
    return ChronoUnit.DAYS.between(
      application.getStartTime().toLocalDate(),
      application.getEndTime().toLocalDate()
    ) + 1;
  }

  /**
   * Determines whether the rental period for the application spans at least six months.
   *
   * The method calculates a date that is six months after the start time of the application.
   * It then checks if the end time of the application is on or after that calculated date.
   *
   * @return {@code true} if the rental period of the application is at least six months,
   *         {@code false} otherwise.
   */
  private boolean isAtLeastSixMonths() {
    ZonedDateTime start = application.getStartTime();
    ZonedDateTime sixMonthsLater = start.plusMonths(6);
    return !application.getEndTime().isBefore(sixMonthsLater);
  }

  /**
   * Determines if the area's rental payment starts on or after a specific date
   * defined by the constant `POST_2026_PAYMENT_DATE`. The comparison is made
   * using the Helsinki time zone.
   *
   * @return {@code true} if the rental payment starts on or after the
   *         `POST_2026_PAYMENT_DATE`, {@code false} otherwise.
   */
  private boolean isPost2026AreaRentalPayment() {
    return !application.getStartTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId).isBefore(
      ZonedDateTime.of(POST_2026_PAYMENT_DATE, TimeUtil.HelsinkiZoneId)
    );
  }

  private Integer getFirstOpenPeriodId() {
    return invoicingPeriods.stream().filter(p -> !p.isClosed()).findFirst().map(p -> p.getId()).orElse(null);
  }

  @Override
  public void addLocationPrice(Location location, ZonedDateTime startTime) {
    List<AreaRentalPeriodPrice> periodPrices = getLocationPeriodPrices(location, startTime);
    for (AreaRentalPeriodPrice periodPrice : periodPrices) {
      addChargeBasisEntry(location, periodPrice);
      setPriceInCents(periodPrice.getNetPrice() + getPriceInCents());
      if (toBoolean(location.getUnderpass())) {
        addUnderpassDiscount(periodPrice, startTime);
      }
    }
  }

  private void addUnderpassDiscount(AreaRentalPeriodPrice periodPrice, ZonedDateTime startTime) {
    final double discount = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNDERPASS_DICOUNT_PERCENTAGE, startTime);
    ChargeBasisTag tag = periodPrice.getPeriodId() != null ? ChargeBasisTag.AreaRentalUnderpass(Integer.toString(periodPrice.getLocationKey()), Integer.toString(periodPrice.getPeriodId())) :
      ChargeBasisTag.AreaRentalUnderpass(Integer.toString(periodPrice.getLocationKey()));
    String explanation = String.format(LOCATION_IDENTIFIER, application.getApplicationId(), periodPrice.getLocationKey());
    addChargeBasisEntry(tag, ChargeBasisUnit.PERCENT, -discount, 0, UNDERPASS_TEXT, 0, Collections.singletonList(explanation) , periodPrice.getTag(), periodPrice.getPeriodId(), null);
    int netDiscount = (int)Math.round((discount / 100.0) * periodPrice.getNetPrice());
    setPriceInCents(getPriceInCents() - netDiscount);
  }

  private void addChargeBasisEntry(Location location, AreaRentalPeriodPrice periodPrice) {
    addChargeBasisEntry(periodPrice.getTag(), ChargeBasisUnit.DAY, periodPrice.getNumberOfDays(), periodPrice.getUnitPrice(),
        getPriceText(periodPrice), periodPrice.getNetPrice(), pricingExplanator.getExplanation(location, periodPrice.getPeriodText()),
        null, periodPrice.getPeriodId(), periodPrice.getLocationId());
  }

  protected List<AreaRentalPeriodPrice> getLocationPeriodPrices(Location location, ZonedDateTime startTime) {
    AreaRentalLocationPrice locationPrice = new AreaRentalLocationPrice(location, AREA_UNIT);
    locationPrice.setDailyPrice(getPrice(locationPrice.getNumUnits(), locationPrice.getPaymentClass(), startTime));
    List<AreaRentalPeriodPrice> periodPrices = new ArrayList<>();

    if (invoicingPeriods.isEmpty()) {
      periodPrices.add(new AreaRentalPeriodPrice(locationPrice));
    } else {
      getLocationPeriods(locationPrice).forEach(p -> periodPrices.add(new AreaRentalPeriodPrice(locationPrice, p)));
    }
    return periodPrices;
  }

  /**
   * Gets invoicing periods overlapping with location's period
   */
  private List<InvoicingPeriod> getLocationPeriods(AreaRentalLocationPrice locationPrice) {
    return invoicingPeriods.stream()
        .filter(p -> overlaps(p, locationPrice))
        .collect(Collectors.toList());
  }

  private boolean overlaps(InvoicingPeriod period, AreaRentalLocationPrice locationPrice) {
    if (period.getEndTime() == null) {
      // Open ended period, overlaps if location end date not before period start date
      return TimeUtil.isSameDateOrLater(locationPrice.getEndTime(), period.getStartTime());
    } else {
      return TimeUtil.datePeriodsOverlap(period.getStartTime(), period.getEndTime(), locationPrice.getStartTime(), locationPrice.getEndTime());
    }
  }


  private int getPrice(int numUnits, String paymentClass, ZonedDateTime startTime) {
    if (paymentClass.equals(PriceUtil.UNDEFINED_PAYMENT_CLASS) || paymentClass.equalsIgnoreCase("h1")) {
      return 0;
    }
    return numUnits * pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNIT_PRICE, paymentClass, startTime);
  }

  private String getPriceText(AreaRentalPeriodPrice periodPrice) {
    return String.format(DAILY_PRICE_EXPLANATION, PriceUtil.getPaymentClassText(periodPrice.getPaymentClass()),
        application.getApplicationId(), periodPrice.getLocationKey());
  }

}
