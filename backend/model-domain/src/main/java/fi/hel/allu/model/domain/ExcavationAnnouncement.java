package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;

import java.time.ZonedDateTime;

/**
 * Excavation announcement (Kaivuilmoitus) specific data.
 */
public class ExcavationAnnouncement extends ApplicationExtension {
  private Applicant contractor;
  private Contact responsiblePerson;
  private Boolean pksCard;
  private Boolean constructionWork;
  private Boolean maintenanceWork;
  private Boolean emergencyWork;
  private Boolean propertyConnectivity;
  private ZonedDateTime winterTimeOperation;
  private ZonedDateTime summerTimeOperation;
  private ZonedDateTime workFinished;
  private ZonedDateTime unauthorizedWorkStartTime;
  private ZonedDateTime unauthorizedWorkEndTime;
  private ZonedDateTime guaranteeEndTime;
  private Integer cableReportId;
  private String additionalInfo;
  private String trafficArrangements;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
  }

  /**
   * In Finnish: Työn suorittaja (yritys yleensä).
   * The company that does the actual work instead of the party that acts as customer.
   */
  public Applicant getContractor() {
    return contractor;
  }

  public void setContractor(Applicant contractor) {
    this.contractor = contractor;
  }

  /**
   * In Finnish: vastuuhenkilö
   * The person responsible of the actual work.
   */
  public Contact getResponsiblePerson() {
    return responsiblePerson;
  }

  public void setResponsiblePerson(Contact responsiblePerson) {
    this.responsiblePerson = responsiblePerson;
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
   * In Finnish: Kesätyön toiminnallinen kunto (päivämäärä, jolloin valmis).
   */
  public ZonedDateTime getSummerTimeOperation() {
    return summerTimeOperation;
  }

  public void setSummerTimeOperation(ZonedDateTime summerTimeOperation) {
    this.summerTimeOperation = summerTimeOperation;
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
   * In Finnish: johtoselvitys kaivuilmoitukselle.
   */
  public Integer getCableReportId() {
    return cableReportId;
  }

  public void setCableReportId(Integer cableReportId) {
    this.cableReportId = cableReportId;
  }

  /**
   * In Finnish: lisätiedot.
   */
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
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
}
