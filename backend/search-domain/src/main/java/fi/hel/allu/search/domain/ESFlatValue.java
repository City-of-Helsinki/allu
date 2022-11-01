package fi.hel.allu.search.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.wnameless.json.flattener.JsonifyArrayList;
import fi.hel.allu.common.json.DoubleSerializer;

import java.math.BigDecimal;

/**
 * Utility class for flattening any data structure as key-value pairs, which allows using ElasticSearch without predefined schema.
 */
public class ESFlatValue {
  private String fieldName;
  private Double decValue;
  private Integer integerValue;
  private Long longValue;
  private String strValue;
  private Boolean boolValue;

  public ESFlatValue() {
    // for JSON serialization
  }

  public ESFlatValue(String prefix, String fieldName, Double fieldValue) {
    this.fieldName = createFieldName(prefix, fieldName);
    this.decValue = fieldValue;
  }

  public ESFlatValue(String prefix, String fieldName, Long fieldValue) {
    this.fieldName = createFieldName(prefix, fieldName);
    this.longValue = fieldValue;
  }

  public ESFlatValue(String prefix, String fieldName, String fieldValue) {
    this.fieldName = createFieldName(prefix, fieldName);
    this.strValue = fieldValue;
  }

  public ESFlatValue(String prefix, String fieldName, Boolean fieldValue) {
    this.fieldName = createFieldName(prefix, fieldName);
    this.boolValue = fieldValue;
  }

  public ESFlatValue(String prefix, String fieldName, Integer fieldValue) {
    this.fieldName = createFieldName(prefix, fieldName);
    this.integerValue = fieldValue;
  }

  public static ESFlatValue mapValue(String prefix, String name, Object value) {
    if (value instanceof BigDecimal) {
      BigDecimal numValue = (BigDecimal) value;
      if (hasFraction(numValue)) {
        return new ESFlatValue(prefix, name, numValue.doubleValue());
      } else {
        return new ESFlatValue(prefix, name, numValue.longValue());
      }
    } else if (value instanceof Double) {
      return new ESFlatValue(prefix, name, (Double) value);
    } else if (value instanceof Long) {
      return new ESFlatValue(prefix, name, (Long) value);
    } else if (value instanceof String) {
      return new ESFlatValue(prefix, name, (String) value);
    } else if (value instanceof Boolean) {
      return  new ESFlatValue(prefix, name, (Boolean) value);
    } else if (value instanceof Integer) {
      return  new ESFlatValue(prefix, name,  (Integer)value);
    } else if (value == null) {
      return new ESFlatValue(prefix, name, (String) null);
    } else if (value instanceof JsonifyArrayList) {
      // we're skipping empty arrays. Arrays with data should not exist
      JsonifyArrayList jsonifyArrayList = (JsonifyArrayList) value;
      if (jsonifyArrayList.isEmpty()) {
        return new ESFlatValue(prefix, name, (String) null);
      } else {
        throw new UnsupportedOperationException("Unexpected array content during JSON flattening: " + jsonifyArrayList);
      }
    } else {
      throw new UnsupportedOperationException(
          "Mapping to JSON failed, name: " + name + ", value: " + value + ", class: " + value.getClass().toString());
    }
  }


  public String createFieldName(String prefix, String name) {
    return prefix + "-" + name;
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  @JsonSerialize(using = DoubleSerializer.class)
  public Double getDecValue() {
    return this.decValue;
  }

  public void setDecValue(Double decValue) {
    this.decValue = decValue == null ? null : decValue;
  }

  public Long getIntValue() {
    return longValue;
  }

  public void setIntValue(Long intValue) {
    this.longValue = intValue == null ? null : intValue;
  }

  public Boolean getBoolValue() {
    return boolValue;
  }

  public void setBoolValue(Boolean boolValue) {
    this.boolValue = boolValue;
  }

  public String getStrValue() {
    return strValue;
  }

  public void setStrValue(String strValue) {
    this.strValue = strValue;
  }

  public Integer getIntegerValue() {
    return integerValue;
  }

  public void setIntegerValue(Integer integerValue) {
    this.integerValue = integerValue;
  }

  private static  boolean hasFraction(BigDecimal number) {
    return number.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0;
  }
}
