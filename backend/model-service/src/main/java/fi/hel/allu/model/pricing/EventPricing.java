package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EventPricing extends Pricing {

  private static final String BASE_FEE_TEXT = "Päivän perustaksa %1$.2f EUR/päivä";
  private static final String STRUCTURE_EXTRA_FEE_TEXT = "Rakennelisä %1$.2f EUR/päivä";
  private static final String AREA_EXTRA_FEE_TEXT = "Pinta-alalisä %1$.2f EUR/päivä";
  private static final String MULTIPLE_DAY_FEE_TEXT = "Maksu %1$d päivältä à %2$.2f EUR";
  private static final String LONG_EVENT_DISCOUNT_TEXT = "Alennus %1$d päivää ylittäviltä tapahtumapäiviltä";
  private static final String BUILD_DAY_FEE_TEXT = "Rakennus-/purkupäiviä";
  private static final String ECO_COMPASS_TEXT = "Ekokompassi-alennus -30%";

  // Privately store the price in euros, convert to cents on
  // extraction:
  private BigDecimal fullPrice = BigDecimal.ZERO;
  // What percentage of the full price must be paid after discounts?
  private int paymentPercentage = 100;

  /**
   * Accumulate the price for an outdoor event with given parameters
   *
   * @param pricingConfig
   * @param eventDays
   * @param buildDays
   * @param structureArea
   * @param area
   */
  public void accumulatePrice(PricingConfiguration pricingConfig, int eventDays, int buildDays, double structureArea,
      double area, InfoTexts infoTexts) {
    List<String> explanation = new ArrayList<>();
    final String address = Optional.ofNullable(infoTexts.fixedLocation).orElse(infoTexts.locationAddress);
    // Add place and event time as one explanation line:
    explanation.add(address + " (" + infoTexts.eventPeriod + ")");
    // daily charge is in euros:
    BigDecimal dailyCharge = BigDecimal.valueOf(pricingConfig.getBaseCharge(), 4);
    explanation.add(String.format(BASE_FEE_TEXT, dailyCharge.doubleValue()));

    BigDecimal structureExtras = calculateStructureExtras(pricingConfig, structureArea);
    BigDecimal areaExtras = calculateAreaExtras(pricingConfig, area);
    if (structureExtras.compareTo(BigDecimal.ZERO) != 0) {
      explanation.add(String.format(STRUCTURE_EXTRA_FEE_TEXT, structureExtras.doubleValue()));
    }
    if (areaExtras.compareTo(BigDecimal.ZERO) != 0) {
      explanation.add(String.format(AREA_EXTRA_FEE_TEXT, areaExtras.doubleValue()));
    }
    dailyCharge = dailyCharge.add(structureExtras.add(areaExtras));

    BigDecimal totalCharge = dailyCharge.multiply(BigDecimal.valueOf(eventDays));
    addChargeBasisEntry(ChargeBasisTag.EventMultipleDayFee(pricingConfig.getFixedLocationId()), ChargeBasisUnit.DAY, eventDays, priceInCents(dailyCharge),
        String.format(MULTIPLE_DAY_FEE_TEXT, eventDays, dailyCharge.doubleValue()), priceInCents(totalCharge),
        explanation);

    if (pricingConfig.getDurationDiscountLimit() != 0 && eventDays > pricingConfig.getDurationDiscountLimit()) {
      int discountDays = eventDays - pricingConfig.getDurationDiscountLimit();
      BigDecimal dailyDiscount = dailyCharge
          .multiply(BigDecimal.valueOf(pricingConfig.getDurationDiscountPercent(), 2));
      BigDecimal discount = dailyDiscount.multiply(BigDecimal.valueOf(discountDays));
      addChargeBasisEntry(ChargeBasisTag.EventLongEventDiscount(pricingConfig.getFixedLocationId()), ChargeBasisUnit.DAY, discountDays, -priceInCents(dailyDiscount),
          String.format(LONG_EVENT_DISCOUNT_TEXT, pricingConfig.getDurationDiscountLimit()),
          -priceInCents(discount));
      totalCharge = totalCharge.subtract(discount);
    }
    if (buildDays != 0 && pricingConfig.getBuildDiscountPercent() != 0) {
      BigDecimal dailyBuildFee = dailyCharge
          .multiply(BigDecimal.valueOf(100 - pricingConfig.getBuildDiscountPercent(), 2));
      BigDecimal buildFees = BigDecimal.valueOf(buildDays).multiply(dailyBuildFee);
      totalCharge = totalCharge.add(buildFees);
      addChargeBasisEntry(ChargeBasisTag.EventBuildDayFee(pricingConfig.getFixedLocationId()), ChargeBasisUnit.DAY, buildDays, priceInCents(dailyBuildFee),
          BUILD_DAY_FEE_TEXT,
          priceInCents(buildFees), Collections.singletonList(address + " (" + infoTexts.buildPeriods + ")"));
    }

    fullPrice = fullPrice.add(totalCharge);
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

  private BigDecimal calculateStructureExtras(PricingConfiguration pricingConfig, double structureArea) {
    Long[] structureExtraCharges = pricingConfig.getStructureExtraCharges();
    if (structureExtraCharges == null) {
      return BigDecimal.ZERO; // No extra charges for structures
    }
    BigDecimal total = BigDecimal.ZERO;
    Double[] structureExtraChargeLimits = pricingConfig.getStructureExtraChargeLimits();
    // BillableArea is per starting 10 sq. meters
    double billableStructures = Math.ceil(structureArea / 10.0) * 10.0;
    for (int i = 0; i < structureExtraChargeLimits.length && billableStructures > structureExtraChargeLimits[i]; ++i) {
      double lowerLimit = structureExtraChargeLimits[i];
      double upperLimit = billableStructures;
      if (i + 1 < structureExtraChargeLimits.length) {
        // only until next extra limit
        upperLimit = Math.min(upperLimit, structureExtraChargeLimits[i + 1]);
      }
      double billingMultiplier = (upperLimit - lowerLimit) / 10.0; // charge is
                                                                   // per 10 sqm
      total = total
          .add(BigDecimal.valueOf(structureExtraCharges[i], 4).multiply(BigDecimal.valueOf(billingMultiplier)));
    }
    return total;
  }

  private BigDecimal calculateAreaExtras(PricingConfiguration pricingConfig, double area) {
    Long[] areaExtraCharges = pricingConfig.getAreaExtraCharges();
    if (areaExtraCharges == null) {
      return BigDecimal.ZERO; // no extra tax for areas
    }
    BigDecimal total = BigDecimal.ZERO;
    Double[] areaExtraChargeLimits = pricingConfig.getAreaExtraChargeLimits();
    // billing is per starting full square meter:
    double billableArea = Math.ceil(area);
    for (int i = 0; i < areaExtraChargeLimits.length && billableArea > areaExtraChargeLimits[i]; ++i) {
      double lowerLimit = areaExtraChargeLimits[i];
      double upperLimit = billableArea;
      if (i+1 < areaExtraChargeLimits.length) {
        upperLimit = Math.min(upperLimit, areaExtraChargeLimits[i+1]);
      }
      total = total
          .add(BigDecimal.valueOf(areaExtraCharges[i], 4).multiply(BigDecimal.valueOf(upperLimit - lowerLimit)));
    }
    return total;
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
