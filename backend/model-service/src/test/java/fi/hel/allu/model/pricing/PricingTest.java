package fi.hel.allu.model.pricing;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PricingTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testOpenEvent() {
    // Pricing configuration for open event at Narinkka
    PricingConfiguration bc = new PricingConfiguration(6000000L, 50, 50, 14, null, null, null, null);
    Pricing bill = new Pricing();
    // Calculate a bill for 5-day event with two build days and some structures + area:
    bill.calculateFullPrice(bc, 5, 2, 30.5, 300);
    assertEquals(360000, bill.getPrice()); // The price should be 3600 EUR
    // Verify that EcoCompass gives 30% discount
    bill.applyDiscounts(true, null, false, false);
    assertEquals(252000, bill.getPrice());
    // Verify that 100% discount works:
    bill.applyDiscounts(false, "SportsEvent", false, false);
    assertEquals(0, bill.getPrice());
    // Verify that sports event with heavy structures gets only 50% discount:
    bill.applyDiscounts(false, "SportsEvent", true, false);
    assertEquals(180000, bill.getPrice());
    // Verify that commercial activities also gives 50%:
    bill.applyDiscounts(false, "SportsEvent", false, true);
    assertEquals(180000, bill.getPrice());
    // Commercial activities and heavy structures --> no discount:
    bill.applyDiscounts(false, "SportsEvent", true, true);
    assertEquals(360000, bill.getPrice());
  }

  @Test
  public void testAreaExtraFee() {
    // Pricing configuration for non-free open event at Zone 2
    PricingConfiguration bc = new PricingConfiguration(2500000, 50, 50, 14, null, null, new long[] { 5000, 2500, 1250 },
        new double[] { 0.0, 2000.0, 4000.0 });
    Pricing bill = new Pricing();
    // Calculate price for 20-day event with four build days and a 5000 sq.m.
    // area (i.e., 14 days with base price, 6 days with discount price and 4
    // days with build price)
    bill.calculateFullPrice(bc, 20, 4, 20, 5000.0);
    assertEquals(3562500L, bill.getPrice()); // The price should be 35625 EUR
  }

  @Test
  public void testStructureExtraFee() {
    // Pricing configuration for open event at Zone 3
    PricingConfiguration bc = new PricingConfiguration(1250000, 50, 50, 14, new long[] { 125000, 62500 },
        new double[] { 100.0, 300.0 },
        null, null);
    Pricing bill = new Pricing();
    // Price for 25-day event with three build days, 455 sqm structures and 1000
    // sqm area:
    bill.calculateFullPrice(bc, 25, 3, 455.0, 1000.0);
    assertEquals(997500, bill.getPrice()); // The price should be 9975 EUR
  }

  @Test(expected = IllegalStateException.class)
  public void testAreaExtraChargeValidation() {
    PricingConfiguration pricingConfiguration = new PricingConfiguration();
    pricingConfiguration.setAreaExtraChargeLimits(new Double[] { Double.valueOf(1020.0), Double.valueOf(2204.2) });
    pricingConfiguration.setAreaExtraCharges(new Long[] { Long.valueOf(1234L) });
  }

  @Test(expected = IllegalStateException.class)
  public void testStructureExtraChargeValidation() {
    PricingConfiguration pricingConfiguration = new PricingConfiguration();
    pricingConfiguration.setStructureExtraChargeLimits(new Double[] { Double.valueOf(1020.0), Double.valueOf(2204.2) });
    pricingConfiguration.setStructureExtraCharges(new Long[] { Long.valueOf(1234L) });
  }

}
