package fi.hel.allu.external.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import org.geolatte.geom.Geometry;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Allu application location, which is exposed to external users.
 */
public class LocationExt {

  private Integer id;
  private Integer locationKey;
  private Integer locationVersion;
  @NotNull
  private ZonedDateTime startTime;
  @NotNull
  private ZonedDateTime endTime;
  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;
  private Double area;
  private Double areaOverride;
  @Valid
  private PostalAddressExt postalAddress;
  private Integer paymentTariff;
  private Integer paymentTariffOverride;
  private Boolean underpass;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getLocationKey() {
    return locationKey;
  }

  public void setLocationKey(Integer locationKey) {
    this.locationKey = locationKey;
  }

  public Integer getLocationVersion() {
    return locationVersion;
  }

  public void setLocationVersion(Integer locationVersion) {
    this.locationVersion = locationVersion;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  public Double getAreaOverride() {
    return areaOverride;
  }

  public void setAreaOverride(Double areaOverride) {
    this.areaOverride = areaOverride;
  }

  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
  }

  public Integer getPaymentTariff() {
    return paymentTariff;
  }

  public void setPaymentTariff(Integer paymentTariff) {
    this.paymentTariff = paymentTariff;
  }

  public Integer getPaymentTariffOverride() {
    return paymentTariffOverride;
  }

  public void setPaymentTariffOverride(Integer paymentTariffOverride) {
    this.paymentTariffOverride = paymentTariffOverride;
  }

  public Boolean getUnderpass() {
    return underpass;
  }

  public void setUnderpass(Boolean underpass) {
    this.underpass = underpass;
  }
}
