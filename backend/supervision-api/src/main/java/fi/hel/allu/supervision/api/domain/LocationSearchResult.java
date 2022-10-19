package fi.hel.allu.supervision.api.domain;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Location search result")
public class LocationSearchResult {

  private String address;
  private String additionalInfo;
  private Integer cityDistrictId;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;

  public LocationSearchResult(String address, String additionalInfo, Integer cityDistrictId, Geometry geometry) {
    this.address = address;
    this.additionalInfo = additionalInfo;
    this.cityDistrictId = cityDistrictId;
    this.geometry = geometry;
  }

  @Schema(description = "Address of the location")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @Schema(description = "City district ID of the location")
  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }

  @Schema(description =
      "Location geometry in <a href=\"https://tools.ietf.org/html/rfc7946\">GeoJSON</a>")
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  @Schema(description = "Location additional information (tarkennettu sijainti)")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }
}
