package fi.hel.allu.model.pricing;

import fi.hel.allu.common.util.CalendarUtil;
import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.model.domain.Application;

import java.time.temporal.ChronoUnit;

public class ExcavationPricing extends Pricing {

  private final Application application;

  private static final String HANDLING_FEE_TEXT = "Käsittelymaksu";
  private static final String AREA_FEE_TEXT = "Alueenkäyttömaksu, maksuluokka %d";

  private static final double SMALL_AREA_LIMIT = 60.0;
  private static final double LARGE_AREA_LIMIT = 120.0;
  private static final int HANDLING_FEE = 18000;
  private static final int SMALL_AREA_DAILY_FEE = 5000;
  private static final int MEDIUM_AREA_DAILY_FEE = 6500;
  private static final int LARGE_AREA_DAILY_FEE = 8000;

  public ExcavationPricing(Application application) {
    this.application = application;
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
    int days = (int) CalendarUtil.startingUnitsBetween(application.getStartTime(), application.getEndTime(),
        ChronoUnit.DAYS);
    int totalPrice = days * dailyFee;
    addChargeBasisEntry(ChargeBasisTag.ExcavationAnnouncementDailyFee(Integer.toString(locationKey)), ChargeBasisUnit.DAY, days,
        dailyFee, String.format(AREA_FEE_TEXT, paymentClass), totalPrice);
    setPriceInCents(totalPrice + getPriceInCents());
  }
}
