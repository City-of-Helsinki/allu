package fi.hel.allu.common.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;

import java.time.ZonedDateTime;
import java.util.List;

public class SupervisionTaskSearchCriteria {
  private List<SupervisionTaskType> taskTypes;
  private String applicationId;
  private ZonedDateTime after;
  private ZonedDateTime before;
  private List<ApplicationType> applicationTypes;
  private List<StatusType> applicationStatus;
  private Integer ownerId;
  private List<Integer> cityDistrictIds;

  public List<SupervisionTaskType> getTaskTypes() {
    return taskTypes;
  }

  public void setTaskTypes(List<SupervisionTaskType> taskTypes) {
    this.taskTypes = taskTypes;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public ZonedDateTime getAfter() {
    return after;
  }

  public void setAfter(ZonedDateTime after) {
    this.after = after;
  }

  public ZonedDateTime getBefore() {
    return before;
  }

  public void setBefore(ZonedDateTime before) {
    this.before = before;
  }

  public List<ApplicationType> getApplicationTypes() {
    return applicationTypes;
  }

  public void setApplicationTypes(List<ApplicationType> applicationTypes) {
    this.applicationTypes = applicationTypes;
  }

  public List<StatusType> getApplicationStatus() {
    return applicationStatus;
  }

  public void setApplicationStatus(List<StatusType> applicationStatus) {
    this.applicationStatus = applicationStatus;
  }

  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }

  public List<Integer> getCityDistrictIds() {
    return cityDistrictIds;
  }

  public void setCityDistrictIds(List<Integer> cityDistrictIds) {
    this.cityDistrictIds = cityDistrictIds;
  }
}
