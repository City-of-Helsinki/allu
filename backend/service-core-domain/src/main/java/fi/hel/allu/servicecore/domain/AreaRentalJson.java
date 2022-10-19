package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Area rental (aluevuokraus) specific data.
 */
@Schema(description ="Area rental specific fields")
public class AreaRentalJson extends ApplicationExtensionJson {
  private Boolean pksCard;
  private Boolean majorDisturbance;
  @NotNull(message = "{application.workPurpose}")
  private String workPurpose;
  private String additionalInfo;
  private String trafficArrangements;
  private ZonedDateTime workFinished;
  private ZonedDateTime customerWorkFinished;
  private ZonedDateTime workFinishedReported;
  @NotNull(message = "{application.arearental.trafficArrangementImpedimentType}")
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @Schema(description = "Application type (always AREA_RENTAL).", allowableValues="AREA_RENTAL", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  @Schema(description = "True, if the contractor doing the actual work has PKS-card")
  public Boolean getPksCard() {
    return pksCard;
  }

  @UpdatableProperty
  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
  }

  /**
   * Vähäistä suurempaa haittaa aiheuttava työ.
   */
  @Schema(description = "Work causing major disturbance (vähäistä suurempaa haittaa aiheuttava työ)")
  public Boolean getMajorDisturbance() {
    return majorDisturbance;
  }

  @UpdatableProperty
  public void setMajorDisturbance(Boolean majorDisturbance) {
    this.majorDisturbance = majorDisturbance;
  }

  @Schema(description = "Work purpose", required = true)
  public String getWorkPurpose() {
    return workPurpose;
  }

  @UpdatableProperty
  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @Schema(description = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  @UpdatableProperty
  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @Schema(description = "Traffic arrangements")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  @UpdatableProperty
  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @Schema(description = "Work finished date", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
  }

  @Schema(description = "Work finished date reported by customer", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getCustomerWorkFinished() {
    return customerWorkFinished;
  }

  public void setCustomerWorkFinished(ZonedDateTime customerWorkFinished) {
    this.customerWorkFinished = customerWorkFinished;
  }

  @Schema(description = "Date when customer reported work finished date", accessMode = Schema.AccessMode.READ_ONLY)
  public ZonedDateTime getWorkFinishedReported() {
    return workFinishedReported;
  }

  public void setWorkFinishedReported(ZonedDateTime workFinishedReported) {
    this.workFinishedReported = workFinishedReported;
  }

  @Schema(description = "Traffic arrangement impediment", required = true)
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  @UpdatableProperty
  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }
}
