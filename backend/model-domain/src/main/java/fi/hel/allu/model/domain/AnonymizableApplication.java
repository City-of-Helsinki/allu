package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.ChangeType;

import java.time.ZonedDateTime;

/**
 * In Finnish: anonymisoitava/"poistettava" hakemus
 */
public class AnonymizableApplication {
  private Integer id;
  private String applicationId;
  private ApplicationType applicationType;
  private ZonedDateTime startTime;
  private ZonedDateTime endTime;
  private ChangeType changeType;
  private String changeSpecifier;
  private ZonedDateTime changeTime;

  public AnonymizableApplication() {
  }

  public AnonymizableApplication(Integer id, String applicationId, ApplicationType applicationType, ZonedDateTime startTime, ZonedDateTime endTime, ChangeType changeType, String changeSpecifier, ZonedDateTime changeTime) {
    this.id = id;
    this.applicationId = applicationId;
    this.applicationType = applicationType;
    this.startTime = startTime;
    this.endTime = endTime;
    this.changeType = changeType;
    this.changeSpecifier = changeSpecifier;
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

  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
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

  public String getChangeSpecifier() { return changeSpecifier; }

  public void setChangeSpecifier(String changeSpecifier) { this.changeSpecifier = changeSpecifier; }

  public ZonedDateTime getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(ZonedDateTime changeTime) {
    this.changeTime = changeTime;
  }
}
