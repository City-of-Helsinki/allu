package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.model.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.model.domain.serialization.GeometrySerializerProxy;

import org.geolatte.geom.Geometry;

import javax.validation.constraints.NotNull;

public class FixedLocationSectionJson {
  @NotNull
  private Integer id;
  private String name;
  @NotNull
  private ApplicationKind applicationKind;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;

  /**
   * Get the database id for the section
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
   * Get the section name, e.g. "lohko A"
   *
   * @return the section
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the application kind this section is used for.
   *
   * @return the application kind this section is used for.
   */
  public ApplicationKind getApplicationKind() {
    return applicationKind;
  }

  public void setApplicationKind(ApplicationKind applicationKind) {
    this.applicationKind = applicationKind;
  }

  /**
   * Get the geometry for this section.
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
