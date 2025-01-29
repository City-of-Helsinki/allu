package fi.hel.allu.model.pricing;

import java.time.ZonedDateTime;
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
 * http://www.hel.fi/static/hkr/luvat/maksut_katutyoluvat.pdf,
 * "Alueenkäyttömaksu", for specification.
 */
public class AreaRentalPricing extends Pricing {

  private static final String LOCATION_IDENTIFIER = "%s/%d";
  private static final String DAILY_PRICE_EXPLANATION = "Alueenkäyttömaksu, maksuluokka %s, " + LOCATION_IDENTIFIER;
  private static final String HANDLING_FEE_TEXT = "Käsittely- ja valvontamaksu";
  private static final String MINOR_DISTURBANCE_EXPLANATION = "Vähäistä haittaa aiheuttava työ";
  private static final String MAJOR_DISTURBANCE_EXPLANATION = "Vähäistä suurempaa haittaa aiheuttava työ";
  private static final String UNDERPASS_TEXT = "Altakuljettava";

  private final Application application;
  private final PricingDao pricingDao;
  private final PricingExplanator pricingExplanator;
  private final List<InvoicingPeriod> invoicingPeriods;
  private final double AREA_UNIT;

  public AreaRentalPricing(Application application, PricingDao pricingDao, PricingExplanator pricingExplanator,
      List<InvoicingPeriod> invoicingPeriods) {
    this.application = application;
    this.pricingDao = pricingDao;
    this.pricingExplanator = pricingExplanator;
    this.invoicingPeriods = invoicingPeriods.stream().sorted(Comparator.comparing(InvoicingPeriod::getStartTime))
        .collect(Collectors.toList());
    AREA_UNIT = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.AREA_UNIT_M2, application.getStartTime());
    setHandlingFee();
  }

  private void setHandlingFee() {
    final Integer periodId = getFirstOpenPeriodId();
    final AreaRental areaRental = (AreaRental)application.getExtension();
    if (toBoolean(areaRental.getMajorDisturbance())) {
      final int price = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MAJOR_DISTURBANCE_HANDLING_FEE, application.getStartTime());
      setPriceInCents(price);
      addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, price,
          HANDLING_FEE_TEXT, price, Arrays.asList(MAJOR_DISTURBANCE_EXPLANATION), null, periodId, null);
    } else {
      final int price = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MINOR_DISTURBANCE_HANDLING_FEE, application.getStartTime());
      setPriceInCents(price);
      addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, price,
          HANDLING_FEE_TEXT, price, Arrays.asList(MINOR_DISTURBANCE_EXPLANATION), null, periodId, null);
    }
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
