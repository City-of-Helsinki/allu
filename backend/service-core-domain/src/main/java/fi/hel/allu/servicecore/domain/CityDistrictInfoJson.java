package fi.hel.allu.servicecore.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "City district information")
public class CityDistrictInfoJson {
  private Integer id;
  private Integer districtId;
  private String name;

  @Schema(description = "Internal ID of the city district")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Official city district ID")
  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }

  @Schema(description = "Name of the city district")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
