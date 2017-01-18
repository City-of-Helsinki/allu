package fi.hel.allu.search.domain;

/**
 * ElasticSearch mapping for location.
 */
public class LocationES {
  private String streetAddress;
  private String postalCode;
  private String city;
  private Integer districtId;

  public LocationES() {
    // for JSON serialization
  }

  public LocationES(String streetAddress, String postalCode, String city, Integer districtId) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.districtId = districtId;
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

  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }
}
