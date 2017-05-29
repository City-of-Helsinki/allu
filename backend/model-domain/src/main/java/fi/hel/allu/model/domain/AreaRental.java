package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.TrafficArrangementImpedimentType;

import java.time.ZonedDateTime;

/**
 * Area rental (Aluevuokraus) specific data.
 */
public class AreaRental extends ApplicationExtension {
  private String additionalInfo;
  private String trafficArrangements;
  private ZonedDateTime workFinished;
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
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
  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
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
