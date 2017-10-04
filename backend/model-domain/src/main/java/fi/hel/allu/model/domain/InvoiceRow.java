package fi.hel.allu.model.domain;

/**
 * One line in an invoice
 */
public class InvoiceRow {
  private ChargeBasisUnit unit;
  private double quantity;
  private String text;
  private int unitPrice;
  private int netPrice;

  public InvoiceRow(ChargeBasisUnit unit, double quantity, String text, int unitPrice, int netPrice) {
    this.unit = unit;
    this.quantity = quantity;
    this.text = text;
    this.unitPrice = unitPrice;
    this.netPrice = netPrice;
  }

  public InvoiceRow() {
    // for deserialization
  }

  /**
   * Get the unit used in this row
   */
  public ChargeBasisUnit getUnit() {
    return unit;
  }

  public void setUnit(ChargeBasisUnit unit) {
    this.unit = unit;
  }

  /**
   * Get the amount of units on this row
   * 
   * @return
   */
  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  /**
   * Get the explanatory text for the row
   */
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * Get the price for one unit, in cents
   */
  public int getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(int unitPrice) {
    this.unitPrice = unitPrice;
  }

  /**
   * Get the total price for this row, in cents
   */
  public int getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(int netPrice) {
    this.netPrice = netPrice;
  }

}
