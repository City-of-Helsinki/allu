package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChargeBasisUnit;

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

  // Basic unit price on different payment class:
  private static final int UNIT_PRICE[] = { 600, 300, 130 };
  private static final int SHORT_TERM_HANDLING_FEE = 6000;
  private static final int LONG_TERM_HANDLING_FEE = 18000;

  private final Application application;

  public AreaRentalPricing(Application application) {
    this.application = application;
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
    case PUBLIC_EVENT:
      setPriceInCents(SHORT_TERM_HANDLING_FEE);
        addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, SHORT_TERM_HANDLING_FEE,
            SHORT_TERM_HANDLING_EXPLANATION, SHORT_TERM_HANDLING_FEE);
      break;
    case PROPERTY_RENOVATION:
    case NEW_BUILDING_CONSTRUCTION:
    case CONTAINER_BARRACK:
    case STORAGE_AREA:
    case OTHER:
      setPriceInCents(LONG_TERM_HANDLING_FEE);
        addChargeBasisEntry(ChargeBasisTag.AreaRentalHandlingFee(), ChargeBasisUnit.PIECE, 1, LONG_TERM_HANDLING_FEE,
            LONG_TERM_HANDLING_EXPLANATION, LONG_TERM_HANDLING_FEE);
      break;
    default:
      throw new IllegalArgumentException("Bad application kind for area rental: " + kind);
    }
  }

  @Override
  public void addLocationPrice(int locationKey, double locationArea, int paymentClass) {
    if (paymentClass < 1 || paymentClass > 3) {
      throw new IllegalArgumentException(String.format("Bad payment class %d", paymentClass));
    }
    long numUnits = Math.round(Math.ceil(locationArea / AREA_UNIT));
    int dailyPrice = (int) numUnits * UNIT_PRICE[paymentClass - 1];
    int numDays = (int) CalendarUtil.startingUnitsBetween(application.getStartTime(), application.getEndTime(),
        ChronoUnit.DAYS);
    int netPrice = dailyPrice * numDays;
    addChargeBasisEntry(ChargeBasisTag.AreaRentalDailyFee(Integer.toString(locationKey)), ChargeBasisUnit.DAY, numDays, dailyPrice,
        DAILY_PRICE_EXPLANATION, netPrice);
    setPriceInCents(netPrice + getPriceInCents());
  }
}
