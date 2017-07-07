package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import fi.hel.allu.common.types.DefaultTextType;

public class CableInfoEntryJson {

  private DefaultTextType type;
  private String additionalInfo;

  /**
   * Add a fake "id" field during serialization so that comparison of tag lists
   * in allu-ui-service's ObjectComparer compares by ID.
   */
  @JsonProperty(access = Access.READ_ONLY)
  public int getId() {
    return type.ordinal();
  }

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
