package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationType;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class FixedLocationJson {
  @NotNull
  Integer id;
  @NotBlank
  String area;
  String section;
  @NotNull
  ApplicationType applicationType;

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

  /**
   * Get the application type this location is used for.
   *
   * @return  the application type this location is used for.
   */
  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }
}
