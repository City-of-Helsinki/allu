package fi.hel.allu.supervision.api.domain;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application location")
public class LocationSearchResult {

  private String address;
  private Integer cityDistrictId;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;

  public LocationSearchResult(String address, Integer cityDistrictId, Geometry geometry) {
    this.address = address;
    this.cityDistrictId = cityDistrictId;
    this.geometry = geometry;
  }

  @ApiModelProperty(value = "Address of the location")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  @ApiModelProperty(value = "City district ID of the location")
  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }

  @ApiModelProperty(value =
      "Location geometry in <a href=\"https://tools.ietf.org/html/rfc7946\">GeoJSON</a>")
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }
}
