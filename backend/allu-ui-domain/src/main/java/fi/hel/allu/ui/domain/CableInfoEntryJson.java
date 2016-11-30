package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.CableInfoType;

public class CableInfoEntryJson {

  private CableInfoType type;
  private String additionalInfo;

  public CableInfoType getType() {
    return type;
  }

  public void setType(CableInfoType type) {
    this.type = type;
  }

  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

}
