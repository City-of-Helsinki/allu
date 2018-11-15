package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application handler (hakemuksen käsittelijä)")
public class HandlerExt {

  private String name;
  private String title;

  public HandlerExt() {
  }

  public HandlerExt(String name, String title) {
    this.name = name;
    this.title = title;
  }

  @ApiModelProperty(value = "Handler name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "Handler title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
