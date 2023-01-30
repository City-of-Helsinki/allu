package fi.hel.allu.model.domain;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import fi.hel.allu.common.domain.types.ChargeBasisUnit;
import fi.hel.allu.common.types.ChargeBasisType;
import fi.hel.allu.common.util.EmptyUtil;
import org.apache.commons.lang3.StringUtils;

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
  private boolean invoicable;
  private Integer invoicingPeriodId;
  private Integer locationId;

  public ChargeBasisEntry() {
    // for deserialization
    this.invoicable = true;
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
    this.invoicable = true;
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

  public boolean isInvoicable() {
    return invoicable;
  }

  public void setInvoicable(boolean invoicable) {
    this.invoicable = invoicable;
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
    result = prime * result + ((invoicingPeriodId == null) ? 0 : invoicingPeriodId.hashCode());
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
    if (!equalDescriptiveContent(other))
      return false;
    if (invoicingPeriodId == null) {
      return other.invoicingPeriodId == null;
    } else if (!invoicingPeriodId.equals(other.invoicingPeriodId))
      return false;
    return true;
  }

  public boolean equalDescriptiveContent(ChargeBasisEntry other) {
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
    if (type != other.type)
      return false;
    if (unit != other.unit)
      return false;
    if (unitPrice != other.unitPrice)
      return false;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    return true;
  }

  /**
   * Compares the content and tag against another {@code ChargeBasisEntry} instance.
   * The tags are primarily compared as is. However, if invoicingPeriod is not null
   * on either instance, then tag is cut using regex
   * (see function {@link #retrieveTagWithoutInvoicePeriodId()}).
   * @param other {@code ChargeBasisEntry} to compare against
   * @return comparison result
   */
  public boolean equalDescriptiveContentAndTagPrefix(ChargeBasisEntry other) {
    String thisTag = this.tag;
    String otherTag = other.getTag();
    if (invoicingPeriodId != null || other.getInvoicingPeriodId() != null) {
      thisTag = retrieveTagWithoutInvoicePeriodId();
      otherTag = other.retrieveTagWithoutInvoicePeriodId();
    }
    return equalDescriptiveContent(other) &&
      StringUtils.equals(thisTag, otherTag);
  }

  /**
   * Uses regex to remove invoicePeriod from tag.
   * Only affects tags with both locationId and invoicePeriodId.
   * @return handled tag if null returns null
   */
  public String retrieveTagWithoutInvoicePeriodId() {
      return tag == null ? null : stringWithoutInvoicePeriodId(tag);
  }

  public String stringWithoutInvoicePeriodId(String tagToParse) {
    String pattern = "(.*)(#\\d+)(#\\d+)";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(tagToParse);
    if (m.find()) {
      return m.group(1) + m.group(2);
    } else {
      return tagToParse;
    }
  }



  /**
   * Compares content of this object with content of another {@code ChargeBasisEntry}.
   * Locations are not compared.
   * @param other another entry
   * @return true if entry content match, else false
   */
  public boolean equalContent(ChargeBasisEntry other) {
    return equalContent(other, null);
  }

  /**
   * Compares content of this object with content of another {@code ChargeBasisEntry}.
   * Use {@code locationMap} parameter to include location comparison.
   * @param other another entry
   * @param locationMap map should include location of both entries if not set null
   * @return true if entry content match and both have null/non-null locationId, else false
   */
  public boolean equalContent(ChargeBasisEntry other, Map<Integer, Location> locationMap) {
    // Check only content first, as all entries may not have a location.
    // This leads to locationId in ChargeBasisEntry tag not matching at any case.

    if(isUnderPass() && other.isUnderPass()){
        return true;
    }
    if (this.equalDescriptiveContentAndTagPrefix(other)) {
      if (isBothLocationIdsNull(this.getLocationId(), other.locationId)) {
        return true;
      }
      else if (this.getLocationId() != null && other.getLocationId() != null) {
        if (EmptyUtil.isNotEmpty(locationMap)) {
          try {
            return locationMap.get(this.getLocationId())
              .equalGeneralContentAndGeometry(locationMap.get(other.getLocationId()));
          } catch (NullPointerException npe) {
            System.err.println("Failing gracefully at ChargeBasisEntry.equalReplacingChargeBasisEntry: " +
              "Must have not specified locationMap correctly or something unexpected happened");
            npe.printStackTrace();
            // Fail gracefully by returning what would have been returned if locationMap had been null (true).
          }
        }
        return true;
      }
      return false;
    }
    return false;
  }

  public Integer getInvoicingPeriodId() {
    return invoicingPeriodId;
  }

  public void setInvoicingPeriodId(Integer invoicingPeriodId) {
    this.invoicingPeriodId = invoicingPeriodId;
  }

  public Integer getLocationId() {
    return locationId;
  }

  public void setLocationId(Integer locationId) {
    this.locationId = locationId;
  }

  public boolean isUnderPass(){
    return this.text.equalsIgnoreCase("altakuljettava")
      && this.quantity == -50;
  }

  private boolean isBothLocationIdsNull(Integer locationId , Integer otherlocatioId){
    return locationId == null && otherlocatioId == null;
  }

}