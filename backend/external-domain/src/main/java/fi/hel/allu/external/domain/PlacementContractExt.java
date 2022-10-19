package fi.hel.allu.external.domain;

import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Placement contract (sijoitussopimus) input model")
public class PlacementContractExt extends BaseApplicationExt {

  @NotEmpty(message = "{application.clientApplicationKind}")
  private String clientApplicationKind;
  private String workDescription;
  private String propertyIdentificationNumber;

  @Schema(description = "Application kind of the client system. Allu application kind will be selected by handler according to this value", required = true)
  public String getClientApplicationKind() {
    return clientApplicationKind;
  }

  public void setClientApplicationKind(String clientApplicationKind) {
    this.clientApplicationKind = clientApplicationKind;
  }

  @Schema(description = "Work description")
  public String getWorkDescription() {
    return workDescription;
  }

  public void setWorkDescription(String workDescription) {
    this.workDescription = workDescription;
  }

  @Schema(description = "Property identification number (in Finnish: kiinteistötunnus)")
  public String getPropertyIdentificationNumber() {
    return propertyIdentificationNumber;
  }

  public void setPropertyIdentificationNumber(String propertyIdentificationNumber) {
    this.propertyIdentificationNumber = propertyIdentificationNumber;
  }
}
