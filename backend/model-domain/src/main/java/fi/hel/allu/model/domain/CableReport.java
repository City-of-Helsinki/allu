package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;

import java.util.List;

public class CableReport extends ApplicationExtension {

  private boolean cableSurveyRequired;
  private String cableReportId;
  private String workDescription;
  private Applicant owner;
  private Contact contact;
  private Integer mapExtractCount;
  private List<CableInfoEntry> infoEntries;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.CABLE_REPORT;
  }

  /**
   * Whether cable survey is needed for cable report (in Finnish: "Johtokartoitettava")
   */
  public boolean isCableSurveyRequired() {
    return cableSurveyRequired;
  }

  public void setCableSurveyRequired(boolean cableSurveyRequired) {
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
  public Applicant getOwner() {
    return owner;
  }

  public void setOwner(Applicant owner) {
    this.owner = owner;
  }

  /**
   * Get the cable report contact (in Finnish: "Yhteyshenkilö")
   *
   * @return the contact
   */
  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
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
  public List<CableInfoEntry> getInfoEntries() {
    return infoEntries;
  }

  public void setInfoEntries(List<CableInfoEntry> infoEntries) {
    this.infoEntries = infoEntries;
  }

}
