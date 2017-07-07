package fi.hel.allu.servicecore.domain;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Search parameter mapping for UI. Any of the search fields may be left empty. If all of them are empty, the query parameter is ignored.
 */
public class QueryParameterJson {

  private String fieldName;
  private String fieldValue;
  private List<String> fieldMultiValue;
  private ZonedDateTime startDateValue;
  private ZonedDateTime endDateValue;

  public QueryParameterJson() {
    // JSON deserialization
  }

  public QueryParameterJson(String fieldName, List<String> fieldMultiValue) {
    this.fieldName = fieldName;
    this.fieldMultiValue = fieldMultiValue;
  }

  /**
   * Returns the name of the search field. This is the field name ElasticSearch is also using. If magical field name "_all" is used, the
   * query is done against all fields.
   *
   * @return  the name of the search field. This is the field name ElasticSearch is also using.
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
