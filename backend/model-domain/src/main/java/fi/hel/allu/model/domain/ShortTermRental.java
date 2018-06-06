package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

/**
 * Model for short term rentals.
 */
public class ShortTermRental extends ApplicationExtension {
  private String description;
  private Boolean commercial;
  private Boolean billableSalesArea;

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
   * Is this application for a billable sales area (previously over 0.8 * 3 sqm, now over 0.8 from a wall)?
   */
  public Boolean getBillableSalesArea() {
    return billableSalesArea;
  }

  public void setBillableSalesArea(Boolean billableSalesArea) {
    this.billableSalesArea = billableSalesArea;
  }
}
