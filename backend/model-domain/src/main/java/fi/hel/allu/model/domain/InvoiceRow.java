package fi.hel.allu.model.domain;

public class InvoiceRow {
  private String tag;
  private String referredTag;
  private boolean manuallySet;
  private InvoiceUnit unit;
  private double quantity;
  private String rowText;
  private int unitPrice;
  private int netPrice;

  public InvoiceRow() {
    // for deserialization
  }

  public InvoiceRow(String tag, String referredTag, boolean manuallySet, InvoiceUnit unit, double quantity,
      String rowText, int unitPrice, int netPrice) {
    this.tag = tag;
    this.referredTag = referredTag;
    this.manuallySet = manuallySet;
    this.unit = unit;
    this.quantity = quantity;
    this.rowText = rowText;
    this.unitPrice = unitPrice;
    this.netPrice = netPrice;
  }

  /**
   * Get the invoice row's tag that can be used to refer to single invoice row
   * within invoice. Tag must be generated systematically so that database
   * migrations are possible.
   */
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  /**
   * Get the tag of the invoice row that this manually set invoice row refers
   * to. Used to manipulate the value of calculated row by applying a multiplier
   * to it.
   */
  public String getReferredTag() {
    return referredTag;
  }

  public void setReferredTag(String referredTag) {
    this.referredTag = referredTag;
  }

  /**
   * Get the invoice tag of the (automatic) invoice row that this (manual) row
   */
  /**
   * Was the row manually set? Manually set rows don't get overridden when
   * pricing is recalculated.
   *
   * @return true if row was manually set
   */
  public boolean getManuallySet() {
    return manuallySet;
  }

  public void setManuallySet(boolean manuallySet) {
    this.manuallySet = manuallySet;
  }

  /**
   * Get the unit for the row
   */
  public InvoiceUnit getUnit() {
    return unit;
  }

  public void setUnit(InvoiceUnit unit) {
    this.unit = unit;
  }

  /**
   * Get the amount of units for the row
   */
  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  /**
   * Get the invoice row's explanatory text
   */
  public String getRowText() {
    return rowText;
  }

  public void setRowText(String rowText) {
    this.rowText = rowText;
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
   * Get the row's net price in cents. Can be negative. <em>To avoid possible
   * rounding errors, this is not a calculated value</em>.
   */
  public int getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(int netPrice) {
    this.netPrice = netPrice;
  }
}
