package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supervision task approval or rejection event")
public class SupervisionEventExt  implements Comparable<SupervisionEventExt> {

  private ZonedDateTime eventTime;
  private SupervisionTaskType type;
  private SupervisionTaskStatusType status;
  private String comment;

  public SupervisionEventExt() {
  }

  public SupervisionEventExt(ZonedDateTime eventTime, SupervisionTaskType type, SupervisionTaskStatusType status,
      String comment) {
    this.eventTime = eventTime;
    this.type = type;
    this.status = status;
    if (status == SupervisionTaskStatusType.REJECTED) {
      this.comment = comment;
    }
  }

  public SupervisionTaskType getType() {
    return type;
  }

  @Schema(description = "Supervision task type")
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

  @Schema(description = "Supervisor's comment")
  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public ZonedDateTime getEventTime() {
    return eventTime;
  }

  public void setEventTime(ZonedDateTime eventTime) {
    this.eventTime = eventTime;
  }

  @Override
  public int compareTo(SupervisionEventExt o) {
    int result = this.getEventTime().compareTo(o.getEventTime());
    return result == 0 ? this.type.name().compareTo(o.type.name()) : result;
  }
}
