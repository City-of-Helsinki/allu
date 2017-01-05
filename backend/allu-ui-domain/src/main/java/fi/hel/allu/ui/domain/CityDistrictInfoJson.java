package fi.hel.allu.ui.domain;

/**
 * Helsinki city district info.
 */
public class CityDistrictInfoJson {
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
