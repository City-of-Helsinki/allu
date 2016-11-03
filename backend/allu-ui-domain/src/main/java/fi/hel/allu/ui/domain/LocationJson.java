package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

import javax.validation.Valid;

/**
 * inFinnish: Hakemuksen sijainti
 */
public class LocationJson {
  private Integer id;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  @Valid
  private PostalAddressJson postalAddress;
  private Integer fixedLocationId;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
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
  public Integer getFixedLocationId() {
    return fixedLocationId;
  }

  public void setFixedLocationId(Integer fixedLocationId) {
    this.fixedLocationId = fixedLocationId;
  }
}
