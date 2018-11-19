package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application status change event")
public class ApplicationStatusEventExt implements Comparable<ApplicationStatusEventExt> {

  private ZonedDateTime eventTime;
  private StatusType newStatus;
  private String applicationIdentifier;

  public ApplicationStatusEventExt() {
  }

  public ApplicationStatusEventExt(ZonedDateTime eventTime, StatusType newStatus, String applicationIdentifier) {
    this.eventTime = eventTime;
    this.newStatus = newStatus;
    this.applicationIdentifier = applicationIdentifier;
  }

  @ApiModelProperty(value = "Time of the application event")
  public ZonedDateTime getEventTime() {
    return eventTime;
  }

  public void setEventTime(ZonedDateTime eventTime) {
    this.eventTime = eventTime;
  }

  @ApiModelProperty(value = "Status of the application after the event")
  public StatusType getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(StatusType newStatus) {
    this.newStatus = newStatus;
  }

  @ApiModelProperty(value = "Application identifier (hakemustunniste)")
  public String getApplicationIdentifier() {
    return applicationIdentifier;
  }

  public void setApplicationIdentifier(String applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
  }

  @Override
  public int compareTo(ApplicationStatusEventExt o) {
    int result = this.getEventTime().compareTo(o.getEventTime());
    return result == 0 ? this.newStatus.name().compareTo(o.newStatus.name()) : result;
  }

}
