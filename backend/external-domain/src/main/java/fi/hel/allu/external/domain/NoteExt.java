package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.geolatte.geom.Geometry;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NotFalse(rules = {
    "startTime, startTimeNotAfterEndTime, {application.startTime}",
    "applicationKind, kindMatchType, {note.kind}",
    "recurringEndYear, lessThanYearActivity, {application.lessThanYearActivity}"
})

@ApiModel("Note (muistiinpano) input model.")
public class NoteExt implements HasGeometry {

  @NotNull(message = "{application.kind}")
  private ApplicationKind applicationKind;
  private PostalAddressExt postalAddress;
  @NotBlank(message = "{application.name}")
  private String name;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  @NotNull(message = "{application.geometry.missing}")
  private Geometry geometry;
  @NotNull(message = "{application.starttime}")
  private ZonedDateTime startTime;
  @NotNull(message = "{application.endtime}")
  private ZonedDateTime endTime;
  private Integer recurringEndYear;
  private Double area;
  private String description;


  @ApiModelProperty(value = "Application kind.", required = true)
  public ApplicationKind getApplicationKind() {
    return applicationKind;
  }

  public void setApplicationKind(ApplicationKind applicationKind) {
    this.applicationKind = applicationKind;
  }

  @ApiModelProperty(value = "Postal address")
  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
  }

  @ApiModelProperty(value="Name for the note", required = true)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value =
      "Application geometry in <a href=\"https://tools.ietf.org/html/rfc7946\">GeoJSON</a> with following limitations:"
      +"<ul>"
      +"<li>Feature / FeatureCollection is currently not supported, geometry should be given as <a href=\"https://tools.ietf.org/html/rfc7946#section-3.1.8\">GeometryCollection</a>.</li>"
     + "<li>Only named CRS is supported, the given name must either be of the form: urn:ogc:def:crs:EPSG:x.y:4326 (x.y: the version of the EPSG) or of the form EPSG:4326</li>"
      +"</ul>",
  required = true)
  @Override
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  @ApiModelProperty(value = "Start time of the note.", required = true)
  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  @ApiModelProperty(value = "End time of the note.", required = true)
  public ZonedDateTime getEndTime() {
    return endTime;
  }

  @ApiModelProperty(value = "The last year the recurring note is active. Null if note is not recurring.")
  public Integer getRecurringEndYear() {
    return recurringEndYear;
  }

  public void setRecurringEndYear(Integer recurringEndYear) {
    this.recurringEndYear = recurringEndYear;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  @ApiModelProperty(value = "Area in square meters")
  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  @ApiModelProperty(value = "Note description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @JsonIgnore
  public boolean getLessThanYearActivity() {
    if (getRecurringEndYear() != null) {
      return getStartTime().plusYears(1).isAfter(getEndTime());
    }
    return true;
  }

  @JsonIgnore
  public boolean getStartTimeNotAfterEndTime() {
    return !startTime.isAfter(endTime);
  }

  @JsonIgnore
  public boolean getKindMatchType() {
    if (applicationKind != null) {
      return applicationKind.getTypes().contains(ApplicationType.NOTE);
    }
    return true;
  }
}
