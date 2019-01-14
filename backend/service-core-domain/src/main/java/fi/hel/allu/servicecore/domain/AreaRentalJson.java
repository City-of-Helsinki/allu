package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Area rental (aluevuokraus) specific data.
 */
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

  @ApiModelProperty(hidden = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  @ApiModelProperty(value = "True, if the contractor doing the actual work has PKS-card")
  public Boolean getPksCard() {
    return pksCard;
  }

  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
  }

  /**
   * Vähäistä suurempaa haittaa aiheuttava työ.
   */
  @ApiModelProperty(value = "Work causing major disturbance (vähäistä suurempaa haittaa aiheuttava työ)")
  public Boolean getMajorDisturbance() {
    return majorDisturbance;
  }

  public void setMajorDisturbance(Boolean majorDisturbance) {
    this.majorDisturbance = majorDisturbance;
  }

  @ApiModelProperty(value = "Work purpose")
  public String getWorkPurpose() {
    return workPurpose;
  }

  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @ApiModelProperty(value = "Additional information")
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  @ApiModelProperty(value = "Traffic arrangements")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @ApiModelProperty(value = "Work finished date")
  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
  }

  @ApiModelProperty(value = "Work finished date reported by customer")
  public ZonedDateTime getCustomerWorkFinished() {
    return customerWorkFinished;
  }

  public void setCustomerWorkFinished(ZonedDateTime customerWorkFinished) {
    this.customerWorkFinished = customerWorkFinished;
  }

  @ApiModelProperty(value = "Date when customer reported work finished date")
  public ZonedDateTime getWorkFinishedReported() {
    return workFinishedReported;
  }

  public void setWorkFinishedReported(ZonedDateTime workFinishedReported) {
    this.workFinishedReported = workFinishedReported;
  }

  @ApiModelProperty(value = "Traffic arrangement impediment")
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }
}
