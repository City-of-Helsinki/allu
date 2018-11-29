package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

import java.time.ZonedDateTime;
import java.util.List;

public class Location implements PostalAddressItem {
  private Integer id;
  private Integer applicationId;
  private Integer locationKey;
  private Integer locationVersion;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private String additionalInfo;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private Double areaOverride;
  private PostalAddress postalAddress;
  private List<Integer> fixedLocationIds;
  private Integer cityDistrictId;
  private Integer cityDistrictIdOverride;
  private String paymentTariff;
  private String paymentTariffOverride;
  private Boolean underpass;
  private ZonedDateTime customerStartTime;
  private ZonedDateTime customerEndTime;
  private ZonedDateTime customerReportingTime;

  /**
   * Get location's database ID
   *
   * @return the ID
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
   * Returns the human readable name of location (actually a number, which can be converted into a name). Each new location for one
   * application gets a key greater than the previous key. In case there are locations 1,2 and 3 and 2 is deleted and a new location is
   * added, the new location gets key 4.
   *
   * @return  Returns the human readable name of location (actually a number, which can be converted into a name).
   */
  public Integer getLocationKey() {
    return locationKey;
  }

  public void setLocationKey(Integer locationKey) {
    this.locationKey = locationKey;
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
   * Returns the time location use starts.
   *
   * @return  the time location use starts.
   */
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  /**
   * Returns the time location use ends.
   *
   * @return  the time location use ends.
   */
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
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
   * Get the geometry for the location
   *
   * @return the geometry
   */
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
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
   * Returns the calculated payment tariff (maksuluokka) of the location.
   *
   * @return the calculated payment tariff (maksuluokka) of the location or
   *         <code>null</code>.
   */
  public String getPaymentTariff() {
    return paymentTariff;
  }

  public void setPaymentTariff(String paymentTariff) {
    this.paymentTariff = paymentTariff;
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
    return paymentTariff;
  }

  /**
   * Returns true if it's possible to pass through the reserved area without obstacles (altakuljettava).
   *
   * @return  true if it's possible to pass through the reserved area without obstacles (altakuljettava).
   */
  public Boolean getUnderpass() {
    return underpass;
  }

  public void setUnderpass(Boolean underpass) {
    this.underpass = underpass;
  }

  public ZonedDateTime getCustomerStartTime() {
    return customerStartTime;
  }

  public void setCustomerStartTime(ZonedDateTime customerStartTime) {
    this.customerStartTime = customerStartTime;
  }

  public ZonedDateTime getCustomerEndTime() {
    return customerEndTime;
  }

  public void setCustomerEndTime(ZonedDateTime customerEndTime) {
    this.customerEndTime = customerEndTime;
  }

  public ZonedDateTime getCustomerReportingTime() {
    return customerReportingTime;
  }

  public void setCustomerReportingTime(ZonedDateTime customerReportingTime) {
    this.customerReportingTime = customerReportingTime;
  }
}
