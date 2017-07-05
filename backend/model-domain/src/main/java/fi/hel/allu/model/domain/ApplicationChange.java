package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.common.domain.types.StatusType;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * A single change to an application. Can be creation, status change or contents
 * change. In case of a status change, the new status is provided. In case of
 * contents change, the changed field descriptions are provided.
 */
public class ApplicationChange {
  private Integer userId;
  private ChangeType changeType;
  private StatusType newStatus;
  private ZonedDateTime changeTime;
  private List<ApplicationFieldChange> fieldChanges;

  public ApplicationChange() {
  }

  public ApplicationChange(Integer userId, ChangeType changeType,
      StatusType newStatus, ZonedDateTime changeTime, List<ApplicationFieldChange> fieldChanges) {
    this.userId = userId;
    this.changeType = changeType;
    this.newStatus = newStatus;
    this.changeTime = changeTime;
    this.fieldChanges = fieldChanges;
  }

  /**
   * Get the database ID for the user who made the change.
   *
   * @return
   */
  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  /**
   * Get the change type (@see ChangeType)
   *
   * @return
   */
  public ChangeType getChangeType() {
    return changeType;
  }

  public void setChangeType(ChangeType changeType) {
    this.changeType = changeType;
  }

  /**
   * Get the new status if this was a status change change
   *
   * @return new status or null
   */
  public StatusType getNewStatus() {
    return newStatus;
  }

  public void setNewStatus(StatusType newStatus) {
    this.newStatus = newStatus;
  }

  /**
   * Get the time of the change
   *
   * @return
   */
  public ZonedDateTime getChangeTime() {
    return changeTime;
  }

  public void setChangeTime(ZonedDateTime changeTime) {
    this.changeTime = changeTime;
  }

  /**
   * Get the list of changed fields if this was an application content change
   *
   * @return list of fields, empty list, or null
   */
  public List<ApplicationFieldChange> getFieldChanges() {
    return fieldChanges;
  }

  public void setFieldChanges(List<ApplicationFieldChange> fieldChanges) {
    this.fieldChanges = fieldChanges;
  }
}
