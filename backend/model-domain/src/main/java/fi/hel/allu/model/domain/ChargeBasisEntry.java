package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;
import java.util.Arrays;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;

public class ChargeBasisEntry {
  private Integer id;
  private String tag;
  private String referredTag;
  private boolean manuallySet;
  @NotNull(message = "application.invoicing.chargebasis.type")
  private ChargeBasisType type;
  private ChargeBasisUnit unit;
  private double quantity;
  @Size(max = 70, message = "application.invoicing.chargebasis.text")
  private String text;
  @Size(max = 5, message = "application.invoicing.chargebasis.explanation.length")
  private String[] explanation;
  private int unitPrice;
  private int netPrice;
  private ZonedDateTime modificationTime;
  private Boolean locked;
  private boolean referrable;

  public ChargeBasisEntry() {
    // for deserialization
  }

  public ChargeBasisEntry(String tag, String referredTag, boolean manuallySet, ChargeBasisType type,
    ChargeBasisUnit unit, double quantity, String text, String[] explanation, int unitPrice, int netPrice) {
    this.tag = tag;
    this.referredTag = referredTag;
    this.manuallySet = manuallySet;
    this.type = type;
    this.unit = unit;
    this.quantity = quantity;
    this.text = text;
    this.explanation = explanation;
    this.unitPrice = unitPrice;
    this.netPrice = netPrice;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
   * Type of this charge basis entry
   */
  public ChargeBasisType getType() {
    return type;
  }

  public void setType(ChargeBasisType type) {
    this.type = type;
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
   * Get the entry's text
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

  public ZonedDateTime getModificationTime() {
    return modificationTime;
  }

  public void setModificationTime(ZonedDateTime modificationTime) {
    this.modificationTime = modificationTime;
  }

  public Boolean getLocked() {
    return locked;
  }

  public void setLocked(Boolean locked) {
    this.locked = locked;
  }

  public boolean isReferrable() {
     return referrable;
  }

  public void setReferrable(boolean referrable) {
      this.referrable = referrable;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(explanation);
    result = prime * result + (manuallySet ? 1231 : 1237);
    result = prime * result + netPrice;
    long temp;
    temp = Double.doubleToLongBits(quantity);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + (referrable ? 1231 : 1237);
    result = prime * result + ((referredTag == null) ? 0 : referredTag.hashCode());
    result = prime * result + ((tag == null) ? 0 : tag.hashCode());
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((unit == null) ? 0 : unit.hashCode());
    result = prime * result + unitPrice;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ChargeBasisEntry other = (ChargeBasisEntry) obj;
    if (!Arrays.equals(explanation, other.explanation))
      return false;
    if (manuallySet != other.manuallySet)
      return false;
    if (netPrice != other.netPrice)
      return false;
    if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity))
      return false;
    if (referrable != other.referrable)
      return false;
    if (referredTag == null) {
      if (other.referredTag != null)
        return false;
    } else if (!referredTag.equals(other.referredTag))
      return false;
    if (tag == null) {
      if (other.tag != null)
        return false;
    } else if (!tag.equals(other.tag))
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    if (type != other.type)
      return false;
    if (unit != other.unit)
      return false;
    if (unitPrice != other.unitPrice)
      return false;
    return true;
  }
}
