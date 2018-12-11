package fi.hel.allu.servicecore.domain.history;

import com.fasterxml.jackson.annotation.JsonProperty;
import fi.hel.allu.common.domain.types.ApplicationTagType;

public class ApplicationTagForHistory {

  private ApplicationTagType type;

  /**
   * Add a fake "id" field during serialization so that comparison of tag lists
   * in allu-ui-service's ObjectComparer compares by ID.
   */
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  public int getId() {
    return getType().ordinal();
  }

  public ApplicationTagForHistory() {
  }

  public ApplicationTagForHistory(ApplicationTagType type) {
    this.type = type;
  }

  public ApplicationTagType getType() {
    return type;
  }

  public void setType(ApplicationTagType type) {
    this.type = type;
  }
}
