package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;

/**
 * One line in an invoice
 */
public class InvoiceRow {
  Integer id;
  private Integer chargeBasisId;
  private ChargeBasisUnit unit;
  private double quantity;
  private String text;
  private String[] explanation;
  private int unitPrice;
  private int netPrice;

  public InvoiceRow(Integer chargeBasisId, ChargeBasisUnit unit, double quantity, String text, String[] explanation, int unitPrice,
      int netPrice) {
    this.chargeBasisId = chargeBasisId;
    this.unit = unit;
    this.quantity = quantity;
    this.text = text;
    this.explanation = explanation;
    this.unitPrice = unitPrice;
    this.netPrice = netPrice;
  }

  public InvoiceRow() {
    // for deserialization
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
   * Get the text for the row
   */
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  /**
   * Get the explanation texts for the row (note: SAP only supports 5 pieces of
   * explanatory texts per row)
   */
  public String[] getExplanation() {
    return explanation == null ? new String[0] : explanation;
  }

  public void setExplanation(String[] explanation) {
    this.explanation = explanation;
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

  public Integer getChargeBasisId() {
    return chargeBasisId;
  }

  public void setChargeBasisId(Integer chargeBasisId) {
    this.chargeBasisId = chargeBasisId;
  }

}
