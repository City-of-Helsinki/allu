package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.user.User;

import java.time.ZonedDateTime;

public class SupervisionWorkItem {
  private Integer id;
  private SupervisionTypeES type;
  private Integer applicationId;
  private String applicationIdText;
  private StatusType applicationStatus;
  private User creator;
  private ZonedDateTime plannedFinishingTime;
  private String[] address;
  private String projectName;
  private User owner;
  private Integer cityDistrictId;
  private ApplicationType applicationType;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public SupervisionTypeES getType() {
    return type;
  }

  public void setType(SupervisionTypeES type) {
    this.type = type;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  public String getApplicationIdText() {
    return applicationIdText;
  }

  public void setApplicationIdText(String applicationIdText) {
    this.applicationIdText = applicationIdText;
  }

  public StatusType getApplicationStatus() {
    return applicationStatus;
  }

  public void setApplicationStatus(StatusType applicationStatus) {
    this.applicationStatus = applicationStatus;
  }

  public User getCreator() {
    return creator;
  }

  public void setCreator(User creator) {
    this.creator = creator;
  }

  public ZonedDateTime getPlannedFinishingTime() {
    return plannedFinishingTime;
  }

  public void setPlannedFinishingTime(ZonedDateTime plannedFinishingTime) {
    this.plannedFinishingTime = plannedFinishingTime;
  }

  public String[] getAddress() {
    return address;
  }

  public void setAddress(String[] address) {
    this.address = address;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public Integer getCityDistrictId() {
    return cityDistrictId;
  }

  public void setCityDistrictId(Integer cityDistrictId) {
    this.cityDistrictId = cityDistrictId;
  }

  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }
}