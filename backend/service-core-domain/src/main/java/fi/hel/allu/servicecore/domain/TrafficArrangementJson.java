package fi.hel.allu.servicecore.domain;

import javax.validation.constraints.NotNull;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Traffic arrangement specific fields")
public class TrafficArrangementJson extends ApplicationExtensionJson {
  private String workPurpose;
  private String trafficArrangements;
  @NotNull(message = "{application.trafficarrangements.trafficArrangementImpedimentType}")
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @Schema(description = "Application type (always TEMPORARY_TRAFFIC_ARRANGEMENTS).", allowableValues="TEMPORARY_TRAFFIC_ARRANGEMENTS", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS;
  }

  @Schema(description = "Purpose of the work")
  public String getWorkPurpose() {
    return workPurpose;
  }

  @UpdatableProperty
  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @Schema(description = "Traffic arrangements")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  @UpdatableProperty
  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @Schema(description = "Traffic arrangement impediment type", required = true)
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  @UpdatableProperty
  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }
}
