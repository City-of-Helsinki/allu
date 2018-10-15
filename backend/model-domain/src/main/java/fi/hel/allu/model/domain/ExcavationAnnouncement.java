package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;

import java.time.ZonedDateTime;

/**
 * Excavation announcement (Kaivuilmoitus) specific data.
 */
public class ExcavationAnnouncement extends ApplicationExtension {
  private Boolean pksCard;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;
  private Boolean compactionAndBearingCapacityMeasurement;
  private Boolean qualityAssuranceTest;
  private ZonedDateTime winterTimeOperation;
  private ZonedDateTime workFinished;
  private ZonedDateTime unauthorizedWorkStartTime;
  private ZonedDateTime unauthorizedWorkEndTime;
  private ZonedDateTime guaranteeEndTime;
  private ZonedDateTime customerStartTime;
  private ZonedDateTime customerEndTime;
  private ZonedDateTime customerWinterTimeOperation;
  private ZonedDateTime customerWorkFinished;
  private ZonedDateTime operationalConditionReported;
  private ZonedDateTime workFinishedReported;
  private ZonedDateTime validityReported;
  private Integer cableReportId;
  private String workPurpose;
  private String trafficArrangements;
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;


  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
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

  public Boolean getCompactionAndBearingCapacityMeasurement() {
    return compactionAndBearingCapacityMeasurement;
  }

  public void setCompactionAndBearingCapacityMeasurement(Boolean compactionAndBearingCapacityMeasurement) {
    this.compactionAndBearingCapacityMeasurement = compactionAndBearingCapacityMeasurement;
  }

  public Boolean getQualityAssuranceTest() {
    return qualityAssuranceTest;
  }

  public void setQualityAssuranceTest(Boolean qualityAssuranceTest) {
    this.qualityAssuranceTest = qualityAssuranceTest;
  }

  /**
   * In Finnish: Talvityön toiminnallinen kunto (päivämäärä, jolloin valmis).
   */
  public ZonedDateTime getWinterTimeOperation() {
    return winterTimeOperation;
  }

  public void setWinterTimeOperation(ZonedDateTime winterTimeOperation) {
    this.winterTimeOperation = winterTimeOperation;
  }

  /**
   * In Finnish: Työ valmis.
   */
  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
  }

  /**
   * In Finnish: Luvattoman kaivutyön aloitusaika.
   */
  public ZonedDateTime getUnauthorizedWorkStartTime() {
    return unauthorizedWorkStartTime;
  }

  public void setUnauthorizedWorkStartTime(ZonedDateTime unauthorizedWorkStartTime) {
    this.unauthorizedWorkStartTime = unauthorizedWorkStartTime;
  }

  /**
   * In Finnish: Luvattoman kaivutyön lopetusaika.
   */
  public ZonedDateTime getUnauthorizedWorkEndTime() {
    return unauthorizedWorkEndTime;
  }

  public void setUnauthorizedWorkEndTime(ZonedDateTime unauthorizedWorkEndTime) {
    this.unauthorizedWorkEndTime = unauthorizedWorkEndTime;
  }

  /**
   * In Finnish: Takuun päättymispäivämäärä.
   */
  public ZonedDateTime getGuaranteeEndTime() {
    return guaranteeEndTime;
  }

  public void setGuaranteeEndTime(ZonedDateTime guaranteeEndTime) {
    this.guaranteeEndTime = guaranteeEndTime;
  }

  /**
   * In Finnish: Asiakkaan ilmoittama hakemuksen alkuaika.
   */
  public ZonedDateTime getCustomerStartTime() {
    return customerStartTime;
  }

  public void setCustomerStartTime(ZonedDateTime customerStartTime) {
    this.customerStartTime = customerStartTime;
  }

  /**
   * In Finnish: Asiakkaan ilmoittama hakemuksen loppuaika.
   */
  public ZonedDateTime getCustomerEndTime() {
    return customerEndTime;
  }

  public void setCustomerEndTime(ZonedDateTime customerEndTime) {
    this.customerEndTime = customerEndTime;
  }

  /**
   * In Finnish: Asiakkaan ilmoittama talvityön toiminnallinen kunto.
   */
  public ZonedDateTime getCustomerWinterTimeOperation() {
    return customerWinterTimeOperation;
  }

  public void setCustomerWinterTimeOperation(ZonedDateTime customerWinterTimeOperation) {
    this.customerWinterTimeOperation = customerWinterTimeOperation;
  }

  /**
   * In Finnish: Asiakkaan ilmoittama aika, jolloin työ on valmis.
   */
  public ZonedDateTime getCustomerWorkFinished() {
    return customerWorkFinished;
  }

  public void setCustomerWorkFinished(ZonedDateTime customerWorkFinished) {
    this.customerWorkFinished = customerWorkFinished;
  }

  /**
   * In Finnish: johtoselvitys kaivuilmoitukselle.
   */
  public Integer getCableReportId() {
    return cableReportId;
  }

  public void setCableReportId(Integer cableReportId) {
    this.cableReportId = cableReportId;
  }

  /**
   * In Finnish: Työn tarkoitus.
   */
  public String getWorkPurpose() {
    return workPurpose;
  }

  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  /**
   * In Finnish: suoritettavat liikennejärjestelytyöt.
   */
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  /**
   * In Finnish: Liikennejärjestelyn haitta.
   */
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }

  /**
   * Date when customer reported operational condition date
   */
  public ZonedDateTime getOperationalConditionReported() {
    return operationalConditionReported;
  }

  public void setOperationalConditionReported(ZonedDateTime operationalConditionReported) {
    this.operationalConditionReported = operationalConditionReported;
  }

  /**
   * Date when customer reported work finished date
   */
  public ZonedDateTime getWorkFinishedReported() {
    return workFinishedReported;
  }

  public void setWorkFinishedReported(ZonedDateTime workFinishedReported) {
    this.workFinishedReported = workFinishedReported;
  }

  /**
   * Date when customer reported validity dates
   */
  public ZonedDateTime getValidityReported() {
    return validityReported;
  }

  public void setValidityReported(ZonedDateTime validityReported) {
    this.validityReported = validityReported;
  }
}
