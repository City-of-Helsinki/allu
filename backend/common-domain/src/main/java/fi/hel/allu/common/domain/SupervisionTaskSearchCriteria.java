package fi.hel.allu.common.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;

import java.time.ZonedDateTime;
import java.util.List;

public class SupervisionTaskSearchCriteria {
  private List<Integer> owners;
  private List<SupervisionTaskType> taskTypes;
  private String applicationId;
  private ZonedDateTime after;
  private ZonedDateTime before;
  private List<ApplicationType> applicationTypes;
  private List<StatusType> applicationStatus;
  private List<Integer> cityDistrictIds;
  private List<SupervisionTaskStatusType> statuses;
  private List<Integer> applicationIds;

  public List<Integer> getOwners() {
    return owners;
  }

  public void setOwners(List<Integer> owners) {
    this.owners = owners;
  }

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

  public List<Integer> getCityDistrictIds() {
    return cityDistrictIds;
  }

  public void setCityDistrictIds(List<Integer> cityDistrictIds) {
    this.cityDistrictIds = cityDistrictIds;
  }

  public List<SupervisionTaskStatusType> getStatuses() {
    return statuses;
  }

  public void setStatuses(List<SupervisionTaskStatusType> statuses) {
    this.statuses = statuses;
  }

  public List<Integer> getApplicationIds() {
    return applicationIds;
  }

  public void setApplicationIds(List<Integer> applicationIds) {
    this.applicationIds = applicationIds;
  }
}
