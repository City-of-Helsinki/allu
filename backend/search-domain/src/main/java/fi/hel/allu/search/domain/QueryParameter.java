package fi.hel.allu.search.domain;

import java.time.ZonedDateTime;

public class QueryParameter {
  private String fieldName;
  private String fieldValue;
  private ZonedDateTime startDateValue;
  private ZonedDateTime endDateValue;

  public ZonedDateTime getStartDateValue() {
    return startDateValue;
  }

  public void setStartDateValue(ZonedDateTime startDateValue) {
    this.startDateValue = startDateValue;
  }

  public ZonedDateTime getEndDateValue() {
    return endDateValue;
  }

  public void setEndDateValue(ZonedDateTime endDateValue) {
    this.endDateValue = endDateValue;
  }

  public String getFieldValue() {
    return fieldValue;
  }

  public void setFieldValue(String fieldValue) {
    this.fieldValue = fieldValue;
  }
  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }
}
