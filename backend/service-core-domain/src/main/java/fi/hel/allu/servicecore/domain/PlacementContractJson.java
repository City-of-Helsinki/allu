package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "Placement contract specific data")
public class PlacementContractJson extends ApplicationExtensionJson {
  private String propertyIdentificationNumber;
  private String additionalInfo;
  private String contractText;
  private Integer sectionNumber;
  private String rationale;

  @ApiModelProperty(value = "Application type (always PLACEMENT_CONTRACT).", allowableValues="PLACEMENT_CONTRACT", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.PLACEMENT_CONTRACT;
  }

  @ApiModelProperty(value = "Property identification number (kiinteistötunnus)")
  public String getPropertyIdentificationNumber() {
    return propertyIdentificationNumber;
  }

  @UpdatableProperty
  public void setPropertyIdentificationNumber(String propertyIdentificationNumber) {
    this.propertyIdentificationNumber = propertyIdentificationNumber;
  }

  @ApiModelProperty(value = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  @UpdatableProperty
  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @ApiModelProperty(value = "Contract text")
  public String getContractText() {
    return contractText;
  }

  @UpdatableProperty
  public void setContractText(String contractText) {
    this.contractText = contractText;
  }

  @ApiModelProperty(value = "Section number (pykälänumero)", readOnly = true)
  public Integer getSectionNumber() {
    return sectionNumber;
  }

  @UpdatableProperty
  public void setSectionNumber(Integer sectionNumber) {
    this.sectionNumber = sectionNumber;
  }

  @ApiModelProperty(value = "Rationale")
  public String getRationale() {
    return rationale;
  }

  @UpdatableProperty
  public void setRationale(String rationale) {
    this.rationale = rationale;
  }
}
