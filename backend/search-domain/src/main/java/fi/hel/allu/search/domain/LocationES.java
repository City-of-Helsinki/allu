package fi.hel.allu.search.domain;

/**
 * ElasticSearch mapping for location.
 */
public class LocationES {
  private String streetAddress;
  private String postalCode;
  private String city;
  private Integer cityDistrictId;
  private String additionalInfo;
  private String address;

  public LocationES() {
    // for JSON serialization
  }

  public LocationES(String streetAddress, String postalCode, String city, Integer cityDistrictId,
      String additionalInfo) {
    this.streetAddress = streetAddress;
    this.postalCode = postalCode;
    this.city = city;
    this.cityDistrictId = cityDistrictId;
    this.additionalInfo = additionalInfo;
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
}
