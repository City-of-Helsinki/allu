package fi.hel.allu.external.domain;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;

import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Excavation announcement (kaivuilmoitus) input model")
public class ExcavationAnnouncementExt extends BaseApplicationExt {

  @NotEmpty(message = "{application.clientApplicationKind}")
  private String clientApplicationKind;

  @NotNull(message = "{excavation.contractor}")
  @Valid
  private CustomerWithContactsExt contractorWithContacts;
  @Valid
  private CustomerWithContactsExt propertyDeveloperWithContacts;
  private Boolean pksCard;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;
  private Boolean selfSupervision;
  @NotNull(message = "{application.workPurpose}")
  private String workPurpose;
  private String additionalInfo;
  private String trafficArrangements;
  private TrafficArrangementImpedimentType trafficArrangementImpediment;
  private List<String> placementContracts;
  private List<String> cableReports;

  @ApiModelProperty(value = "Application kind of the client system. Allu application kind will be selected by handler according to this value", required = true)
  public String getClientApplicationKind() {
    return clientApplicationKind;
  }

  public void setClientApplicationKind(String clientApplicationKind) {
    this.clientApplicationKind = clientApplicationKind;
  }

  @ApiModelProperty(value = "Contractor (työn suorittaja)", required = true)
  public CustomerWithContactsExt getContractorWithContacts() {
    return contractorWithContacts;
  }

  public void setContractorWithContacts(CustomerWithContactsExt contractorWithContacts) {
    this.contractorWithContacts = contractorWithContacts;
  }

  @ApiModelProperty(value = "Property developer (rakennuttaja)")
  public CustomerWithContactsExt getPropertyDeveloperWithContacts() {
    return propertyDeveloperWithContacts;
  }

  public void setPropertyDeveloperWithContacts(CustomerWithContactsExt propertyDeveloperWithContacts) {
    this.propertyDeveloperWithContacts = propertyDeveloperWithContacts;
  }

  @ApiModelProperty(value = "PKS card (PKS kortti)")
  public Boolean getPksCard() {
    return pksCard;
  }

  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
  }

  @ApiModelProperty(value = "Construction work (rakentaminen)")
  public Boolean getConstructionWork() {
    return constructionWork;
  }

  public void setConstructionWork(Boolean constructionWork) {
    this.constructionWork = constructionWork;
  }

  @ApiModelProperty(value = "Maintenance work (kunnossapito)")
  public Boolean getMaintenanceWork() {
    return maintenanceWork;
  }

  public void setMaintenanceWork(Boolean maintenanceWork) {
    this.maintenanceWork = maintenanceWork;
  }

  @ApiModelProperty(value = "Emergency work (hätätyö)")
  public Boolean getEmergencyWork() {
    return emergencyWork;
  }

  public void setEmergencyWork(Boolean emergencyWork) {
    this.emergencyWork = emergencyWork;
  }

  @ApiModelProperty(value = "Property connectivity (kiinteistöliitos)")
  public Boolean getPropertyConnectivity() {
    return propertyConnectivity;
  }

  public void setPropertyConnectivity(Boolean propertyConnectivity) {
    this.propertyConnectivity = propertyConnectivity;
  }

  @ApiModelProperty(value = "Self supervision (omavalvonta)")
  public Boolean getSelfSupervision() {
    return selfSupervision;
  }

  public void setSelfSupervision(Boolean selfSupervision) {
    this.selfSupervision = selfSupervision;
  }

  @ApiModelProperty(value = "Work purpose (työn tarkoitus)", required = true)
  public String getWorkPurpose() {
    return workPurpose;
  }

  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @ApiModelProperty(value = "Additional information (lisätiedot)")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @ApiModelProperty(value = "Traffic arrangementes (suoritettavat liikennejärjestelyt)")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @ApiModelProperty(value = "Traffic arrangement impediment (liikennejärjestelyn haitta)")
  public TrafficArrangementImpedimentType getTrafficArrangementImpediment() {
    return trafficArrangementImpediment;
  }

  public void setTrafficArrangementImpediment(TrafficArrangementImpedimentType trafficArrangementImpediment) {
    this.trafficArrangementImpediment = trafficArrangementImpediment;
  }

  @ApiModelProperty(value = "Application identifiers of related placement contracts")
  public List<String> getPlacementContracts() {
    return placementContracts;
  }

  public void setPlacementContracts(List<String> placementContracts) {
    this.placementContracts = placementContracts;
  }

  @ApiModelProperty(value = "Application identifiers of related cable reports")
  public List<String> getCableReports() {
    return cableReports;
  }

  public void setCableReports(List<String> cableReports) {
    this.cableReports = cableReports;
  }

}
