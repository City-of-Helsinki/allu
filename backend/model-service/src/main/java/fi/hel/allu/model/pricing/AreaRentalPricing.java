package fi.hel.allu.model.pricing;

import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.dao.PricingDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.PricingKey;

import java.time.temporal.ChronoUnit;

/**
 * Implementation for area rental pricing. See
 * http://www.hel.fi/static/hkr/luvat/maksut_katutyoluvat.pdf,
 * "Alueenkäyttömaksu", for specification.
 */
public class AreaRentalPricing extends Pricing {

  private static final String DAILY_PRICE_EXPLANATION = "Alueenkäyttömaksu";
  private static final String SHORT_TERM_HANDLING_EXPLANATION = "Käsittely- ja valvontamaksu (tilapäinen työ)";
  private static final String LONG_TERM_HANDLING_EXPLANATION = "Käsittely- ja valvontamaksu (työmaavuokraus)";

  private static final double AREA_UNIT = 15.0;

  private final Application application;
  private final PricingDao pricingDao;

  public AreaRentalPricing(Application application, PricingDao pricingDao) {
    this.application = application;
    this.pricingDao = pricingDao;
    setHandlingFee();
  }

  private void setHandlingFee() {
    ApplicationKind kind = application.getKind();
    switch (kind) {
    case ROLL_OFF:
    case LIFTING:
    case RELOCATION:
    case PHOTO_SHOOTING:
    case SNOW_WORK:
    case PUBLIC_EVENT: {
      final int price = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.SHORT_TERM_HANDLING_FEE);
      setPriceInCents(price);
        addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, price,
            SHORT_TERM_HANDLING_EXPLANATION, price);
      break;
    }
    case PROPERTY_RENOVATION:
    case NEW_BUILDING_CONSTRUCTION:
    case CONTAINER_BARRACK:
    case STORAGE_AREA:
    case OTHER: {
      final int price = pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.LONG_TERM_HANDLING_FEE);
      setPriceInCents(price);
        addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, price,
            LONG_TERM_HANDLING_EXPLANATION, price);
      break;
    }
    default:
      throw new IllegalArgumentException("Bad application kind for area rental: " + kind);
    }
  }

  @Override
  public void addLocationPrice(int locationKey, double locationArea, String paymentClass) {
    final long numUnits = Math.round(Math.ceil(locationArea / AREA_UNIT));
    final int dailyPrice = getPrice((int)numUnits, paymentClass);
    final int numDays = (int) CalendarUtil.startingUnitsBetween(application.getStartTime(), application.getEndTime(),
        ChronoUnit.DAYS);
    final int netPrice = dailyPrice * numDays;
    addChargeBasisEntry(ChargeBasisTag.AreaRentalDailyFee(Integer.toString(locationKey)), ChargeBasisUnit.DAY, numDays, dailyPrice,
        DAILY_PRICE_EXPLANATION, netPrice);
    setPriceInCents(netPrice + getPriceInCents());
  }

  private int getPrice(int numUnits, String paymentClass) {
    if (paymentClass.equalsIgnoreCase(UNDEFINED_PAYMENT_CLASS) || paymentClass.equalsIgnoreCase("h1")) {
      return 0;
    }
    return numUnits * pricingDao.findValue(ApplicationType.AREA_RENTAL, PricingKey.UNIT_PRICE, paymentClass);
  }
}
