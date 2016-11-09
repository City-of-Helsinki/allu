package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;

public class FixedLocation {
  private Integer id;
  private String area;
  private String section;
  private ApplicationType applicationType;

  /**
   * Get the database id for the FixedLocation
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

  /**
   * Get the application type this fixed location is valid for.
   *
   * @return  the application type this fixed location is valid for.
   */
  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }
}
