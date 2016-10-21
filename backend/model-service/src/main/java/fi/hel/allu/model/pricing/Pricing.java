package fi.hel.allu.model.pricing;

import fi.hel.allu.model.pricing.InvoiceLine.LineType;

import java.util.ArrayList;
import java.util.List;

public class Pricing {

  private List<InvoiceLine> invoiceLines = new ArrayList<>();

  /**
   * Calculate full price for an outdoor event with given parameters
   * 
   * @param pricingConfig
   * @param eventDays
   * @param buildDays
   * @param structureArea
   * @param area
   * @return
   */
  public long calculateFullPrice(PricingConfiguration pricingConfig, int eventDays, int buildDays, double structureArea,
      double area) {
    long dailyCharge = pricingConfig.getBaseCharge();
    addInvoiceLine(LineType.BASE_CHARGE, dailyCharge);

    dailyCharge += calculateStructureExtras(pricingConfig, structureArea);
    dailyCharge += calculateAreaExtras(pricingConfig, area);
    addInvoiceLine(LineType.DAILY_CHARGE, dailyCharge);

    long totalCharge;
    if (pricingConfig.getDurationDiscountLimit() != 0 && eventDays > pricingConfig.getDurationDiscountLimit()) {
      totalCharge = dailyCharge * pricingConfig.getDurationDiscountLimit();
      totalCharge += (long) (0.5
          + (eventDays - pricingConfig.getDurationDiscountLimit()) // amount of discounted
                                                        // days
              * dailyCharge * (100 - pricingConfig.getDurationDiscountPercent()) / 100.0);
    } else {
      totalCharge = dailyCharge * eventDays;
    }
    totalCharge += (long) (0.5 + buildDays * dailyCharge * (100 - pricingConfig.getBuildDiscountPercent()) / 100.0);

    addInvoiceLine(LineType.TOTAL_CHARGE, totalCharge);
    return totalCharge;
  }

  private long calculateStructureExtras(PricingConfiguration pricingConfig, double structureArea) {
    long[] structureExtraCharges = pricingConfig.getStructureExtraCharges();
    if (structureExtraCharges == null) {
      return 0; // No extra charges for structures
    }
    long total = 0L;
    double[] structureExtraChargeLimits = pricingConfig.getStructureExtraChargeLimits();
    assert structureExtraChargeLimits != null && structureExtraChargeLimits.length == structureExtraCharges.length;
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
    addInvoiceLine(LineType.STRUCTURE_CHARGE, total);
    return total;
  }

  private long calculateAreaExtras(PricingConfiguration pricingConfig, double area) {
    long[] areaExtraCharges = pricingConfig.getAreaExtraCharges();
    if (areaExtraCharges == null) {
      return 0; // no extra tax for areas
    }
    long total = 0L;
    double[] areaExtraChargeLimits = pricingConfig.getAreaExtraChargeLimits();
    assert areaExtraChargeLimits != null && areaExtraChargeLimits.length == areaExtraCharges.length;
    double billableArea = Math.ceil(area); // billing is per starting full sq.
                                           // m.
    for (int i = 0; i < areaExtraChargeLimits.length && billableArea > areaExtraChargeLimits[i]; ++i) {
      double lowerLimit = areaExtraChargeLimits[i];
      double upperLimit = billableArea;
      if (i+1 < areaExtraChargeLimits.length) {
        upperLimit = Math.min(upperLimit, areaExtraChargeLimits[i+1]);
      }
      total += (long) (0.5 + areaExtraCharges[i] * (upperLimit - lowerLimit));
    }
    addInvoiceLine(LineType.AREA_CHARGE, total);
    return total;
  }

  private void addInvoiceLine(InvoiceLine.LineType lineType, long value) {
    invoiceLines.add(new InvoiceLine(lineType, value));
  }
}
