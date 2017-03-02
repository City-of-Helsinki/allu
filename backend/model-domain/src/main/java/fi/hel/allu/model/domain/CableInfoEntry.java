package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.DefaultTextType;

public class CableInfoEntry {

  private DefaultTextType type;
  private String additionalInfo;

  public DefaultTextType getType() {
    return type;
  }

  public void setType(DefaultTextType type) {
    this.type = type;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

}
