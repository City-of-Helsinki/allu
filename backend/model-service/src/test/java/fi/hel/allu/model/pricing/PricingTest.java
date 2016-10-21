package fi.hel.allu.model.pricing;

import static org.junit.Assert.assertEquals;

import fi.hel.allu.model.pricing.Pricing;
import fi.hel.allu.model.pricing.PricingConfiguration;

import org.junit.Before;
import org.junit.Test;

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
    long sum = bill.calculateFullPrice(bc, 5, 2, 30.5, 300);
    assertEquals(36000000L, sum); // The price should be 3600 EUR
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
    long sum = bill.calculateFullPrice(bc, 20, 4, 20, 5000.0);
    assertEquals(356250000L, sum); // The price should be 35625 EUR
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
    long sum = bill.calculateFullPrice(bc, 25, 3, 455.0, 1000.0);
    assertEquals(99750000, sum); // The price should be 9975 EUR
  }

}
