package fi.hel.allu.model.domain;

import fi.hel.allu.common.domain.types.CodeSetType;

public class CodeSet {
  private Integer id;
  private CodeSetType type;
  private String code;
  private String description;
  private String value;

  public CodeSet() {
  }

  public CodeSet(CodeSetType type, String code, String description, String value) {
    this.type = type;
    this.code = code;
    this.description = description;
    this.value = value;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public CodeSetType getType() {
    return type;
  }

  public void setType(CodeSetType type) {
    this.type = type;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
