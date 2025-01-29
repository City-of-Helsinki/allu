package fi.hel.allu.model.pricing;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.exception.NotImplementedException;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.model.domain.ChargeBasisEntry;
import fi.hel.allu.model.domain.Location;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Pricing {
  private int priceInCents = 0;
  private final List<ChargeBasisEntry> chargeBasisEntries = new ArrayList<>();

  public List<ChargeBasisEntry> getChargeBasisEntries() {
    return chargeBasisEntries;
  }

  protected void addChargeBasisEntry(ChargeBasisTag tag, ChargeBasisUnit unit, double quantity, int unitPrice,
      String text, int netPrice, List<String> explanation) {
    addChargeBasisEntry(tag, unit, quantity, unitPrice, text, netPrice, explanation, null, null, null);
  }

  protected void addChargeBasisEntry(ChargeBasisTag tag, ChargeBasisUnit unit, double quantity, int unitPrice,
      String text, int netPrice, List<String> explanation, Integer invoicingPeriodId) {
    addChargeBasisEntry(tag, unit, quantity, unitPrice, text, netPrice, explanation, null, invoicingPeriodId, null);
  }


  protected void addChargeBasisEntry(ChargeBasisTag tag, ChargeBasisUnit unit, double quantity, int unitPrice,
      String text, int netPrice) {
    addChargeBasisEntry(tag, unit, quantity, unitPrice, text, netPrice, null, null, null, null);
  }

  protected void addChargeBasisEntry(ChargeBasisUnit unit, double quantity, int unitPrice, String text,
      int netPrice, ChargeBasisTag referredTag) {
    addChargeBasisEntry(null, unit, quantity, unitPrice, text, netPrice, null, referredTag, null, null);
  }

  protected void addChargeBasisEntry(ChargeBasisTag tag, ChargeBasisUnit unit, double quantity, int unitPrice,
      String text, int netPrice, List<String> explanation, ChargeBasisTag referredTag, Integer invoicingPeriodId,
      Integer locationId) {
    ChargeBasisEntry entry = new ChargeBasisEntry();
    entry.setTag(tag == null ? null : tag.toString());
    entry.setType(ChargeBasisType.CALCULATED);
    entry.setUnit(unit);
    entry.setQuantity(quantity);
    entry.setUnitPrice(unitPrice);
    entry.setText(text);
    entry.setNetPrice(netPrice);
    entry.setReferrable(tag != null && tag.isReferrable());
    entry.setReferredTag(referredTag == null ? null : referredTag.toString());
    Optional.ofNullable(explanation).ifPresent(e -> entry.setExplanation(e.toArray(new String[e.size()])));
    entry.setInvoicable(true);
    entry.setInvoicingPeriodId(invoicingPeriodId);
    entry.setLocationId(locationId);
    chargeBasisEntries.add(entry);
  }

  public int getPriceInCents() {
    return priceInCents;
  }

  protected void setPriceInCents(int priceInCents) {
    this.priceInCents = priceInCents;
  }

  /**
   * Add a single location's price with given location
   */
  public void addLocationPrice(Location location, ZonedDateTime startTime) {
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
