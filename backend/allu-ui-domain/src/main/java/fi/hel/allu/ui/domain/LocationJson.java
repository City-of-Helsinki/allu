package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * in Finnish: Hakemuksen sijainti
 */
public class LocationJson {
  private Integer id;
  private Integer locationKey;
  private Integer locationVersion;
  @NotNull(message = "{location.startTime}")
  private ZonedDateTime startTime;
  @NotNull(message = "{location.endTime}")
  private ZonedDateTime endTime;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private Double areaOverride;
  @Valid
  private PostalAddressJson postalAddress;
  private List<Integer> fixedLocationIds;
  private Integer cityDistrictId;
  private Integer cityDistrictIdOverride;
  private Integer paymentTariff;
  private Integer paymentTariffOverride;
  private Boolean underpass;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  /**
   * @return the area in sq. meters
   */
  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  /**
   * @return the area override in sq. meters or null, if override is not set
   */
  public Double getAreaOverride() {
    return areaOverride;
  }

  public void setAreaOverride(Double areaOverride) {
    this.areaOverride = areaOverride;
  }

  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  /**
   * Get the location's area-section address ID.
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
