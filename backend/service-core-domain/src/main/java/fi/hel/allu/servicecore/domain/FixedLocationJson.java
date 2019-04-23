package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class FixedLocationJson {
  @NotNull
  private Integer id;
  @NotBlank
  private String area;
  private String section;
  @NotNull
  private ApplicationKind applicationKind;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private boolean active;

  /**
   * Get the database id for the FixedLocationJson
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
   * Get the application kind this location is used for.
   *
   * @return the application kind this location is used for.
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

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
