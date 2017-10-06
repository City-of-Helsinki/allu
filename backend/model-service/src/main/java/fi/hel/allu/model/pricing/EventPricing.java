package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;

public class EventPricing extends Pricing {

  private static final String BASE_FEE_TEXT = "Päivän perustaksa";
  private static final String DAY_TOTAL_FEE_TEXT = "Päivätaksa sisältäen rakenne- ja pinta-alalisät";
  private static final String MULTIPLE_DAY_FEE_TEXT = "Maksu %1$d päivältä à %2$.2f EUR";
  private static final String LONG_EVENT_DISCOUNT_TEXT = "Alennus %1$d päivää ylittäviltä tapahtumapäiviltä";
  private static final String BUILD_DAY_FEE_TEXT = "Rakennus-/purkupäiviä";
  private static final String HEAVY_STRUCTURE_TEXT = "Raskaita rakenteita +50%";
  private static final String SALES_ACTIVITY_TEXT = "Myyntitoimintaa +50%";
  private static final String ECO_COMPASS_TEXT = "Ekokompassi-alennus -30%";

  // Privately store the price in 1/100 of cents, convert to cents on
  // extraction:
  private long fullPrice = 0;
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
      double area) {
    long dailyCharge = pricingConfig.getBaseCharge();
    addChargeBasisEntry(ChargeBasisTag.EventBaseFee(), ChargeBasisUnit.PIECE, 0, priceInCents(dailyCharge), BASE_FEE_TEXT, 0);

    dailyCharge += calculateStructureExtras(pricingConfig, structureArea);
    dailyCharge += calculateAreaExtras(pricingConfig, area);
    addChargeBasisEntry(ChargeBasisTag.EventDailyFee(), ChargeBasisUnit.PIECE, 0, priceInCents(dailyCharge), DAY_TOTAL_FEE_TEXT,
        0);

    long totalCharge = dailyCharge * eventDays;
    addChargeBasisEntry(ChargeBasisTag.EventMultipleDayFee(), ChargeBasisUnit.DAY, eventDays, priceInCents(dailyCharge),
        String.format(MULTIPLE_DAY_FEE_TEXT, eventDays, priceInCents(dailyCharge) / 100.0), priceInCents(totalCharge));
    if (pricingConfig.getDurationDiscountLimit() != 0 && eventDays > pricingConfig.getDurationDiscountLimit()) {
      int discountDays = eventDays - pricingConfig.getDurationDiscountLimit();
      long dailyDiscount = Math.round(dailyCharge * pricingConfig.getDurationDiscountPercent() / 100.0);
      long discount = dailyDiscount * discountDays;
      addChargeBasisEntry(ChargeBasisTag.EventLongEventDiscount(), ChargeBasisUnit.DAY, discountDays, -priceInCents(dailyDiscount),
          String.format(LONG_EVENT_DISCOUNT_TEXT, pricingConfig.getDurationDiscountLimit()),
          -priceInCents(discount));
      totalCharge -= discount;
    }
    if (buildDays != 0 && pricingConfig.getBuildDiscountPercent() != 0) {
      long dailyBuildFee = Math.round(dailyCharge * (100 - pricingConfig.getBuildDiscountPercent()) / 100.0);
      long buildFees = buildDays * dailyBuildFee;
      totalCharge += buildFees;
      addChargeBasisEntry(ChargeBasisTag.EventBuildDayFee(), ChargeBasisUnit.DAY, buildDays, priceInCents(dailyBuildFee),
          BUILD_DAY_FEE_TEXT,
          priceInCents(buildFees));
    }

    fullPrice += totalCharge;
  }

  private int priceInCents(long internalPrice) {
    // Divide by 100 to convert internal price units to price in cents.
    int absVal = (int) ((Math.abs(internalPrice) + 50) / 100);
    return (internalPrice < 0) ? -absVal : absVal;
  }

  /**
   * Get the calculated price in cents.
   */
  @Override
  public int getPriceInCents() {
    return Math.round(fullPrice / 100 * paymentPercentage / 100);
  }

  private long calculateStructureExtras(PricingConfiguration pricingConfig, double structureArea) {
    Long[] structureExtraCharges = pricingConfig.getStructureExtraCharges();
    if (structureExtraCharges == null) {
      return 0; // No extra charges for structures
    }
    long total = 0L;
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
      total += (long) (0.5 + structureExtraCharges[i] * billingMultiplier);
    }
    return total;
  }

  private long calculateAreaExtras(PricingConfiguration pricingConfig, double area) {
    Long[] areaExtraCharges = pricingConfig.getAreaExtraCharges();
    if (areaExtraCharges == null) {
      return 0; // no extra tax for areas
    }
    long total = 0L;
    Double[] areaExtraChargeLimits = pricingConfig.getAreaExtraChargeLimits();
    // billing is per starting full square meter:
    double billableArea = Math.ceil(area);
    for (int i = 0; i < areaExtraChargeLimits.length && billableArea > areaExtraChargeLimits[i]; ++i) {
      double lowerLimit = areaExtraChargeLimits[i];
      double upperLimit = billableArea;
      if (i+1 < areaExtraChargeLimits.length) {
        upperLimit = Math.min(upperLimit, areaExtraChargeLimits[i+1]);
      }
      total += (long) (0.5 + areaExtraCharges[i] * (upperLimit - lowerLimit));
    }
    return total;
  }

  public void applyDiscounts(boolean ecoCompass, String noPriceReason, boolean heavyStructure, boolean salesActivity) {
    paymentPercentage = 100;
    if (noPriceReason != null) {
      paymentPercentage = 0;
      if (heavyStructure) {
        long structureFee = fullPrice / 2;
        addChargeBasisEntry(ChargeBasisTag.EventHeavyStructures(), ChargeBasisUnit.PIECE, 1, priceInCents(structureFee),
            HEAVY_STRUCTURE_TEXT,
            priceInCents(structureFee));
        paymentPercentage += 50;
      }
      if (salesActivity) {
        long salesFee = fullPrice / 2;
        addChargeBasisEntry(ChargeBasisTag.EventSalesActivity(), ChargeBasisUnit.PIECE, 1, priceInCents(salesFee),
            SALES_ACTIVITY_TEXT, priceInCents(salesFee));
        paymentPercentage += 50;
      }
    }
    if (ecoCompass) {
      // 30 percent discount from full price (incl. extra fees)
      long ecoDiscount = -fullPrice * paymentPercentage / 100 * 30 / 100;
      addChargeBasisEntry(ChargeBasisTag.EventEcoCompass(), ChargeBasisUnit.PIECE, 1, priceInCents(ecoDiscount), ECO_COMPASS_TEXT,
          priceInCents(ecoDiscount));
      paymentPercentage = paymentPercentage * 7 / 10;
    }
  }
}
