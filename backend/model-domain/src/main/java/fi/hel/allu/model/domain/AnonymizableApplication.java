package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ChangeType;

import java.time.ZonedDateTime;

/**
 * In Finnish: anonymisoitava/"poistettava" hakemus
 */
public class AnonymizableApplication {
  private Integer id;
  private String applicationId;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ChangeType changeType;
  private ZonedDateTime changeTime;

  public AnonymizableApplication() {
  }

  public AnonymizableApplication(Integer id, String applicationId, ZonedDateTime startTime, ZonedDateTime endTime, ChangeType changeType, ZonedDateTime changeTime) {
    this.id = id;
    this.applicationId = applicationId;
    this.startTime = startTime;
    this.endTime = endTime;
    this.changeType = changeType;
    this.changeTime = changeTime;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public ZonedDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(ZonedDateTime startTime) {
    this.startTime = startTime;
  }

  public ZonedDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(ZonedDateTime endTime) {
    this.endTime = endTime;
  }

  public ChangeType getChangeType() {
    return changeType;
  }

  public void setChangeType(ChangeType changeType) {
    this.changeType = changeType;
  }

  public ZonedDateTime getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(ZonedDateTime changeTime) {
    this.changeTime = changeTime;
  }
}
