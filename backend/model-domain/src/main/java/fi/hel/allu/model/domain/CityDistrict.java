package fi.hel.allu.model.domain;

import org.geolatte.geom.Geometry;

/**
 * Helsinki city district data.
 */
public class CityDistrict {
  private Integer id;
  private Integer districtId;
  private String name;
  private Geometry geometry;

  /**
   * District database id.
   *
   * @return  District database id.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

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

  /**
   * Geometry of the city district.
   *
   * @return  Geometry of the city district.
   */
  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }
}
