package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

import java.util.List;

public class FixedLocationAreaJson {
  @NotNull
  private Integer id;
  @NotBlank
  private String name;
  private List<FixedLocationSectionJson> sections;

  /**
   * Get the database id for the FixedLocationJson
   *
   * @return the id
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Get the area name, e.g. "Rautatientori"
   *
   * @return the area
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * Get the sections in this area
   *
   * @return the sections
   */
  public List<FixedLocationSectionJson> getSections() {
    return sections;
  }

  public void setSections(List<FixedLocationSectionJson> sections) {
    this.sections = sections;
  }

}
