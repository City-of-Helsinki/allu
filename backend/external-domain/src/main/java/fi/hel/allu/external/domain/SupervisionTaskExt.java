package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Supervision task (valvontatehtävä)")
public class SupervisionTaskExt {

  private ZonedDateTime taskDate;
  private SupervisionTaskType type;
  private SupervisionTaskStatusType status;

  public SupervisionTaskExt() {
  }

  public SupervisionTaskExt(ZonedDateTime taskDate, SupervisionTaskType type, SupervisionTaskStatusType status) {
    this.taskDate = taskDate;
    this.type = type;
    this.status = status;
  }

  @ApiModelProperty(value = "Supervision task date ")
  public ZonedDateTime getTaskDate() {
    return taskDate;
  }

  public void setTaskDate(ZonedDateTime taskDate) {
    this.taskDate = taskDate;
  }

  @ApiModelProperty(value = "Supervision task type")
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Supervision task status")
  public SupervisionTaskStatusType getStatus() {
    return status;
  }

  public void setStatus(SupervisionTaskStatusType status) {
    this.status = status;
  }

}
