package fi.hel.allu.external.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Traffic arrangement image (liikennejärjestelyn tyyppikuva) metadata")
public class TrafficArrangementImageExt {

  private Integer id;
  private String name;

  public TrafficArrangementImageExt() {
  }

  public TrafficArrangementImageExt(Integer id, String name) {
    this.id = id;
    this.name = name;
  }

  @Schema(description = "Id of the image")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Name of the image")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
