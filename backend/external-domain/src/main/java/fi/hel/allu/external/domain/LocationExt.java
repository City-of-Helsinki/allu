package fi.hel.allu.external.domain;

import java.util.List;

import org.geolatte.geom.Geometry;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fi.hel.allu.common.domain.serialization.GeometryDeserializerProxy;
import fi.hel.allu.common.domain.serialization.GeometrySerializerProxy;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application location information")
public class LocationExt {


  @JsonSerialize(using = GeometrySerializerProxy.class)
  @JsonDeserialize(using = GeometryDeserializerProxy.class)
  private Geometry geometry;


  private String paymentTariff;
  private PostalAddressExt address;
  private String cityDistrict;
  private String additionalInfo;
  private Double area;
  private List<Integer> fixedLocationIds;

  @Schema(description = "Location geometry")
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  @Schema(description = "Payment tariff (alueen maksuluokka)")
  public String getPaymentTariff() {
    return paymentTariff;
  }

  public void setPaymentTariff(String paymentTariff) {
    this.paymentTariff = paymentTariff;
  }


  @Schema(description = "Address")
  public PostalAddressExt getAddress() {
    return address;
  }

  public void setAddress(PostalAddressExt address) {
    this.address = address;
  }

  @Schema(description = "City district name")
  public String getCityDistrict() {
    return cityDistrict;
  }

  public void setCityDistrict(String cityDistrict) {
    this.cityDistrict = cityDistrict;
  }

  @Schema(description = "Application area in sq. meters")
  public Double getArea() {
    return area;
  }

  public void setArea(Double area) {
    this.area = area;
  }

  @Schema(description = "Additional information about location")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Schema(description = "Fixed location IDs")
  public List<Integer> getFixedLocationIds() {
    return fixedLocationIds;
  }

  public void setFixedLocationIds(List<Integer> fixedLocationIds) {
    this.fixedLocationIds = fixedLocationIds;
  }


}
