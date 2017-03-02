package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.DefaultTextType;

public class CableInfoText {
  private Integer id;
  private DefaultTextType cableInfoType;
  private String textValue;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public DefaultTextType getCableInfoType() {
    return cableInfoType;
  }

  public void setCableInfoType(DefaultTextType defaultTextType) {
    this.cableInfoType = defaultTextType;
  }

  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }
}
