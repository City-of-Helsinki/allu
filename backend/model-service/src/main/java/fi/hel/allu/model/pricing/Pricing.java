package fi.hel.allu.model.pricing;

import fi.hel.allu.model.pricing.InvoiceRow.RowType;

import java.util.ArrayList;
import java.util.List;

public class Pricing {

  private List<InvoiceRow> invoiceRows = new ArrayList<>();

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
    addInvoiceRow(RowType.BASE_CHARGE, dailyCharge);

    dailyCharge += calculateStructureExtras(pricingConfig, structureArea);
    dailyCharge += calculateAreaExtras(pricingConfig, area);
    addInvoiceRow(RowType.DAILY_CHARGE, dailyCharge);

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

    addInvoiceRow(RowType.TOTAL_CHARGE, totalCharge);
    fullPrice += totalCharge;
  }

  /**
   * Get the calculated price in cents.
   */
  public int getPrice() {
    return (int) ((fullPrice + 50) / 100 * paymentPercentage / 100);
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
    addInvoiceRow(RowType.STRUCTURE_CHARGE, total);
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
    addInvoiceRow(RowType.AREA_CHARGE, total);
    return total;
  }

  private void addInvoiceRow(InvoiceRow.RowType rowType, long value) {
    invoiceRows.add(new InvoiceRow(rowType, value));
  }

  public void applyDiscounts(boolean ecoCompass, String noPriceReason, boolean heavyStructure, boolean salesActivity) {
    paymentPercentage = 100;
    if (noPriceReason != null) {
      addInvoiceRow(RowType.FREE_EVENT, 0);
      paymentPercentage = 0;
      if (heavyStructure) {
        addInvoiceRow(RowType.HEAVY_STRUCTURE, 0);
        paymentPercentage += 50;
      }
      if (salesActivity) {
        addInvoiceRow(RowType.SALES_ACTIVITY, 0);
        paymentPercentage += 50;
      }
    }
    if (ecoCompass) {
      addInvoiceRow(RowType.ECO_COMPASS, 0);
      paymentPercentage = paymentPercentage * 7 / 10;
    }
  }
}
