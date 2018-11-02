package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;

import java.time.ZonedDateTime;

/**
 * Supervision task directs supervisors to supervise given applications and add their input as result.
 */
public class SupervisionTask {
  private Integer id;
  private Integer applicationId;
  private SupervisionTaskType type;
  private Integer creatorId;
  private Integer ownerId;
  private ZonedDateTime creationTime;
  private ZonedDateTime plannedFinishingTime;
  private ZonedDateTime actualFinishingTime;
  private SupervisionTaskStatusType status;
  private String description;
  private String result;
  private Integer locationId;

  public SupervisionTask() {
    // for deserialization
  }

  public SupervisionTask(
      Integer id,
      Integer applicationId,
      SupervisionTaskType type,
      Integer creatorId,
      Integer ownerId,
      ZonedDateTime creationTime,
      ZonedDateTime plannedFinishingTime,
      ZonedDateTime actualFinishingTime,
      SupervisionTaskStatusType status,
      String description,
      String result,
      Integer locationId) {
    this.id = id;
    this.applicationId = applicationId;
    this.type = type;
    this.creatorId = creatorId;
    this.ownerId = ownerId;
    this.creationTime = creationTime;
    this.plannedFinishingTime = plannedFinishingTime;
    this.actualFinishingTime = actualFinishingTime;
    this.status = status;
    this.description = description;
    this.result = result;
    this.locationId = locationId;
  }

  /**
   * Database id of the task.
   *
   * @return  Database id of the task.
   */
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  /**
   * Database id of the application related to the task.
   *
   * @return  Database id of the application related to the task.
   */
  public Integer getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(Integer applicationId) {
    this.applicationId = applicationId;
  }

  /**
   * Type of the supervision task.
   *
   * @return  Type of the supervision task.
   */
  public SupervisionTaskType getType() {
    return type;
  }

  public void setType(SupervisionTaskType type) {
    this.type = type;
  }

  /**
   * Creator of the task.
   *
   * @return  Creator of the task.
   */
  public Integer getCreatorId() {
    return creatorId;
  }

  public void setCreatorId(Integer creatorId) {
    this.creatorId = creatorId;
  }

  /**
   * Owner (supervisor person) of the task.
   *
   * @return  Owner (supervisor person) of the task.
   */
  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }

  /**
   * The creation time of the task.
   *
   * @return  The creation time of the task.
   */
  public ZonedDateTime getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(ZonedDateTime creationTime) {
    this.creationTime = creationTime;
  }

  /**
   * The time this task should be finished at the latest.
   *
   * @return  The time this task should be finished at the latest.
   */
  public ZonedDateTime getPlannedFinishingTime() {
    return plannedFinishingTime;
  }

  public void setPlannedFinishingTime(ZonedDateTime plannedFinishingTime) {
    this.plannedFinishingTime = plannedFinishingTime;
  }

  /**
   * The time the task was actually finished.
   *
   * @return  The time the task was actually finished.
   */
  public ZonedDateTime getActualFinishingTime() {
    return actualFinishingTime;
  }

  public void setActualFinishingTime(ZonedDateTime actualFinishingTime) {
    this.actualFinishingTime = actualFinishingTime;
  }

  /**
   * Status of the task.
   *
   * @return  Status of the task.
   */
  public SupervisionTaskStatusType getStatus() {
    return status;
  }

  public void setStatus(SupervisionTaskStatusType status) {
    this.status = status;
  }

  /**
   * Description of the task. Given by the task creator.
   *
   * @return  Description of the task. Given by the creator.
   */
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Result of the task. Written by the task owner.
   *
   * @return  Result of the task. Written by the task owner.
   */
  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Integer getLocationId() {
    return locationId;
  }

  public void setLocationId(Integer locationId) {
    this.locationId = locationId;
  }
}
