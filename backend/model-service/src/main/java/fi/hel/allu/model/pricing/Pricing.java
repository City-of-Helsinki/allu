package fi.hel.allu.model.pricing;

import fi.hel.allu.common.exception.NotImplementedException;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.ChargeBasisUnit;

import java.util.ArrayList;
import java.util.List;

public abstract class Pricing {

  private int priceInCents = 0;
  private List<ChargeBasisEntry> chargeBasisEntries = new ArrayList<>();

  public List<ChargeBasisEntry> getChargeBasisEntries() {
    return chargeBasisEntries;
  }

  protected void addChargeBasisEntry(ChargeBasisTag tag, ChargeBasisUnit unit, double quantity, int unitPrice,
      String explanation, int netPrice) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setTag(tag.toString());
    entry.setUnit(unit);
    entry.setQuantity(quantity);
    entry.setUnitPrice(unitPrice);
    entry.setText(explanation);
    entry.setNetPrice(netPrice);
    chargeBasisEntries.add(entry);
  }

  public int getPriceInCents() {
    return priceInCents;
  }

  protected void setPriceInCents(int priceInCents) {
    this.priceInCents = priceInCents;
  }

  /**
   * Add a single location's price with given area and payment class
   *
   * @param locationKey the location's key (unique within application).
   * @param locationArea Location's area in square meters
   * @param paymentClass Payment class: 1,2, or 3.
   */
  public void addLocationPrice(int locationKey, double locationArea, int paymentClass) {
    throw new NotImplementedException("Location price with payment class not implemented in " + this.getClass());
  }

}
