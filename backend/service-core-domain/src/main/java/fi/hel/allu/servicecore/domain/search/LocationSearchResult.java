package fi.hel.allu.servicecore.domain.search;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

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

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }
}
