package fi.hel.allu.external.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Allu user")
public class UserExt {

  private String name;
  private String title;

  public UserExt() {
  }

  public UserExt(String name, String title) {
    this.name = name;
    this.title = title;
  }

  @ApiModelProperty(value = "User's name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @ApiModelProperty(value = "User's title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
