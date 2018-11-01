package fi.hel.allu.external.domain;

import java.time.ZonedDateTime;

import fi.hel.allu.common.domain.types.StatusType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Application status change event")
public class ApplicationStatusEventExt implements Comparable<ApplicationStatusEventExt> {

  private ZonedDateTime eventTime;
  private StatusType newStatus;
  private Integer replacingApplicationId;

  public ApplicationStatusEventExt() {
  }

  public ApplicationStatusEventExt(ZonedDateTime eventTime, StatusType newStatus, Integer replacingApplicationId) {
    this.eventTime = eventTime;
    this.newStatus = newStatus;
    this.replacingApplicationId = replacingApplicationId;
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

  @ApiModelProperty(value = "ID of the replacing application ID if status is changed to REPLACED. Otherwise null.")
  public Integer getReplacingApplicationId() {
    return replacingApplicationId;
  }

  public void setReplacingApplicationId(Integer replacingApplicationId) {
    this.replacingApplicationId = replacingApplicationId;
  }

  @Override
  public int compareTo(ApplicationStatusEventExt o) {
    int result = this.getEventTime().compareTo(o.getEventTime());
    return result == 0 ? this.newStatus.name().compareTo(o.newStatus.name()) : result;
  }
}
