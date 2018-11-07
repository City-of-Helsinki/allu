package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.exception.NotImplementedException;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.domain.ChargeBasisEntry;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Pricing {
  protected static final String UNDEFINED_PAYMENT_CLASS = "undefined";

  private int priceInCents = 0;
  private List<ChargeBasisEntry> chargeBasisEntries = new ArrayList<>();

  public List<ChargeBasisEntry> getChargeBasisEntries() {
    return chargeBasisEntries;
  }

  protected void addChargeBasisEntry(ChargeBasisTag tag, ChargeBasisUnit unit, double quantity, int unitPrice,
      String text, int netPrice, List<String> explanation) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setTag(tag == null ? null : tag.toString());
    entry.setType(ChargeBasisType.CALCULATED);
    entry.setUnit(unit);
    entry.setQuantity(quantity);
    entry.setUnitPrice(unitPrice);
    entry.setText(text);
    entry.setNetPrice(netPrice);
    entry.setReferrable(tag != null && tag.isReferrable());
    Optional.ofNullable(explanation).ifPresent(e -> entry.setExplanation(e.toArray(new String[e.size()])));
    entry.setInvoicable(true);
    chargeBasisEntries.add(entry);
  }

  protected void addChargeBasisEntry(ChargeBasisTag tag, ChargeBasisUnit unit, double quantity, int unitPrice,
      String text, int netPrice) {
    addChargeBasisEntry(tag, unit, quantity, unitPrice, text, netPrice, null);
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
  public void addLocationPrice(int locationKey, double locationArea, String paymentClass) {
    throw new NotImplementedException("Location price with payment class not implemented in " + this.getClass());
  }

  /**
   * Convert a chrono unit to a chage basis unit
   *
   * @param unit
   * @return
   */
  protected ChargeBasisUnit toChargeBasisUnit(ChronoUnit unit) {
    switch (unit) {
      case DAYS:
        return ChargeBasisUnit.DAY;
      case HOURS:
        return ChargeBasisUnit.HOUR;
      case WEEKS:
        return ChargeBasisUnit.WEEK;
      case MONTHS:
        return ChargeBasisUnit.MONTH;
      case YEARS:
        return ChargeBasisUnit.YEAR;
      default:
        // Unknown units are handled as pieces
        return ChargeBasisUnit.PIECE;
    }
  }

}
