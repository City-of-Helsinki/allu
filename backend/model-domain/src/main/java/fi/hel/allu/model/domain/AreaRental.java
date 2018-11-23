package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;

import java.time.ZonedDateTime;

/**
 * Area rental (Aluevuokraus) specific data.
 */
public class AreaRental extends ApplicationExtension implements WorkFinishedDates {
  private Boolean pksCard;
  private Boolean majorDisturbance;
  private String workPurpose;
  private String additionalInfo;
  private String trafficArrangements;
  private ZonedDateTime workFinished;
  private ZonedDateTime customerWorkFinished;
  private ZonedDateTime workFinishedReported;
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  /**
   * In Finnish: PKS-kortti (pääkaupunkiseudun katutöihin liittyvät koulutus).
   * True, if the contractor doing the actual work has PKS-card.
   */
  public Boolean getPksCard() {
    return pksCard;
  }

  /**
   * Vähäistä suurempaa haittaa aiheuttava työ.
   */
  public Boolean getMajorDisturbance() {
    return majorDisturbance;
  }

  public void setMajorDisturbance(Boolean majorDisturbance) {
    this.majorDisturbance = majorDisturbance;
  }

  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
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

  /**
   * In Finnish: Työ valmis.
   */
  @Override
  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  @Override
  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
  }

  /**
   * In Finnish: Asiakkaan ilmoittama aika, jolloin työ on valmis.
   */
  @Override
  public ZonedDateTime getCustomerWorkFinished() {
    return customerWorkFinished;
  }

  @Override
  public void setCustomerWorkFinished(ZonedDateTime customerWorkFinished) {
    this.customerWorkFinished = customerWorkFinished;
  }

  /**
   * Date when customer reported work finished date
   */
  @Override
  public ZonedDateTime getWorkFinishedReported() {
    return workFinishedReported;
  }

  @Override
  public void setWorkFinishedReported(ZonedDateTime workFinishedReported) {
    this.workFinishedReported = workFinishedReported;
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
}
