package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class FixedLocationJson {
  @NotNull
  Integer id;
  @NotBlank
  String area;
  String section;

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
  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  /**
   * Get the section name, e.g. "lohko A"
   *
   * @return the section
   */
  public String getSection() {
    return section;
  }

  public void setSection(String section) {
    this.section = section;
  }
}
