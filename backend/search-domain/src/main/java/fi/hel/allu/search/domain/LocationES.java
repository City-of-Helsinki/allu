package fi.hel.allu.search.domain;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;

/**
 * ElasticSearch mapping for location.
 */
public class LocationES {
  private Integer locationKey;
  private String streetAddress;
  private String postalCode;
  private String city;
  private Integer cityDistrictId;
  private String additionalInfo;
  private String address;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;

  /**
   * ElasticSearch uses different coordinate system. Save search coordinates
   * in separate field to avoid need to transform result coordinates back to Allu coordinate system
   */
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry searchGeometry;


  public LocationES() {
    // for JSON serialization
  }

  public LocationES(Integer locationKey, String streetAddress, String postalCode, String city, Integer cityDistrictId,
      String additionalInfo) {
    this.locationKey = locationKey;
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.cityDistrictId = cityDistrictId;
    this.additionalInfo = additionalInfo;
  }

  public Integer getLocationKey() {
    return locationKey;
  }

  public void setLocationKey(Integer locationKey) {
    this.locationKey = locationKey;
  }

  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public Geometry getSearchGeometry() {
    return searchGeometry;
  }

  public void setSearchGeometry(Geometry searchGeometry) {
    this.searchGeometry = searchGeometry;
  }

  @JsonProperty(access = Access.READ_ONLY)
  public String getExtendedAddress() {
    return Stream.of(address, additionalInfo)
        .filter(s -> StringUtils.isNotBlank(s))
        .collect(Collectors.joining(" "));
  }
}
