package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.types.ChangeType;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * A single change to an application. Can be creation, status change or contents
 * change. In case of a status change, the new status is provided. In case of
 * contents change, the changed field descriptions are provided.
 */
public class ChangeHistoryItemJson {
  private UserJson user;
  private ChangeHistoryItemInfoJson info;
  private ChangeType changeType;
  private String changeSpecifier;
  private String changeSpecifier2;
  private ZonedDateTime changeTime;
  private List<FieldChangeJson> fieldChanges;

  public ChangeHistoryItemJson() {
  }

  public ChangeHistoryItemJson(UserJson user, ChangeHistoryItemInfoJson info, ChangeType changeType,
      String changeSpecifier, String changeSpecifier2, ZonedDateTime changeTime, List<FieldChangeJson> fieldChanges) {
    this.user = user;
    this.info = info;
    this.changeType = changeType;
    this.changeSpecifier = changeSpecifier;
    this.changeSpecifier2 = changeSpecifier2;
    this.changeTime = changeTime;
    this.fieldChanges = fieldChanges;
  }

  public UserJson getUser() {
    return user;
  }

  public void setUser(UserJson user) {
    this.user = user;
  }

  public ChangeHistoryItemInfoJson getInfo() {
    return info;
  }

  public void setInfo(ChangeHistoryItemInfoJson info) {
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
   * Contains target status of application when change is status change.
   */
  public String getChangeSpecifier2() {
    return changeSpecifier2;
  }

  public void setChangeSpecifier2(String changeSpecifier2) {
    this.changeSpecifier2 = changeSpecifier2;
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
  public List<FieldChangeJson> getFieldChanges() {
    return fieldChanges;
  }

  public void setFieldChanges(List<FieldChangeJson> fieldChanges) {
    this.fieldChanges = fieldChanges;
  }
}
