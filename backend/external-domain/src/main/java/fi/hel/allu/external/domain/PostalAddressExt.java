package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Postal address")
public class PostalAddressExt {
  private StreetAddressExt streetAddress;
  private String postalCode;
  private String city;

  public PostalAddressExt() {
    // serialization
  }

  public PostalAddressExt(StreetAddressExt streetAddress, String postalCode, String city) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
  }

  @ApiModelProperty("Street address")
  public StreetAddressExt getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(StreetAddressExt streetAddress) {
    this.streetAddress = streetAddress;
  }

  @ApiModelProperty("Postal code")
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @ApiModelProperty("City")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @JsonIgnore
  public String getStreetAddressAsString() {
    return streetAddress != null ? streetAddress.toString() : null;
  }

  @JsonIgnore
  public void setSimpleStreetAddress(String streetAddress) {
    this.streetAddress = new StreetAddressExt(streetAddress);
  }
}
