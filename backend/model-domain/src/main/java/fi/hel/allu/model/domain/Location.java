package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Optional;

public class Location extends AbstractLocation implements PostalAddressItem, LocationInterface {
  private Integer applicationId;
  private Integer locationVersion;
  private String additionalInfo;
  private Double area;
  private Double areaOverride;
  private PostalAddress postalAddress;
  private List<Integer> fixedLocationIds;
  private Integer cityDistrictId;
  private Integer cityDistrictIdOverride;
  private String paymentTariffOverride;

  public Location() {
  }

  /**
   * @return  The application id related to this location.
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Returns the version of the location. If location is updated, the new version will get higher version number than the previous.
   * Location's application + location key + location version is unique.
   *
   * @return  Returns the version of the location.
   */
  public Integer getLocationVersion() {
    return locationVersion;
  }

  public void setLocationVersion(Integer locationVersion) {
    this.locationVersion = locationVersion;
  }

  /**
   * Get additional info for the location
   */
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  /**
   * Get the area in square meters
   *
   * @return the area
   */
  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  /**
   * Get the area override in square meters if override is set
   *
   * @return the override or null
   */
  public Double getAreaOverride() {
    return areaOverride;
  }

  public void setAreaOverride(Double areaOverride) {
    this.areaOverride = areaOverride;
  }

  /**
   * Get the effective area: if override is set, return the override value,
   * otherwise return the area.
   *
   * @return effective area
   */
  @JsonIgnore
  public Double getEffectiveArea() {
    if (areaOverride != null) {
      return areaOverride;
    }
    return area;
  }

  /**
   * Returns the postal address of the location.
   *
   * @return  the postal address of the location. May be <code>null</code>.
   */
  @Override
  public PostalAddress getPostalAddress() {
    return postalAddress;
  }

  @Override
  public void setPostalAddress(PostalAddress postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * Get the location's fixed-location address ID.
   *
   * @return the fixedLocationId
   */
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }

  /**
   * Get the location's calculated district ID.
   *
   * @return district ID or null
   */
  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }

  /**
   * Get the locations user-overridden district ID
   *
   * @return district ID or null
   */
  public Integer getCityDistrictIdOverride() {
    return cityDistrictIdOverride;
  }

  public void setCityDistrictIdOverride(Integer cityDistrictIdOverride) {
    this.cityDistrictIdOverride = cityDistrictIdOverride;
  }

  /**
   * Get the effective city district id
   *
   * @return
   */
  @JsonIgnore
  public Integer getEffectiveCityDistrictId() {
    if (cityDistrictIdOverride != null) {
      return cityDistrictIdOverride;
    }
    return cityDistrictId;
  }

  /**
   * Returns the user overridden payment tariff (maksuluokka) of the location.
   *
   * @return  the user overridden payment tariff (maksuluokka) of the location or <code>null</code>.
   */
  public String getPaymentTariffOverride() {
    return paymentTariffOverride;
  }

  public void setPaymentTariffOverride(String paymentTariffOverride) {
    this.paymentTariffOverride = paymentTariffOverride;
  }

  @JsonIgnore
  public String getEffectivePaymentTariff() {
    if (paymentTariffOverride != null) {
      return paymentTariffOverride;
    }
    return getPaymentTariff();
  }

  /**
   * Check if some content is equal to other {@code Location} content, including some AbstractLocation content.
   * @param other location to compare with
   * @return true if content is same, else false
   */
  public boolean equalGeneralContentAndGeometry(Location other) {
    if (this.area == null || !this.area.equals(other.getArea()))
      return false;
    if (this.postalAddress == null || !this.postalAddress.equals(other.getPostalAddress()))
      return false;
    if (this.cityDistrictId == null || !this.cityDistrictId.equals(other.getCityDistrictId()))
      return false;
    // Check variables from AbstractLocation here since customerReportingTime may be null.
    // We want to check general data anyways.
    if (getStartTime() == null || !getStartTime().isEqual(other.getStartTime()))
      return false;
    if (getEndTime() == null || !getEndTime().isEqual(other.getEndTime()))
      return false;
    if (getPaymentTariff() == null || !getPaymentTariff().equals(other.getPaymentTariff()))
      return false;
    if (getUnderpass() == null || !getUnderpass().equals(other.getUnderpass()))
      return false;
    return this.equalGeometry(other.getGeometry());
  }

  /**
   * Check if this location exists in provided list using {@link #equalGeneralContentAndGeometry},
   * and returns any location as optional.
   * @param list locations on which comparison is done
   * @return optional location if general content and geometry match any location in list,
   * else empty optional.
   */
  public Optional<Location> getOptionalLocationIfExistsInList(List<Location> list) {
    return list.stream().filter(l -> l.equalGeneralContentAndGeometry(this)).findAny();
  }
}