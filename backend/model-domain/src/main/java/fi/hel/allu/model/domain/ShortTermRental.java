package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationCategory;
import fi.hel.allu.common.types.ApplicationType;

import javax.validation.constraints.NotNull;

/**
 * Model for short term rentals.
 */
public class ShortTermRental extends Event {
  @NotNull
  private ApplicationType type;
  private String description;
  private Boolean commercial;

  @Override
  public ApplicationCategory getApplicationCategory() {
    return ApplicationCategory.SHORT_TERM_RENTAL;
  }

  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
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
}
