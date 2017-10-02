package fi.hel.allu.model.domain;

public class ChargeBasisEntry {
  private String tag;
  private String referredTag;
  private boolean manuallySet;
  private ChargeBasisUnit unit;
  private double quantity;
  private String text;
  private int unitPrice;
  private int netPrice;

  public ChargeBasisEntry() {
    // for deserialization
  }

  public ChargeBasisEntry(String tag, String referredTag, boolean manuallySet, ChargeBasisUnit unit, double quantity,
      String text, int unitPrice, int netPrice) {
    this.tag = tag;
    this.referredTag = referredTag;
    this.manuallySet = manuallySet;
    this.unit = unit;
    this.quantity = quantity;
    this.text = text;
    this.unitPrice = unitPrice;
    this.netPrice = netPrice;
  }

  /**
   * Get the tag that can be used to refer to single charge basis entry within
   * application. Tag must be generated systematically so that database
   * migrations are possible.
   */
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * Get the tag of the charge basis entry that this manually set entry refers
   * to. Used to manipulate the value of calculated entry by applying a
   * multiplier to it.
   */
  public String getReferredTag() {
    return referredTag;
  }

  public void setReferredTag(String referredTag) {
    this.referredTag = referredTag;
  }

  /**
   * Was the entry manually set? Manually set entries don't get overridden when
   * pricing is recalculated.
   *
   * @return true if entry was manually set
   */
  public boolean getManuallySet() {
    return manuallySet;
  }

  public void setManuallySet(boolean manuallySet) {
    this.manuallySet = manuallySet;
  }

  /**
   * Get the unit for the entry
   */
  public ChargeBasisUnit getUnit() {
    return unit;
  }

  public void setUnit(ChargeBasisUnit unit) {
    this.unit = unit;
  }

  /**
   * Get the amount of units for the entry
   */
  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  /**
   * Get the entry's explanatory text
   */
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * Get the price per unit, in cents. Can be negative also (for discounts)
   */
  public int getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(int unitPrice) {
    this.unitPrice = unitPrice;
  }

  /**
   * Get the entry's net price in cents. Can be negative. <em>To avoid possible
   * rounding errors, this is not a calculated value</em>.
   */
  public int getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(int netPrice) {
    this.netPrice = netPrice;
  }
}
