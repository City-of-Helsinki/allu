package fi.hel.allu.servicecore.domain.supervision;

import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.domain.SupervisionTaskLocation;
import fi.hel.allu.servicecore.domain.UserJson;

public class SupervisionTaskJson {
  private Integer id;
  private Integer applicationId;
  private SupervisionTaskType type;
  private UserJson creator;
  private UserJson owner;
  private ZonedDateTime creationTime;
  private ZonedDateTime plannedFinishingTime;
  private ZonedDateTime actualFinishingTime;
  private SupervisionTaskStatusType status;
  private String description;
  private String result;
  private Integer locationId;
  private List<SupervisionTaskLocation> supervisedLocations;


  public SupervisionTaskJson() {
    // for JSON deserialization
  }

  public SupervisionTaskJson(
      Integer id,
      Integer applicationId,
      SupervisionTaskType type,
      UserJson creator,
      UserJson owner,
      ZonedDateTime creationTime,
      ZonedDateTime plannedFinishingTime,
      ZonedDateTime actualFinishingTime,
      SupervisionTaskStatusType status,
      String description,
      String result,
      Integer locationId,
      List<SupervisionTaskLocation> supervisedLocations) {
    this.id = id;
    this.applicationId = applicationId;
    this.type = type;
    this.creator = creator;
    this.owner = owner;
    this.creationTime = creationTime;
    this.plannedFinishingTime = plannedFinishingTime;
    this.actualFinishingTime = actualFinishingTime;
    this.status = status;
    this.description = description;
    this.result = result;
    this.locationId = locationId;
    this.supervisedLocations = supervisedLocations;
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
  public UserJson getCreator() {
    return creator;
  }

  public void setCreator(UserJson creator) {
    this.creator = creator;
  }

  /**
   * Owner (supervisor person) of the task.
   *
   * @return  Owner (supervisor person) of the task.
   */
  public UserJson getOwner() {
    return owner;
  }

  public void setOwner(UserJson owner) {
    this.owner = owner;
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
   * Result of the task. Written by the task handler.
   *
   * @return  Result of the task. Written by the task handler.
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

  /**
   * Location(s) of application when task was approved / rejected
   */
  public List<SupervisionTaskLocation> getSupervisedLocations() {
    return supervisedLocations;
  }

  public void setSupervisedLocations(List<SupervisionTaskLocation> supervisedLocations) {
    this.supervisedLocations = supervisedLocations;
  }

}
