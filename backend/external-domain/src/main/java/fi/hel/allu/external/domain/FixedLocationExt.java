package fi.hel.allu.external.domain;

import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import fi.hel.allu.common.domain.types.ApplicationKind;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Fixed location")
public class FixedLocationExt {

  @NotNull
  private Integer id;
  private String area;
  private String section;
  private ApplicationKind applicationKind;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;

  public FixedLocationExt() {
  }

  public FixedLocationExt(Integer id, String area, String section, ApplicationKind applicationKind, Geometry geometry) {
    this.id = id;
    this.area = area;
    this.section = section;
    this.applicationKind = applicationKind;
    this.geometry = geometry;
  }

  @Schema(description = "Id of the fixed location")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Area of the fixed location (e.g. \"Rautatientori\")")
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  @Schema(description = "Section of the fixed location (e.g. \"lohko A\")")
  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }

  @Schema(description = "Application kind fixed location is used for")
  public ApplicationKind getApplicationKind() {
    return applicationKind;
  }

  public void setApplicationKind(ApplicationKind applicationKind) {
    this.applicationKind = applicationKind;
  }

  @Schema(description = "Geometry of the fixed location")
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

}
