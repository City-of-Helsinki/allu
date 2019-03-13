package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Supervision task")
public class SupervisionTaskSearchResult {

  private Integer id;
  private Integer applicationId;
  private StatusType applicationStatus;
  private String applicationIdentifier;
  private SupervisionTaskType type;
  private String ownerRealName;
  private String ownerUserName;
  private ZonedDateTime creationTime;
  private ZonedDateTime plannedFinishingTime;
  private ZonedDateTime actualFinishingTime;
  private SupervisionTaskStatusType status;
  private String description;
  private String result;
  private List<String> addresses;

  public SupervisionTaskSearchResult() {
  }

  @ApiModelProperty(value = "Id of the supervision task")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @ApiModelProperty(value = "Id of the application this task belongs to")
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  @ApiModelProperty(value = "Supervision task type")
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  @ApiModelProperty(value = "Supervision task owner (supervisor)")
  public String getOwnerRealName() {
    return ownerRealName;
  }

  public void setOwnerRealName(String ownerRealName) {
    this.ownerRealName = ownerRealName;
  }

  @ApiModelProperty(value = "Supervision task owner's username")
  public String getOwnerUserName() {
    return ownerUserName;
  }

  public void setOwnerUserName(String ownerUserName) {
    this.ownerUserName = ownerUserName;
  }

  @ApiModelProperty(value = "Task creation time")
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @ApiModelProperty(value = "Planned finishing time for task")
  public ZonedDateTime getPlannedFinishingTime() {
    return plannedFinishingTime;
  }

  public void setPlannedFinishingTime(ZonedDateTime plannedFinishingTime) {
    this.plannedFinishingTime = plannedFinishingTime;
  }

  @ApiModelProperty(value = "Actual finishing time")
  public ZonedDateTime getActualFinishingTime() {
    return actualFinishingTime;
  }

  public void setActualFinishingTime(ZonedDateTime actualFinishingTime) {
    this.actualFinishingTime = actualFinishingTime;
  }

  @ApiModelProperty(value = "Status of the task")
  public SupervisionTaskStatusType getStatus() {
    return status;
  }

  public void setStatus(SupervisionTaskStatusType status) {
    this.status = status;
  }

  @ApiModelProperty(value = "Task description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @ApiModelProperty(value = "Result (supervisor's comment)")
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @ApiModelProperty(value = "Application identifier (hakemustunniste")
  public String getApplicationIdentifier() {
    return applicationIdentifier;
  }

  public void setApplicationIdentifier(String applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
  }

  @ApiModelProperty(value = "Status of the application")
  public StatusType getApplicationStatus() {
    return applicationStatus;
  }

  public void setApplicationStatus(StatusType applicationStatus) {
    this.applicationStatus = applicationStatus;
  }

  @ApiModelProperty(value = "Address(es) of the supervision task.")
  public List<String> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<String> addresses) {
    this.addresses = addresses;
  }

}
