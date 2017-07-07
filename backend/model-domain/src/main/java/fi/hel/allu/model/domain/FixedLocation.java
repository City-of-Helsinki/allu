package fi.hel.allu.model.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

public class FixedLocation {
  private Integer id;
  private String area;
  private String section;
  private ApplicationKind applicationKind;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;

  /**
   * Get the database id for the FixedLocation
   *
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Get the area name, e.g. "Rautatientori"
   *
   * @return the area
   */
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  /**
   * Get the section name, e.g. "lohko A"
   *
   * @return the section
   */
  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  /**
   * Get the application kind this fixed location is valid for.
   *
   * @return the application kind this fixed location is valid for.
   */
  public ApplicationKind getApplicationKind() {
    return applicationKind;
  }

  public void setApplicationKind(ApplicationKind applicationKind) {
    this.applicationKind = applicationKind;
  }

  /**
   * Get the geometry for this fixed location.
   *
   * @return the geometry
   */
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }
}
