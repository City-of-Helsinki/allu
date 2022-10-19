package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Placement contract specific data")
public class PlacementContractJson extends ApplicationExtensionJson {
  private String propertyIdentificationNumber;
  private String additionalInfo;
  private String contractText;
  private Integer sectionNumber;
  private String rationale;

  @Schema(description = "Application type (always PLACEMENT_CONTRACT).", allowableValues="PLACEMENT_CONTRACT", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.PLACEMENT_CONTRACT;
  }

  @Schema(description = "Property identification number (kiinteistötunnus)")
  public String getPropertyIdentificationNumber() {
    return propertyIdentificationNumber;
  }

  @UpdatableProperty
  public void setPropertyIdentificationNumber(String propertyIdentificationNumber) {
    this.propertyIdentificationNumber = propertyIdentificationNumber;
  }

  @Schema(description = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  @UpdatableProperty
  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Schema(description = "Contract text")
  public String getContractText() {
    return contractText;
  }

  @UpdatableProperty
  public void setContractText(String contractText) {
    this.contractText = contractText;
  }

  @Schema(description = "Section number (pykälänumero)", accessMode = Schema.AccessMode.READ_ONLY)
  public Integer getSectionNumber() {
    return sectionNumber;
  }

  @UpdatableProperty
  public void setSectionNumber(Integer sectionNumber) {
    this.sectionNumber = sectionNumber;
  }

  @Schema(description = "Rationale")
  public String getRationale() {
    return rationale;
  }

  @UpdatableProperty
  public void setRationale(String rationale) {
    this.rationale = rationale;
  }
}
