package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ChangeType;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * A single change to an application. Can be creation, status change or contents
 * change. In case of a status change, the new status and possible target status is provided. In case of
 * contents change, the changed field descriptions are provided.
 */
public class ChangeHistoryItem {
  private Integer userId;
  private ChangeHistoryItemInfo info;
  private ChangeType changeType;
  private String changeSpecifier;
  private String changeSpecifier2;
  private ZonedDateTime changeTime;
  private List<FieldChange> fieldChanges;

  public ChangeHistoryItem() {
  }

  public ChangeHistoryItem(Integer userId, ChangeHistoryItemInfo info, ChangeType changeType,
      String newStatus, ZonedDateTime changeTime, List<FieldChange> fieldChanges) {
    this.userId = userId;
    this.info = info;
    this.changeType = changeType;
    this.changeSpecifier = newStatus;
    this.changeTime = changeTime;
    this.fieldChanges = fieldChanges;
  }

  public ChangeHistoryItem(Integer userId, ChangeHistoryItemInfo info, ChangeType changeType,
      String newStatus, ZonedDateTime changeTime, List<FieldChange> fieldChanges, String changeSpecifier2) {
    this(userId, info, changeType, newStatus, changeTime, fieldChanges);
    this.changeSpecifier2 = changeSpecifier2;
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

  public ChangeHistoryItemInfo getInfo() {
    return info;
  }

  public void setInfo(ChangeHistoryItemInfo info) {
    this.info = info;
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
   * Get the customer role type if this was a customer change.
   */
  public String getChangeSpecifier() {
    return changeSpecifier;
  }

  public void setChangeSpecifier(String changeSpecifier) {
    this.changeSpecifier = changeSpecifier;
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
  public List<FieldChange> getFieldChanges() {
    return fieldChanges;
  }

  public void setFieldChanges(List<FieldChange> fieldChanges) {
    this.fieldChanges = fieldChanges;
  }

  public String getChangeSpecifier2() {
    return changeSpecifier2;
  }

  public void setChangeSpecifier2(String changeSpecifier2) {
    this.changeSpecifier2 = changeSpecifier2;
  }
}
