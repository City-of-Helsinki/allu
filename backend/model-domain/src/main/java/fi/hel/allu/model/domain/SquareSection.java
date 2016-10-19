package fi.hel.allu.model.domain;

public class SquareSection {
  private Integer id;
  private String square;
  private String section;

  /**
   * Get the database id for the SquareSection
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
