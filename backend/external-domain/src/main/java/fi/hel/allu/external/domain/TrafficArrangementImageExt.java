package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Traffic arrangement image (liikennejärjestelyn tyyppikuva) metadata")
public class TrafficArrangementImageExt {

  private Integer id;
  private String name;

  public TrafficArrangementImageExt() {
  }

  public TrafficArrangementImageExt(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  @ApiModelProperty(value = "Id of the image")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Name of the image")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
