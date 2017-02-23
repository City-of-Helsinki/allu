package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

import java.util.List;

public class Location {
  private Integer id;
  private Integer applicationId;
  private Integer locationKey;
  private Integer locationVersion;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private Double areaOverride;
  private String streetAddress;
  private String postalCode;
  private String city;
  private List<Integer> fixedLocationIds;
  private Integer cityDistrictId;
  private Integer cityDistrictIdOverride;
  private Integer paymentTariff;
  private Integer paymentTariffOverride;
  private Boolean underpass;

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
   * Get location's street address, e.g. "Mannerheimintie 3"
   *
   * @return street address
   */
  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  /**
   * Get the location's postal (zip) code.
   *
   * @return the postal code.
   */
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  /**
   * Get the city for the location.
   *
   * @return city name.
   */
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
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
   * Returns the calculated payment tariff (maksuluokka) of the location.
   *
   * @return  the calculated payment tariff (maksuluokka) of the location or <code>null</code>.
   */
  public Integer getPaymentTariff() {
    return paymentTariff;
  }

  public void setPaymentTariff(Integer paymentTariff) {
    this.paymentTariff = paymentTariff;
  }

  /**
   * Returns the user overridden payment tariff (maksuluokka) of the location.
   *
   * @return  the user overridden payment tariff (maksuluokka) of the location or <code>null</code>.
   */
  public Integer getPaymentTariffOverride() {
    return paymentTariffOverride;
  }

  public void setPaymentTariffOverride(Integer paymentTariffOverride) {
    this.paymentTariffOverride = paymentTariffOverride;
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
}
