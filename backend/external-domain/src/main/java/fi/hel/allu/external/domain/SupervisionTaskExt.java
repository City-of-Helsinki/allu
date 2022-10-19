package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supervision task (valvontatehtävä)")
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

  @Schema(description = "Supervision task date ")
  public ZonedDateTime getTaskDate() {
    return taskDate;
  }

  public void setTaskDate(ZonedDateTime taskDate) {
    this.taskDate = taskDate;
  }

  @Schema(description = "Supervision task type")
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  @Schema(description = "Supervision task status")
  public SupervisionTaskStatusType getStatus() {
    return status;
  }

  public void setStatus(SupervisionTaskStatusType status) {
    this.status = status;
  }

}
