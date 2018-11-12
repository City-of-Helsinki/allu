package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PricingKey;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * Implementation for area rental pricing. See
 * http://www.hel.fi/static/hkr/luvat/maksut_katutyoluvat.pdf,
 * "Alueenkäyttömaksu", for specification.
 */
public class AreaRentalPricing extends Pricing {
  private static final String DAILY_PRICE_EXPLANATION = "Alueenkäyttömaksu, maksuluokka %s, %s/%d";
  private static final String HANDLING_FEE_TEXT = "Käsittely- ja valvontamaksu";
  private static final String MINOR_DISTURBANCE_EXPLANATION = "Vähäistä haittaa aiheuttava työ";
  private static final String MAJOR_DISTURBANCE_EXPLANATION = "Vähäistä suurempaa haittaa aiheuttava työ";
  private static final String UNDERPASS_TEXT = "Altakuljettava";

  private final Application application;
  private final PricingDao pricingDao;
  private final PricingExplanator pricingExplanator;
  private final double AREA_UNIT;

  public AreaRentalPricing(Application application, PricingDao pricingDao, PricingExplanator pricingExplanator) {
    this.application = application;
    this.pricingDao = pricingDao;
    this.pricingExplanator = pricingExplanator;
    AREA_UNIT = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.AREA_UNIT_M2);
    setHandlingFee();
  }

  private void setHandlingFee() {
    final AreaRental areaRental = (AreaRental)application.getExtension();
    if (toBoolean(areaRental.getMajorDisturbance())) {
      final int price = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MAJOR_DISTURBANCE_HANDLING_FEE);
      setPriceInCents(price);
      addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, price,
          HANDLING_FEE_TEXT, price, Arrays.asList(MAJOR_DISTURBANCE_EXPLANATION));
    } else {
      final int price = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.MINOR_DISTURBANCE_HANDLING_FEE);
      setPriceInCents(price);
      addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, price,
          HANDLING_FEE_TEXT, price, Arrays.asList(MINOR_DISTURBANCE_EXPLANATION));
    }
  }

  @Override
  public void addLocationPrice(Location location) {
    final String paymentClass = location.getEffectivePaymentTariff();
    final Integer locationKey = location.getLocationKey();
    final double locationArea = location.getEffectiveArea();
    final long numUnits = Math.round(Math.ceil(locationArea / AREA_UNIT));
    final int dailyPrice = getPrice((int)numUnits, paymentClass);
    final int numDays = (int) CalendarUtil.startingUnitsBetween(
        location.getStartTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId),
        location.getEndTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId),
        ChronoUnit.DAYS);
    int netPrice = dailyPrice * numDays;
    final ChargeBasisTag tag = ChargeBasisTag.AreaRentalDailyFee(Integer.toString(locationKey));
    addChargeBasisEntry(tag, ChargeBasisUnit.DAY, numDays, dailyPrice,
        getPriceText(paymentClass, locationKey), netPrice, pricingExplanator.getExplanation(location));

    if (toBoolean(location.getUnderpass())) {
      final double discount = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNDERPASS_DICOUNT_PERCENTAGE);
      addChargeBasisEntry(ChargeBasisUnit.PERCENT, -discount, 0, UNDERPASS_TEXT, 0, tag);
      netPrice = (int)Math.round((discount / 100.0) * netPrice);
    }
    setPriceInCents(netPrice + getPriceInCents());
  }

  private int getPrice(int numUnits, String paymentClass) {
    if (paymentClass.equalsIgnoreCase(UNDEFINED_PAYMENT_CLASS) || paymentClass.equalsIgnoreCase("h1")) {
      return 0;
    }
    return numUnits * pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNIT_PRICE, paymentClass);
  }

  private String getPriceText(String paymentClass, int locationKey) {
    return String.format(DAILY_PRICE_EXPLANATION, getPaymentClassText(paymentClass), application.getApplicationId(), locationKey);
  }
}
