package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Invoice row")
public class InvoiceRowJson {
  private ChargeBasisUnit unit;
  private double quantity;
  private String text;
  private String[] explanation;
  private int unitPrice;
  private int netPrice;

  public InvoiceRowJson(ChargeBasisUnit unit, double quantity, String text, String[] explanation, int unitPrice, int netPrice) {
    this.unit = unit;
    this.quantity = quantity;
    this.text = text;
    this.explanation = explanation;
    this.unitPrice = unitPrice;
    this.netPrice = netPrice;
  }

  public InvoiceRowJson() {
    // for deserialization
  }


  @Schema(description = "Unit of the invoice row")
  public ChargeBasisUnit getUnit() {
    return unit;
  }

  public void setUnit(ChargeBasisUnit unit) {
    this.unit = unit;
  }

  @Schema(description = "Amount of units on this row")
  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  @Schema(description = "Explanatory text for the row")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Schema(description = "Explanation texts for the row")
  public String[] getExplanation() {
    return explanation == null ? new String[0] : explanation;
  }

  public void setExplanation(String[] explanation) {
    this.explanation = explanation;
  }

  @Schema(description = "Price for one unit, in cents")
  public int getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(int unitPrice) {
    this.unitPrice = unitPrice;
  }

  @Schema(description = "Total price for this row, in cents")
  public int getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(int netPrice) {
    this.netPrice = netPrice;
  }

}
