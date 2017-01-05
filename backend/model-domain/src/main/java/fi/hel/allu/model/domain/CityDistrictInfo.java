package fi.hel.allu.model.domain;

/**
 * Helsinki city district info. A subset of the @see CityDistrict class. This
 * class is intended to transmit the "need to know only" data to the UI.
 */
public class CityDistrictInfo {
  private Integer districtId;
  private String name;

  /**
   * The official district id.
   *
   * @return  The official district id.
   */
  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }

  /**
   * Name of the city district.
   *
   * @return  Name of the city district.
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
