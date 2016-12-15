package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.CableInfoType;

public class CableInfoText {
  private Integer id;
  private CableInfoType cableInfoType;
  private String textValue;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public CableInfoType getCableInfoType() {
    return cableInfoType;
  }

  public void setCableInfoType(CableInfoType cableInfoType) {
    this.cableInfoType = cableInfoType;
  }

  public String getTextValue() {
    return textValue;
  }

  public void setTextValue(String textValue) {
    this.textValue = textValue;
  }
}
