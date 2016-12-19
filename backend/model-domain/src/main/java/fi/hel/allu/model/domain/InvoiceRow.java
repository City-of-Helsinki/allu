package fi.hel.allu.model.domain;

public class InvoiceRow {
  private InvoiceUnit unit;
  private double quantity;
  private String rowText;
  private int unitPrice;
  private int netPrice;

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
