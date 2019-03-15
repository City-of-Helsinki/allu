package fi.hel.allu.supervision.api.domain;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Charge basis entry (maksuperuste)")
public class ChargeBasisEntryJson {

  private Integer id;
  @NotNull(message = "{application.invoicing.chargebasis.type}")
  private ChargeBasisType type;
  @NotNull(message = "{application.invoicing.chargebasis.unit}")
  private ChargeBasisUnit unit;
  @NotNull(message = "{application.invoicing.chargebasis.quantity}")
  private double quantity;
  @Size(max = 70, message = "{application.invoicing.chargebasis.text}")
  @NotBlank(message = "{application.invoicing.chargebasis.text.required}")
  private String text;
  @Size(max = 5, message = "{application.invoicing.chargebasis.explanation.length}")
  private List<String> explanation;
  private int unitPrice;
  private int netPrice;
  private Integer invoicingPeriodId;
  private String tag;
  private String referredTag;
  private Boolean referrable;
  private Boolean editable;
  @NotNull(message = "{application.invoicing.chargebasis.invoicable}")
  private Boolean invoicable;


  @ApiModelProperty(value = "Id of the entry.", readOnly = true)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Charge type", allowableValues = "AREA_USAGE_FEE,NEGLIGENCE_FEE,ADDITIONAL_FEE,DISCOUNT", required = true)
  public ChargeBasisType getType() {
    return type;
  }

  public void setType(ChargeBasisType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Charge unit", required = true)
  public ChargeBasisUnit getUnit() {
    return unit;
  }

  public void setUnit(ChargeBasisUnit unit) {
    this.unit = unit;
  }

  @ApiModelProperty(value = "Quantity", required = true)
  public double getQuantity() {
    return quantity;
  }

  public void setQuantity(double quantity) {
    this.quantity = quantity;
  }


  @ApiModelProperty(value = "Charge basis text (to invoice)", required = true)
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @ApiModelProperty(value = "Explanation rows (to invoice). Max 5 rows, max row length 70")
  public List<String> getExplanation() {
    return explanation;
  }

  public void setExplanation(List<String> explanation) {
    this.explanation = explanation;
  }

  @ApiModelProperty(value = "Unit price in cents. Required if type is not percent")
  public int getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(int unitPrice) {
    this.unitPrice = unitPrice;
  }

  @ApiModelProperty(value = "Invoicing period ID of the entry. Null if application does not have periods", readOnly = true)
  public Integer getInvoicingPeriodId() {
    return invoicingPeriodId;
  }

  public void setInvoicingPeriodId(Integer invoicingPeriodId) {
    this.invoicingPeriodId = invoicingPeriodId;
  }

  @ApiModelProperty(value = "Tag that can be used to refer single entry within application. Generated in Allu.", readOnly = true)
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @ApiModelProperty(value = "Tag that this entry refers to. Can be used e.g. to apply discount for a another entry.", readOnly = true)
  public String getReferredTag() {
    return referredTag;
  }

  public void setReferredTag(String referredTag) {
    this.referredTag = referredTag;
  }

  @ApiModelProperty(value = "Value indicating whether another entry can refer to this entry", readOnly = true)
  public Boolean getReferrable() {
    return referrable;
  }

  public void setReferrable(Boolean referrable) {
    this.referrable = referrable;
  }

  @ApiModelProperty(value = "Value indicating whether this entry can be modified by user", readOnly = true)
  public Boolean getEditable() {
    return editable;
  }

  public void setEditable(Boolean editable) {
    this.editable = editable;
  }

  @ApiModelProperty(value = "Net price in cents", required = true)
  public int getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(int netPrice) {
    this.netPrice = netPrice;
  }

  @ApiModelProperty(value = "Value indicating whether this entry should be invoiced", required = true)
  public Boolean getInvoicable() {
    return invoicable;
  }

  public void setInvoicable(Boolean invoicable) {
    this.invoicable = invoicable;
  }
}
