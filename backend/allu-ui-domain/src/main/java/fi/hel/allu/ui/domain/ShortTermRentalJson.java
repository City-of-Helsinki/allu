package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationCategory;
import fi.hel.allu.common.types.ApplicationType;

import javax.validation.constraints.NotNull;

/**
 * JSON DAO covering all short term rental data requirements.
 */
public class ShortTermRentalJson extends EventJson {
  @NotNull(message = "{shorttermrental.type}")
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
}
