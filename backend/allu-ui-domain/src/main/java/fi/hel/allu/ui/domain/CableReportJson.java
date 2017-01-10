package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationType;

import java.util.List;

public class CableReportJson extends ApplicationExtensionJson {

  private Boolean cableSurveyRequired;
  private String cableReportId;
  private String workDescription;
  private ApplicantJson owner;
  private ContactJson contact;
  private Integer mapExtractCount;
  private List<CableInfoEntryJson> infoEntries;
  private Boolean pksCard;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.CABLE_REPORT;
  }

  /**
   * Whether cable survey is needed for cable report (in Finnish: "Johtokartoitettava")
   */
  public Boolean getCableSurveyRequired() {
    return cableSurveyRequired;
  }

  public void setCableSurveyRequired(Boolean cableSurveyRequired) {
    this.cableSurveyRequired = cableSurveyRequired;
  }

  /**
   * Get the cable report ID (in Finnish: "Johtoselvitystunnus")
   *
   * @return cable report ID
   */
  public String getCableReportId() {
    return cableReportId;
  }

  public void setCableReportId(String cableReportId) {
    this.cableReportId = cableReportId;
  }

  /**
   * Get work description (in Finnish: "Työn kuvaus")
   *
   * @return work description
   */
  public String getWorkDescription() {
    return workDescription;
  }

  public void setWorkDescription(String workDescription) {
    this.workDescription = workDescription;
  }

  /**
   * Get the cable report owner (in Finnish: "Omistaja")
   *
   * @return the owner
   */
  public ApplicantJson getOwner() {
    return owner;
  }

  public void setOwner(ApplicantJson owner) {
    this.owner = owner;
  }

  /**
   * Get the cable report contact (in Finnish: "Yhteyshenkilö")
   *
   * @return the contact
   */
  public ContactJson getContact() {
    return contact;
  }

  public void setContact(ContactJson contact) {
    this.contact = contact;
  }

  /**
   * Get the map extract count (In Finnish: "Karttaotteiden määrä")
   *
   * @return the count
   */
  public Integer getMapExtractCount() {
    return mapExtractCount;
  }

  public void setMapExtractCount(Integer mapExtractCount) {
    this.mapExtractCount = mapExtractCount;
  }

  /**
   * Get the cable info entries
   *
   * @return the entries
   */
  public List<CableInfoEntryJson> getInfoEntries() {
    return infoEntries;
  }

  public void setInfoEntries(List<CableInfoEntryJson> infoEntries) {
    this.infoEntries = infoEntries;
  }

  /**
   * In Finnish: PKS-kortti (pääkaupunkiseudun katutöihin liittyvät koulutus).
   * True, if the contractor doing the actual work has PKS-card.
   */
  public Boolean getPksCard() {
    return pksCard;
  }

  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
  }

  /**
   * In Finnish: Rakentaminen.
   */
  public Boolean getConstructionWork() {
    return constructionWork;
  }

  public void setConstructionWork(Boolean constructionWork) {
    this.constructionWork = constructionWork;
  }

  /**
   * In Finnish: Kunnossapito.
   */
  public Boolean getMaintenanceWork() {
    return maintenanceWork;
  }

  public void setMaintenanceWork(Boolean maintenanceWork) {
    this.maintenanceWork = maintenanceWork;
  }

  /**
   * In Finnish: Hätätyö.
   */
  public Boolean getEmergencyWork() {
    return emergencyWork;
  }

  public void setEmergencyWork(Boolean emergencyWork) {
    this.emergencyWork = emergencyWork;
  }

  /**
   * In Finnish: Kiinteistöliitos.
   */
  public Boolean getPropertyConnectivity() {
    return propertyConnectivity;
  }

  public void setPropertyConnectivity(Boolean propertyConnectivity) {
    this.propertyConnectivity = propertyConnectivity;
  }

}
