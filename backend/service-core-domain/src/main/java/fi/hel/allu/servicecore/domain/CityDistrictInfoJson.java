package fi.hel.allu.servicecore.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "City district information")
public class CityDistrictInfoJson {
  private Integer id;
  private Integer districtId;
  private String name;

  @ApiModelProperty(value = "Internal ID of the city district")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Official city district ID")
  public Integer getDistrictId() {
    return districtId;
  }

  public void setDistrictId(Integer districtId) {
    this.districtId = districtId;
  }

  @ApiModelProperty(value = "Name of the city district")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
