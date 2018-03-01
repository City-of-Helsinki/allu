package fi.hel.allu.model.pricing;

import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.InvoiceRow;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class EventPricingTest {
  InfoTexts infoTexts;

  @Before
  public void setUp() throws Exception {
    infoTexts = new InfoTexts();
    infoTexts.locationAddress = "location";
    infoTexts.fixedLocation = "fixedLocation";
    infoTexts.eventPeriod = "eventPeriod";
    infoTexts.buildPeriods = "buildPeriods";
  }

  @Test
  public void testOpenEvent() {
    // EventPricing configuration for open event at Narinkka
    PricingConfiguration bc = new PricingConfiguration(6000000L, 50, 50, 14, null, null, null, null);
    EventPricing bill = new EventPricing();
    // Calculate a bill for 5-day event with two build days and some structures + area:
    bill.accumulatePrice(bc, 5, 2, 30.5, 300, infoTexts);
    assertEquals(360000, bill.getPriceInCents()); // The price should be 3600
                                                  // EUR
    // Verify that EcoCompass gives 30% discount
    bill.applyDiscounts(true);
    assertEquals(252000, bill.getPriceInCents());
    verifyInvoicePrice(bill.getChargeBasisEntries(), bill.getPriceInCents());
  }

  @Test
  public void testAreaExtraFee() {
    // EventPricing configuration for non-free open event at Zone 2
    PricingConfiguration bc = new PricingConfiguration(2500000, 50, 50, 14, null, null, new long[] { 5000, 2500, 1250 },
        new double[] { 0.0, 2000.0, 4000.0 });
    EventPricing bill = new EventPricing();
    // Calculate price for 20-day event with four build days and a 5000 sq.m.
    // area (i.e., 14 days with base price, 6 days with discount price and 4
    // days with build price)
    bill.accumulatePrice(bc, 20, 4, 20, 5000.0, infoTexts);
    assertEquals(3562500L, bill.getPriceInCents()); // The price should be 35625
                                                  // EUR
    verifyInvoicePrice(bill.getChargeBasisEntries(), bill.getPriceInCents());
  }

  @Test
  public void testStructureExtraFee() {
    // EventPricing configuration for open event at Zone 3
    PricingConfiguration bc = new PricingConfiguration(1250000, 50, 50, 14, new long[] { 125000, 62500 },
        new double[] { 100.0, 300.0 },
        null, null);
    EventPricing bill = new EventPricing();
    // Price for 25-day event with three build days, 455 sqm structures and 1000
    // sqm area:
    bill.accumulatePrice(bc, 25, 3, 455.0, 1000.0, infoTexts);
    assertEquals(997500, bill.getPriceInCents()); // The price should be 9975
                                                  // EUR
    verifyInvoicePrice(bill.getChargeBasisEntries(), bill.getPriceInCents());
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

  private void verifyInvoicePrice(List<ChargeBasisEntry> chargeBasisEntries, int expectedPrice) {
    List<InvoiceRow> invoiceRows = new ChargeBasisCalc(chargeBasisEntries).toInvoiceRows();
    int invoicePrices = invoiceRows.stream().mapToInt(ir -> ir.getNetPrice()).sum();
    assertEquals(expectedPrice, invoicePrices);
  }
}
