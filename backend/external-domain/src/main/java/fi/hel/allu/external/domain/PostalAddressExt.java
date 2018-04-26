package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Postal address")
public class PostalAddressExt {
  private String streetAddress;
  private String postalCode;
  private String city;

  public PostalAddressExt() {
    // serialization
  }

  public PostalAddressExt(String streetAddress, String postalCode, String city) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
  }

  @ApiModelProperty("Street address")
  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
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
}
