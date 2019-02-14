package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.EventNature;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EventPricing extends Pricing {

  private static final String BASE_FEE_TEXT = "Päivän perustaksa %1$.2f EUR/päivä";
  private static final String BASE_FEE_TEXT_BIG_EVENT = "Päivän perustaksa %1$.2f EUR/alkava 10000m²/päivä";
  private static final String MULTIPLE_DAY_FEE_TEXT = "Maksu %1$d päivältä à %2$.2f EUR";
  private static final String BUILD_DAY_FEE_TEXT = "Rakennus-/purkupäiviä";
  private static final String ECO_COMPASS_TEXT = "Ekokompassi-alennus -30%";
  private static final double BIG_EVENT_AREA_THRESHOLD = 10000.0;

  // Privately store the price in euros, convert to cents on
  // extraction:
  private BigDecimal fullPrice = BigDecimal.ZERO;
  // What percentage of the full price must be paid after discounts?
  private int paymentPercentage = 100;

  /**
   * Accumulate the price for an outdoor event with given parameters
   */
  public void accumulatePrice(OutdoorPricingConfiguration pricingConfig, int eventDays, int buildDays, Double area,
      InfoTexts infoTexts, EventNature eventNature) {
    final String address = Optional.ofNullable(infoTexts.fixedLocation).orElse(infoTexts.locationAddress);
    BigDecimal baseCharge = BigDecimal.valueOf(pricingConfig.getBaseCharge(), 4);
    BigDecimal dailyCharge = getDailyPrice(baseCharge, eventNature, area);
    BigDecimal totalCharge = dailyCharge.multiply(BigDecimal.valueOf(eventDays));

    List<String> explanation = getExplanation(infoTexts, address, baseCharge, eventNature);

    addChargeBasisEntry(ChargeBasisTag.EventMultipleDayFee(pricingConfig.getFixedLocationId()), ChargeBasisUnit.DAY, eventDays, priceInCents(dailyCharge),
        String.format(MULTIPLE_DAY_FEE_TEXT, eventDays, dailyCharge.doubleValue()), priceInCents(totalCharge),
        explanation);

    if (buildDays != 0) {
      totalCharge = addBuildFees(pricingConfig, buildDays, infoTexts, address, dailyCharge, totalCharge);
    }
    fullPrice = fullPrice.add(totalCharge);
  }

  private BigDecimal addBuildFees(OutdoorPricingConfiguration pricingConfig, int buildDays, InfoTexts infoTexts,
      final String address, BigDecimal dailyCharge, BigDecimal totalCharge) {
    BigDecimal dailyBuildFee;
    if (pricingConfig.getBuildDiscountPercent() != 0) {
      dailyBuildFee = dailyCharge.multiply(BigDecimal.valueOf(100 - pricingConfig.getBuildDiscountPercent(), 2));
    } else {
      dailyBuildFee = dailyCharge;
    }
    BigDecimal buildFees = BigDecimal.valueOf(buildDays).multiply(dailyBuildFee);
    totalCharge = totalCharge.add(buildFees);
    addChargeBasisEntry(ChargeBasisTag.EventBuildDayFee(pricingConfig.getFixedLocationId()), ChargeBasisUnit.DAY, buildDays, priceInCents(dailyBuildFee),
        BUILD_DAY_FEE_TEXT,
        priceInCents(buildFees), Collections.singletonList(address + " (" + infoTexts.buildPeriods + ")"));
    return totalCharge;
  }

  private BigDecimal getDailyPrice(BigDecimal baseCharge, EventNature eventNature, Double area) {
    BigDecimal dailyPrice = baseCharge;
    if (eventNature == EventNature.BIG_EVENT) {
      dailyPrice = dailyPrice.multiply(BigDecimal.valueOf(Math.ceil(area / BIG_EVENT_AREA_THRESHOLD)));
    }
    return dailyPrice;
  }

  private List<String> getExplanation(InfoTexts infoTexts, final String address, BigDecimal dailyCharge, EventNature eventNature) {
    List<String> explanation = new ArrayList<>();
    // Add place and event time as one explanation line:
    explanation.add(address + " (" + infoTexts.eventPeriod + ")");
    // daily charge is in euros:
    String baseFeeText = eventNature == EventNature.BIG_EVENT ? BASE_FEE_TEXT_BIG_EVENT : BASE_FEE_TEXT;
    explanation.add(String.format(baseFeeText, dailyCharge.doubleValue()));
    return explanation;
  }

  private int priceInCents(BigDecimal priceInEuros) {
    return priceInEuros.scaleByPowerOfTen(2).setScale(0, RoundingMode.HALF_UP).intValue();
  }

  /**
   * Get the calculated price in cents.
   */
  @Override
  public int getPriceInCents() {
    return priceInCents(fullPrice.multiply(BigDecimal.valueOf(paymentPercentage, 2)));
  }

  public void applyDiscounts(boolean ecoCompass) {
    paymentPercentage = 100;
    if (ecoCompass) {
      // 30 percent discount from full price (incl. extra fees)
      addChargeBasisEntry(ChargeBasisTag.EcoCompassTag(), ChargeBasisUnit.PERCENT, -30.0, 0, ECO_COMPASS_TEXT, 0);
      paymentPercentage = paymentPercentage * 7 / 10;
    }
  }
}
