package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application status change event")
public class ApplicationStatusEventExt implements Comparable<ApplicationStatusEventExt> {

  private ZonedDateTime eventTime;
  private String newStatus;
  private String applicationIdentifier;
  private String targetStatus;

  public ApplicationStatusEventExt() {
  }

  public ApplicationStatusEventExt(ZonedDateTime eventTime, String newStatus, String applicationIdentifier, String targetStatus) {
    this.eventTime = eventTime;
    this.newStatus = newStatus;
    this.applicationIdentifier = applicationIdentifier;
    this.targetStatus = targetStatus;
  }

  @ApiModelProperty(value = "Time of the application event")
  public ZonedDateTime getEventTime() {
    return eventTime;
  }

  public void setEventTime(ZonedDateTime eventTime) {
    this.eventTime = eventTime;
  }

  @ApiModelProperty(value = "Status of the application after the event")
  public String getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(String newStatus) {
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
    return result == 0 ? this.newStatus.compareTo(o.newStatus) : result;
  }

  @ApiModelProperty(value = "Target status. Tells next status (DECISION, OPERATIONAL_CONDITION or FINISHED) if current status is DECISIONMAKING.")
  public String getTargetStatus() {
    return targetStatus;
  }

  public void setTargetStatus(String targetStatus) {
    this.targetStatus = targetStatus;
  }

}
