package fi.hel.allu.ui.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class SquareSectionJson {
  @NotNull
  Integer id;
  @NotBlank
  String square;
  String section;

  /**
   * Get the database id for the SquareSectionJson
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
   * Get the square name, e.g. "Rautatientori"
   *
   * @return the square
   */
  public String getSquare() {
    return square;
  }

  public void setSquare(String square) {
    this.square = square;
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
