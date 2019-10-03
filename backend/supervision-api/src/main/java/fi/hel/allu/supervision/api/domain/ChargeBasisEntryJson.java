package fi.hel.allu.supervision.api.domain;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Charge basis entry (maksuperuste)",
description = "Examples</br> "
    + "<h3>Add new area usage fee (100m\u00B2, 10€/m\u00B2):</h3>"
    + "<ul>"
    + " <li>type: AREA_USAGE_FEE</li> "
    + " <li>text: Description of the fee</li> "
    + " <li>unit: SQUARE_METER</li> "
    + " <li>unitPrice: 1000</li> "
    + " <li>quantity: 100</li> "
    + " <li>netPrice: 100000</li> "
    + "</ul>"
    + "<h3>Add 100€ discount applied to invoice total</h3>"
    + "<ul>"
    + " <li>type: DISCOUNT</li> "
    + " <li>text: Description of the discount</li> "
    + " <li>unit: PIECE</li> "
    + " <li>unitPrice: -10000</li> "
    + " <li>netPrice: -10000</li> "
    + "</ul>"
    + "<h3>Add 25% discount applied to another entry with tag 'EADF#1'</h3>"
    + "<ul>"
    + " <li>type: DISCOUNT</li> "
    + " <li>text: Description of the discount</li> "
    + " <li>unit: PERCENT</li> "
    + " <li>quantity: -25</li> "
    + " <li>referredTag: EADF#1"
    + "</ul>"
)
@NotFalse(rules = {
    "unitPrice, validUnitPrice, {application.invoicing.chargebasis.unitPrice}",
    "netPrice, validNetPrice, {application.invoicing.chargebasis.netPrice}",
    "unit, validUnit, {application.invoicing.chargebasis.invalid.unit}",
    "quantity, validQuantity, {application.invoicing.chargebasis.quantity}"
 })
public class ChargeBasisEntryJson {

  private Integer id;
  @NotNull(message = "{application.invoicing.chargebasis.type}")
  private ChargeBasisType type;
  @NotNull(message = "{application.invoicing.chargebasis.unit}")
  private ChargeBasisUnit unit;
  private Double quantity;
  @Size(max = 70, message = "{application.invoicing.chargebasis.text}")
  @NotBlank(message = "{application.invoicing.chargebasis.text.required}")
  private String text;
  @Size(max = 5, message = "{application.invoicing.chargebasis.explanation.length}")
  private List<String> explanation;
  private Integer unitPrice;
  private Integer netPrice;
  private Integer invoicingPeriodId;
  private String tag;
  private String referredTag;
  private Boolean referrable;
  private Boolean editable;
  private Boolean invoicable;


  @ApiModelProperty(value = "Id of the entry.", readOnly = true)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Charge type", allowableValues = "AREA_USAGE_FEE,NEGLIGENCE_FEE,ADDITIONAL_FEE,DISCOUNT",
      required = true)
  public ChargeBasisType getType() {
    return type;
  }

  public void setType(ChargeBasisType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Charge unit. For discounts only PERCENT and PIECE (discount in euro cents) "
      + "are allowed and PERCENT is allowed only if type is DISCOUNT", required = true)
  public ChargeBasisUnit getUnit() {
    return unit;
  }

  public void setUnit(ChargeBasisUnit unit) {
    this.unit = unit;
  }

  @ApiModelProperty(value = "Quantity. Ignored for discounts in euro cents. Quantity for percent discount is given "
      + "as negative value.", required = true)
  public Double getQuantity() {
    return quantity;
  }

  public void setQuantity(Double quantity) {
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

  @ApiModelProperty(value = "Unit price in cents. Required if unit is not percent. Discount is given as negative price")
  public Integer getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(Integer unitPrice) {
    this.unitPrice = unitPrice;
  }

  @ApiModelProperty(value = "Invoicing period ID of the entry. Null if application does not have periods",
      readOnly = true)
  public Integer getInvoicingPeriodId() {
    return invoicingPeriodId;
  }

  public void setInvoicingPeriodId(Integer invoicingPeriodId) {
    this.invoicingPeriodId = invoicingPeriodId;
  }

  @ApiModelProperty(value = "Tag that can be used to refer single entry within application. Generated in Allu.",
      readOnly = true)
  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  @ApiModelProperty(value = "Tag that this entry refers to. Applies only to disount entries, otherwise ignored."
      + "<ul>"
      + "<li>If referred tag is given, discount is applied only to entry with referred tag</li>"
      + "<li>If referred tag is not given, discount is applied to invoice total</li>"
      + "</ul>")
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

  @ApiModelProperty(value = "Net price in cents. Required if unit is not percent. Discount is given as negative price", required = true)
  public Integer getNetPrice() {
    return netPrice;
  }

  public void setNetPrice(Integer netPrice) {
    this.netPrice = netPrice;
  }

  @ApiModelProperty(value = "Value indicating whether this entry should be invoiced. Can be changed through separate API", readOnly = true)
  public Boolean getInvoicable() {
    return invoicable;
  }

  public void setInvoicable(Boolean invoicable) {
    this.invoicable = invoicable;
  }

  @JsonIgnore
  public boolean getValidUnitPrice() {
    return unit == ChargeBasisUnit.PERCENT || unitPrice != null;
  }

  @JsonIgnore
  public boolean getValidNetPrice() {
    return unit == ChargeBasisUnit.PERCENT || netPrice != null;
  }

  @JsonIgnore
  public boolean getValidUnit() {
    if (type == ChargeBasisType.DISCOUNT) {
      return unit == ChargeBasisUnit.PERCENT || unit == ChargeBasisUnit.PIECE;
    } else {
      return unit != ChargeBasisUnit.PERCENT;
    }
  }

  @JsonIgnore
  public boolean getValidQuantity() {
    // Quantity not required discount in euro cents
    return (unit == ChargeBasisUnit.PIECE && type == ChargeBasisType.DISCOUNT) || quantity != null;
  }
}
