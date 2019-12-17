package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Invoice row")
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


  @ApiModelProperty(value = "Unit of the invoice row")
  public ChargeBasisUnit getUnit() {
    return unit;
  }

  public void setUnit(ChargeBasisUnit unit) {
    this.unit = unit;
  }

  @ApiModelProperty(value = "Amount of units on this row")
  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }

  @ApiModelProperty(value = "Explanatory text for the row")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @ApiModelProperty(value = "Explanation texts for the row")
  public String[] getExplanation() {
    return explanation == null ? new String[0] : explanation;
  }

  public void setExplanation(String[] explanation) {
    this.explanation = explanation;
  }

  @ApiModelProperty(value = "Price for one unit, in cents")
  public int getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(int unitPrice) {
    this.unitPrice = unitPrice;
  }

  @ApiModelProperty(value = "Total price for this row, in cents")
  public int getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(int netPrice) {
    this.netPrice = netPrice;
  }

}
