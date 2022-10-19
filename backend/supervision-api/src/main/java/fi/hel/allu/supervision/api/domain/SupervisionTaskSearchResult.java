package fi.hel.allu.supervision.api.domain;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Supervision task")
public class SupervisionTaskSearchResult {

  private Integer id;
  private Integer applicationId;
  private Integer locationId;
  private Integer locationKey;
  private StatusType applicationStatus;
  private ApplicationType applicationType;
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

  @Schema(description = "Id of the supervision task")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  @Schema(description = "Id of the application this task belongs to")
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  @Schema(description = "ID of the location this task applies to (present only in work time supervision tasks of area rentals)")
  public Integer getLocationId() {
    return locationId;
  }

  public void setLocationId(Integer locationId) {
    this.locationId = locationId;
  }

  @Schema(description = "Key of the location this task applies to (present only in work time supervision tasks of area rentals)")
  public Integer getLocationKey() {
    return locationKey;
  }

  public void setLocationKey(Integer locationKey) {
    this.locationKey = locationKey;
  }

  @Schema(description = "Supervision task type")
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  @Schema(description = "Supervision task owner (supervisor)")
  public String getOwnerRealName() {
    return ownerRealName;
  }

  public void setOwnerRealName(String ownerRealName) {
    this.ownerRealName = ownerRealName;
  }

  @Schema(description = "Supervision task owner's username")
  public String getOwnerUserName() {
    return ownerUserName;
  }

  public void setOwnerUserName(String ownerUserName) {
    this.ownerUserName = ownerUserName;
  }

  @Schema(description = "Task creation time")
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  @Schema(description = "Planned finishing time for task")
  public ZonedDateTime getPlannedFinishingTime() {
    return plannedFinishingTime;
  }

  public void setPlannedFinishingTime(ZonedDateTime plannedFinishingTime) {
    this.plannedFinishingTime = plannedFinishingTime;
  }

  @Schema(description = "Actual finishing time")
  public ZonedDateTime getActualFinishingTime() {
    return actualFinishingTime;
  }

  public void setActualFinishingTime(ZonedDateTime actualFinishingTime) {
    this.actualFinishingTime = actualFinishingTime;
  }

  @Schema(description = "Status of the task")
  public SupervisionTaskStatusType getStatus() {
    return status;
  }

  public void setStatus(SupervisionTaskStatusType status) {
    this.status = status;
  }

  @Schema(description = "Task description")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Schema(description = "Result (supervisor's comment)")
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  @Schema(description = "Application identifier (hakemustunniste")
  public String getApplicationIdentifier() {
    return applicationIdentifier;
  }

  public void setApplicationIdentifier(String applicationIdentifier) {
    this.applicationIdentifier = applicationIdentifier;
  }

  @Schema(description = "Status of the application")
  public StatusType getApplicationStatus() {
    return applicationStatus;
  }

  public void setApplicationStatus(StatusType applicationStatus) {
    this.applicationStatus = applicationStatus;
  }

  @Schema(description = "Application type")
  public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  @Schema(description = "Address(es) of the supervision task.")
  public List<String> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<String> addresses) {
    this.addresses = addresses;
  }

}
