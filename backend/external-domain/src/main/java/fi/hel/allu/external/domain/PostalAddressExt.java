package fi.hel.allu.external.domain;

/**
 * Allu application location, which is exposed to external users.
 */
public class PostalAddressExt {
  private String streetAddress;
  private String postalCode;
  private String city;

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
}
