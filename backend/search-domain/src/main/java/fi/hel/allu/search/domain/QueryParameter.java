package fi.hel.allu.search.domain;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Class for defining a single search query parameter. It's worth noticing that due to JSON serialization constraints, this class has
 * not been split up into specialized classes such as <code>QueryParameter</code> and <code>QueryMultiParameter</code>.
 */
public class QueryParameter {

  /** Application type search field name in Elastic Search */
  public static final String FIELD_NAME_APPLICATION_TYPE = "type.value";
  /** Application status search field name in Elastic Search */
  public static final String FIELD_NAME_STATUS = "status.value";

  private String fieldName;
  private String fieldValue;
  private List<String> fieldMultiValue;
  private ZonedDateTime startDateValue;
  private ZonedDateTime endDateValue;

  public QueryParameter() {
  }

  public QueryParameter(String fieldName, String fieldValue) {
    this.fieldName = fieldName;
    this.fieldValue = fieldValue;
  }

  public QueryParameter(String fieldName, List<String> fieldMultiValue) {
    this.fieldName = fieldName;
    this.fieldMultiValue = fieldMultiValue;
  }

  public QueryParameter(String fieldName, ZonedDateTime startDateValue, ZonedDateTime endDateValue) {
    this.fieldName = fieldName;
    this.startDateValue = startDateValue;
    this.endDateValue = endDateValue;
  }

  /**
   * Returns the name of the search field. This is the field name ElasticSearch is also using.
   *
   * @return
   */
  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  /**
   * Returns single search value.
   *
   * @return  single search value or <code>null</code> if this is not single value search.
   */
  public String getFieldValue() {
    return fieldValue;
  }

  public void setFieldValue(String fieldValue) {
    this.fieldValue = fieldValue;
  }

  /**
   * Returns search value with multiple alternatives i.e. search is a match if given field contains any of the listed values.
   *
   * @return  multi search value or <code>null</code> if this is not multi value search.
   */
  public List<String> getFieldMultiValue() {
    return fieldMultiValue;
  }

  public void setFieldMultiValue(List<String> fieldMultiValue) {
    this.fieldMultiValue = fieldMultiValue;
  }

  /**
   * Returns the date range start search is using.
   *
   * @return  the date range start search is using or <code>null</code> if date has not been specified.
   */
  public ZonedDateTime getStartDateValue() {
    return startDateValue;
  }

  public void setStartDateValue(ZonedDateTime startDateValue) {
    this.startDateValue = startDateValue;
  }

  /**
   * Returns the date range end search is using.
   *
   * @return  the date range end search is using or <code>null</code> if date has not been specified.
   */
  public ZonedDateTime getEndDateValue() {
    return endDateValue;
  }

  public void setEndDateValue(ZonedDateTime endDateValue) {
    this.endDateValue = endDateValue;
  }
}
