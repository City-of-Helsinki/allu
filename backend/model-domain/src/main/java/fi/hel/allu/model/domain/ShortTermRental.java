package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

/**
 * Model for short term rentals.
 */
public class ShortTermRental extends ApplicationExtension {
  private String description;
  private Boolean commercial;
  private Boolean largeSalesArea;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.SHORT_TERM_RENTAL;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Boolean getCommercial() {
    return commercial;
  }

  public void setCommercial(Boolean commercial) {
    this.commercial = commercial;
  }

  /**
   * Is this application for a large sales area (over 0.8 * 3 sqm)?
   *
   * @return true if the application is for a large sales area
   */
  public Boolean getLargeSalesArea() {
    return largeSalesArea;
  }

  public void setLargeSalesArea(Boolean largeSalesArea) {
    this.largeSalesArea = largeSalesArea;
  }
}
