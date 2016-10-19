package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

public class Location {
  private Integer id;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private String streetAddress;
  private String postalCode;
  private String city;
  private Integer squareSectionId;

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
   * Get the location's square-section address ID.
   *
   * @return the squareSectionId
   */
  public Integer getSquareSectionId() {
    return squareSectionId;
  }

  public void setSquareSectionId(Integer squareSectionId) {
    this.squareSectionId = squareSectionId;
  }

}
