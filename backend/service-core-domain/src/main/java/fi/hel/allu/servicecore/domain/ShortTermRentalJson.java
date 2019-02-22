package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Short term rental specific fields")
public class ShortTermRentalJson extends ApplicationExtensionJson {

  private String description;
  private Boolean commercial;
  private Boolean billableSalesArea;

  @ApiModelProperty(value = "Application type (always SHORT_TERM_RENTAL).", allowableValues="SHORT_TERM_REANTAL", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.SHORT_TERM_RENTAL;
  }

  @ApiModelProperty(value = "Description of the rental")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(value = "True, if rental has commercial nature.")
  public Boolean getCommercial() {
    return commercial;
  }

  public void setCommercial(Boolean commercial) {
    this.commercial = commercial;
  }

  @ApiModelProperty(value = "True if the sales area is billable (over 80cm from the wall)")
  public Boolean getBillableSalesArea() {
    return billableSalesArea;
  }

  public void setBillableSalesArea(Boolean billableSalesArea) {
    this.billableSalesArea = billableSalesArea;
  }
}
