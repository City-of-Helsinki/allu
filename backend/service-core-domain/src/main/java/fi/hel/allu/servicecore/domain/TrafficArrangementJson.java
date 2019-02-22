package fi.hel.allu.servicecore.domain;

import javax.validation.constraints.NotNull;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Traffic arrangement specific fields")
public class TrafficArrangementJson extends ApplicationExtensionJson {
  private String workPurpose;
  private String trafficArrangements;
  @NotNull(message = "{application.trafficarrangements.trafficArrangementImpedimentType}")
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @ApiModelProperty(value = "Application type (always TEMPORARY_TRAFFIC_ARRANGEMENTS).", allowableValues="TEMPORARY_TRAFFIC_ARRANGEMENTS", required = true)
  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS;
  }

  @ApiModelProperty(value = "Purpose of the work")
  public String getWorkPurpose() {
    return workPurpose;
  }

  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @ApiModelProperty(value = "Traffic arrangements")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @ApiModelProperty(value = "Traffic arrangement impediment type", required = true)
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }
}
