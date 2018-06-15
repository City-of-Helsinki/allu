package fi.hel.allu.servicecore.domain.supervision;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.servicecore.domain.PostalAddressJson;
import fi.hel.allu.servicecore.domain.UserJson;

import java.time.ZonedDateTime;

public class SupervisionWorkItemJson {
  private Integer id;
  private SupervisionTaskType type;
  private Integer applicationId;
  private String applicationIdText;
  private StatusType applicationStatus;
  private UserJson creator;
  private ZonedDateTime plannedFinishingTime;
  private PostalAddressJson postalAddress;
  private String address;
  private String projectName;
  private UserJson owner;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
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

  public UserJson getCreator() {
    return creator;
  }

  public void setCreator(UserJson creator) {
    this.creator = creator;
  }

  public ZonedDateTime getPlannedFinishingTime() {
    return plannedFinishingTime;
  }

  public void setPlannedFinishingTime(ZonedDateTime plannedFinishingTime) {
    this.plannedFinishingTime = plannedFinishingTime;
  }

  public PostalAddressJson getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(PostalAddressJson postalAddress) {
    this.postalAddress = postalAddress;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }

  public UserJson getOwner() {
    return owner;
  }

  public void setOwner(UserJson owner) {
    this.owner = owner;
  }
}
