package fi.hel.allu.search.domain;

/**
 * ElasticSearch mapping for location.
 */
public class LocationES {
  private String streetAddress;
  private String postalCode;
  private String city;
  private Integer cityDistrictId;

  public LocationES() {
    // for JSON serialization
  }

  public LocationES(String streetAddress, String postalCode, String city, Integer cityDistrictId) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.cityDistrictId = cityDistrictId;
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
}
