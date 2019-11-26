package fi.hel.allu.common.exception;

public class FieldUpdateException extends IllegalArgumentException implements ExceptionWithExtraInfo {
  private String fieldName;

  public FieldUpdateException(String s, String fieldName) {
    super(s);
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getExtraInfo() {
    return getFieldName();
  }

}
