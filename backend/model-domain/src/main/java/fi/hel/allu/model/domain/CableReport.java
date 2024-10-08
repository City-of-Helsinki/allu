package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

import java.time.ZonedDateTime;
import java.util.List;

public class CableReport extends ApplicationExtension {

  private String cableReportId;
  private String workDescription;
  private Integer mapExtractCount;
  private List<CableInfoEntry> infoEntries;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;
  private ZonedDateTime validityTime;
  private Integer orderer;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.CABLE_REPORT;
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

  /**
   * In Finnish: Johtoselvityksen voimassaoloaika
   *
   * @return  The validity time of the cable report.
   */
  public ZonedDateTime getValidityTime() {
    return validityTime;
  }

  public void setValidityTime(ZonedDateTime validityTime) {
    this.validityTime = validityTime;
  }

  /**
   * In Finnish: Johtoselvityksen tilaaja
   * Contact who ordered the cable report
   *
   * @return id of the contact who ordered the cable report
   */
  public Integer getOrderer() {
    return orderer;
  }

  public void setOrderer(Integer orderer) {
    this.orderer = orderer;
  }
}
