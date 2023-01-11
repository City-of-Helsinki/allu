package fi.hel.allu.servicecore.domain;

import fi.hel.allu.model.domain.PostalAddressInterface;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * in Finnish: Osoitteen tiedot
 */
@Schema(description = "Postal address")
public class PostalAddressJson implements PostalAddressInterface {
  private String streetAddress;
  private String postalCode;
  private String city;

  public PostalAddressJson() {
    // serialization
  }

  public PostalAddressJson(String streetAddress, String postalCode, String city) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
  }

  @Schema(description = "Street address")
  public String getStreetAddress() {
    return streetAddress;
  }

  public void setStreetAddress(String streetAddress) {
    this.streetAddress = streetAddress;
  }

  @Schema(description = "Postal code")
  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  @Schema(description = "City")
  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PostalAddressJson that = (PostalAddressJson) o;

    if (streetAddress != null ? !streetAddress.equals(that.streetAddress) : that.streetAddress != null) return false;
    if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) return false;
    return city != null ? city.equals(that.city) : that.city == null;
  }

  @Override
  public int hashCode() {
    int result = streetAddress != null ? streetAddress.hashCode() : 0;
    result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
    result = 31 * result + (city != null ? city.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "PostalAddressJson{" +
        "streetAddress='" + streetAddress + '\'' +
        ", postalCode='" + postalCode + '\'' +
        ", city='" + city + '\'' +
        '}';
  }
}