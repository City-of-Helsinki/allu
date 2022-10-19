package fi.hel.allu.external.domain;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotEmpty;

import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description ="Excavation announcement (kaivuilmoitus) input model")
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

  @Schema(description = "Application kind of the client system. Allu application kind will be selected by handler according to this value", required = true)
  public String getClientApplicationKind() {
    return clientApplicationKind;
  }

  public void setClientApplicationKind(String clientApplicationKind) {
    this.clientApplicationKind = clientApplicationKind;
  }

  @Schema(description = "Contractor (työn suorittaja)", required = true)
  public CustomerWithContactsExt getContractorWithContacts() {
    return contractorWithContacts;
  }

  public void setContractorWithContacts(CustomerWithContactsExt contractorWithContacts) {
    this.contractorWithContacts = contractorWithContacts;
  }

  @Schema(description = "Property developer (rakennuttaja)")
  public CustomerWithContactsExt getPropertyDeveloperWithContacts() {
    return propertyDeveloperWithContacts;
  }

  public void setPropertyDeveloperWithContacts(CustomerWithContactsExt propertyDeveloperWithContacts) {
    this.propertyDeveloperWithContacts = propertyDeveloperWithContacts;
  }

  @Schema(description = "PKS card (PKS kortti)")
  public Boolean getPksCard() {
    return pksCard;
  }

  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
  }

  @Schema(description = "Construction work (rakentaminen)")
  public Boolean getConstructionWork() {
    return constructionWork;
  }

  public void setConstructionWork(Boolean constructionWork) {
    this.constructionWork = constructionWork;
  }

  @Schema(description = "Maintenance work (kunnossapito)")
  public Boolean getMaintenanceWork() {
    return maintenanceWork;
  }

  public void setMaintenanceWork(Boolean maintenanceWork) {
    this.maintenanceWork = maintenanceWork;
  }

  @Schema(description = "Emergency work (hätätyö)")
  public Boolean getEmergencyWork() {
    return emergencyWork;
  }

  public void setEmergencyWork(Boolean emergencyWork) {
    this.emergencyWork = emergencyWork;
  }

  @Schema(description = "Property connectivity (kiinteistöliitos)")
  public Boolean getPropertyConnectivity() {
    return propertyConnectivity;
  }

  public void setPropertyConnectivity(Boolean propertyConnectivity) {
    this.propertyConnectivity = propertyConnectivity;
  }

  @Schema(description = "Self supervision (omavalvonta)")
  public Boolean getSelfSupervision() {
    return selfSupervision;
  }

  public void setSelfSupervision(Boolean selfSupervision) {
    this.selfSupervision = selfSupervision;
  }

  @Schema(description = "Work purpose (työn tarkoitus)", required = true)
  public String getWorkPurpose() {
    return workPurpose;
  }

  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @Schema(description = "Additional information (lisätiedot)")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Schema(description = "Traffic arrangementes (suoritettavat liikennejärjestelyt)")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @Schema(description = "Traffic arrangement impediment (liikennejärjestelyn haitta)")
  public TrafficArrangementImpedimentType getTrafficArrangementImpediment() {
    return trafficArrangementImpediment;
  }

  public void setTrafficArrangementImpediment(TrafficArrangementImpedimentType trafficArrangementImpediment) {
    this.trafficArrangementImpediment = trafficArrangementImpediment;
  }

  @Schema(description = "Application identifiers of related placement contracts")
  public List<String> getPlacementContracts() {
    return placementContracts;
  }

  public void setPlacementContracts(List<String> placementContracts) {
    this.placementContracts = placementContracts;
  }

  @Schema(description = "Application identifiers of related cable reports")
  public List<String> getCableReports() {
    return cableReports;
  }

  public void setCableReports(List<String> cableReports) {
    this.cableReports = cableReports;
  }

}
