package fi.hel.allu.model.domain;

/**
 * Represents a single field change in change history
 */
public class FieldChange {
  private String fieldName;
  private String oldValue;
  private String newValue;

  public FieldChange() {
  }

  public FieldChange(String fieldName, String oldValue, String newValue) {
    this.fieldName = fieldName;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  /**
   * Get the field name. This should be a xpath, like "/customer/firstName"
   *
   * @return the field name
   */
  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  /**
   * Get the string representation of the old value.
   *
   * @return
   */
  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  /**
   * Get the string representation of the new value.
   *
   * @return
   */
  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }
}
