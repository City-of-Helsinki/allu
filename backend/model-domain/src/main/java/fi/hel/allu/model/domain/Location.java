package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

import java.util.List;

public class Location {
  private Integer id;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private Double areaOverride;
  private String streetAddress;
  private String postalCode;
  private String city;
  private List<Integer> fixedLocationIds;
  private Integer districtId;
  private Integer districtIdOverride;

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
  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }

  /**
   * Get the locations user-overridden district ID
   *
   * @return district ID or null
   */
  public Integer getDistrictIdOverride() {
    return districtIdOverride;
  }

  public void setDistrictIdOverride(Integer districtIdOverride) {
    this.districtIdOverride = districtIdOverride;
  }

}
