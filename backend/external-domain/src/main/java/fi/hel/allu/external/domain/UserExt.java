package fi.hel.allu.external.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Allu user")
public class UserExt {

  private String name;
  private String title;

  public UserExt() {
  }

  public UserExt(String name, String title) {
    this.name = name;
    this.title = title;
  }

  @Schema(description = "User's name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Schema(description = "User's title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

}
