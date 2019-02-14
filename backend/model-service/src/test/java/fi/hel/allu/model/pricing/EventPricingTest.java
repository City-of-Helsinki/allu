package fi.hel.allu.model.pricing;

import fi.hel.allu.common.types.EventNature;
import fi.hel.allu.model.domain.ChargeBasisCalc;
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
    OutdoorPricingConfiguration bc = new OutdoorPricingConfiguration(6000000L, 50);
    EventPricing bill = new EventPricing();
    // Calculate a bill for 5-day event with two build days
    bill.accumulatePrice(bc, 5, 2, 300.0, infoTexts, EventNature.PUBLIC_FREE);
    assertEquals(360000, bill.getPriceInCents());
    // Verify that EcoCompass gives 30% discount
    bill.applyDiscounts(true);
    assertEquals(252000, bill.getPriceInCents());
    verifyInvoicePrice(bill.getChargeBasisEntries(), bill.getPriceInCents());
  }

  @Test
  public void testBigEvent() {
    // EventPricing configuration for open event at Narinkka
    OutdoorPricingConfiguration bc = new OutdoorPricingConfiguration(5000000L, 0);
    EventPricing bill = new EventPricing();
    // Calculate a bill for 10-day big event,
    bill.accumulatePrice(bc, 10, 0, 11000.0, infoTexts, EventNature.BIG_EVENT);
    // 10 days, 500€ every starting 10000m² -> 1000€ per day
    assertEquals(1000000, bill.getPriceInCents());
  }

  private void verifyInvoicePrice(List<ChargeBasisEntry> chargeBasisEntries, int expectedPrice) {
    List<InvoiceRow> invoiceRows = new ChargeBasisCalc(chargeBasisEntries).toInvoiceRows();
    int invoicePrices = invoiceRows.stream().mapToInt(ir -> ir.getNetPrice()).sum();
    assertEquals(expectedPrice, invoicePrices);
  }
}
