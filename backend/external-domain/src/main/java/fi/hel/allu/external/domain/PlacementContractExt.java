package fi.hel.allu.external.domain;

import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Placement contract (sijoitussopimus)")
public class PlacementContractExt extends ApplicationExt {

  private PostalAddressExt postalAddress;

  @NotEmpty(message = "{application.clientApplicationKind}")
  private String clientApplicationKind;
  private String workDescription;
  private String propertyIdentificationNumber;

  @ApiModelProperty(value = "Postal address")
  public PostalAddressExt getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressExt postalAddress) {
    this.postalAddress = postalAddress;
  }

  @ApiModelProperty(value = "Application kind of the client system. Allu application kind will be selected by handler according to this value", required = true)
  public String getClientApplicationKind() {
    return clientApplicationKind;
  }

  public void setClientApplicationKind(String clientApplicationKind) {
    this.clientApplicationKind = clientApplicationKind;
  }

  @ApiModelProperty(value = "Work description")
  public String getWorkDescription() {
    return workDescription;
  }

  public void setWorkDescription(String workDescription) {
    this.workDescription = workDescription;
  }

  @ApiModelProperty(value = "Property identification number (in Finnish: kiinteistötunnus)")
  public String getPropertyIdentificationNumber() {
    return propertyIdentificationNumber;
  }

  public void setPropertyIdentificationNumber(String propertyIdentificationNumber) {
    this.propertyIdentificationNumber = propertyIdentificationNumber;
  }
}
