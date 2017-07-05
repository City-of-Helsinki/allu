package fi.hel.allu.ui.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

/**
 * JSON DAO covering all short term rental data requirements.
 */
public class ShortTermRentalJson extends ApplicationExtensionJson {

  private String description;
  private Boolean commercial;
  private Boolean largeSalesArea;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.SHORT_TERM_RENTAL;
  }

  /**
   * Returns the description of the short term rental.
   *
   * @return  the description of the short term rental.
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * True, if this short term rental has commercial nature.
   *
   * @return  True, if this short term rental has commercial nature.
   */
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
