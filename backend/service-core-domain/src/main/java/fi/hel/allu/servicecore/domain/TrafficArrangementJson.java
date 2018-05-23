package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;

import javax.validation.constraints.NotNull;

/**
 * Traffic arrangement (väliaikainen liikennejärjestely) specific data.
 */
public class TrafficArrangementJson extends ApplicationExtensionJson {
  private String workPurpose;
  private String trafficArrangements;
  @NotNull(message = "{application.trafficarrangements.trafficArrangementImpedimentType}")
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS;
  }

  /**
   * In Finnish: työn tarkoitus.
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
}
