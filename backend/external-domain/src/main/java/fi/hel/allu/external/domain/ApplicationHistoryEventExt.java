package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application history event")
public class ApplicationHistoryEventExt implements Comparable<ApplicationHistoryEventExt>{

  private ZonedDateTime eventTime;
  private StatusType newStatus;

  public ApplicationHistoryEventExt() {
  }

  public ApplicationHistoryEventExt(ZonedDateTime eventTime, StatusType newStatus) {
    this.eventTime = eventTime;
    this.newStatus = newStatus;
  }

  @ApiModelProperty(value = "Time of the application event")
  public ZonedDateTime getEventTime() {
    return eventTime;
  }

  public void setEventTime(ZonedDateTime eventTime) {
    this.eventTime = eventTime;
  }

  public StatusType getNewStatus() {
    return newStatus;
  }

  @ApiModelProperty(value = "Status of the application after the event")
  public void setNewStatus(StatusType newStatus) {
    this.newStatus = newStatus;
  }

  @Override
  public int compareTo(ApplicationHistoryEventExt o) {
    int result = this.getEventTime().compareTo(o.getEventTime());
    return result == 0 ? this.newStatus.name().compareTo(o.newStatus.name()) : result;
  }
}
