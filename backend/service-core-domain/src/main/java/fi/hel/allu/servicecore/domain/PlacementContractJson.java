package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.ApplicationType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "Placement contract specific data")
public class PlacementContractJson extends ApplicationExtensionJson {
  private String propertyIdentificationNumber;
  private String additionalInfo;
  private String contractText;
  private ZonedDateTime terminationDate;
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

  public void setPropertyIdentificationNumber(String propertyIdentificationNumber) {
    this.propertyIdentificationNumber = propertyIdentificationNumber;
  }

  @ApiModelProperty(value = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @ApiModelProperty(value = "Contract text")
  public String getContractText() {
    return contractText;
  }

  public void setContractText(String contractText) {
    this.contractText = contractText;
  }

  @ApiModelProperty(value = "Contract termination date")
  public ZonedDateTime getTerminationDate() {
    return terminationDate;
  }

  public void setTerminationDate(ZonedDateTime terminationDate) {
    this.terminationDate = terminationDate;
  }

  @ApiModelProperty(value = "Section number (pykälänumero)", readOnly = true)
  public Integer getSectionNumber() {
    return sectionNumber;
  }

  public void setSectionNumber(Integer sectionNumber) {
    this.sectionNumber = sectionNumber;
  }

  @ApiModelProperty(value = "Rationale")
  public String getRationale() {
    return rationale;
  }

  public void setRationale(String rationale) {
    this.rationale = rationale;
  }
}
