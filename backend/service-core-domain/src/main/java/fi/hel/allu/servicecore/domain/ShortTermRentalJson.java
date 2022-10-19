package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Short term rental specific fields")
public class ShortTermRentalJson extends ApplicationExtensionJson {

  private String description;
  private Boolean commercial;
  private Boolean billableSalesArea;

  @Schema(description = "Application type (always SHORT_TERM_RENTAL).", allowableValues="SHORT_TERM_REANTAL", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.SHORT_TERM_RENTAL;
  }

  @Schema(description = "Description of the rental")
  public String getDescription() {
    return description;
  }

  @UpdatableProperty
  public void setDescription(String description) {
    this.description = description;
  }

  @Schema(description = "True, if rental has commercial nature.")
  public Boolean getCommercial() {
    return commercial;
  }

  @UpdatableProperty
  public void setCommercial(Boolean commercial) {
    this.commercial = commercial;
  }

  @Schema(description = "True if the sales area is billable (over 80cm from the wall)")
  public Boolean getBillableSalesArea() {
    return billableSalesArea;
  }

  @UpdatableProperty
  public void setBillableSalesArea(Boolean billableSalesArea) {
    this.billableSalesArea = billableSalesArea;
  }
}
