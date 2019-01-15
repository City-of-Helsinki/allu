package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@NotFalse(rules = {"type, isAllowedType, {supervisiontask.create.type}"})
@ApiModel(value = "Supervision task creation model")
public class SupervisionTaskCreateJson {

  @NotNull(message = "{supervisiontask.type}")
  private SupervisionTaskType type;
  @NotNull(message = "{supervisiontask.applicationId}")
  private Integer applicationId;
  @NotNull(message = "{supervisiontask.ownerId}")
  private Integer ownerId;
  @NotNull(message = "{supervisiontask.plannedFinishingTime}")
  private ZonedDateTime plannedFinishingTime;
  private String description;

  @ApiModelProperty(value = "Type of the supervision task", allowableValues = "SUPERVISION, PRELIMINARY_SUPERVISION", required = true)
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Application ID", required = true)
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

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

  @JsonIgnore
  public boolean getIsAllowedType() {
    return type == SupervisionTaskType.PRELIMINARY_SUPERVISION || type == SupervisionTaskType.SUPERVISION;
  }
}
