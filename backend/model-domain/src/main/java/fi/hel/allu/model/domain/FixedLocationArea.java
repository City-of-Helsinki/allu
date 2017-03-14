package fi.hel.allu.model.domain;

import java.util.List;

public class FixedLocationArea {
  private Integer id;
  private String name;
  private List<FixedLocationSection> sections;

  public FixedLocationArea() {
  } // for jackson

  public FixedLocationArea(Integer id, String name, List<FixedLocationSection> sections) {
    this.id = id;
    this.name = name;
    this.sections = sections;
  }

  /**
   * Get the database id for the FixedLocationArea
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
   * @return list of sections
   */
  public List<FixedLocationSection> getSections() {
    return sections;
  }

  public void setSections(List<FixedLocationSection> sections) {
    this.sections = sections;
  }
}
