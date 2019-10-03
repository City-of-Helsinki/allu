package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Model to modify supervision task data")
public class SupervisionTaskModifyJson {

  @NotNull(message = "{supervisiontask.ownerId}")
  private Integer ownerId;
  @NotNull(message = "{supervisiontask.plannedFinishingTime}")
  private ZonedDateTime plannedFinishingTime;
  private String description;

  @ApiModelProperty(value = "ID of the user (supervisor) who owns the task", required = true)
  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }

  @ApiModelProperty(value = "Planned finishing time for the task", required = true)
  public ZonedDateTime getPlannedFinishingTime() {
    return plannedFinishingTime;
  }

  public void setPlannedFinishingTime(ZonedDateTime plannedFinishingTime) {
    this.plannedFinishingTime = plannedFinishingTime;
  }

  @ApiModelProperty(value = "Task description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
