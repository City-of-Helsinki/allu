package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.hel.allu.common.types.ApplicationCategory;
import fi.hel.allu.common.types.ApplicationType;

import javax.validation.constraints.NotNull;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "applicationCategory")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OutdoorEventJson.class, name = "EVENT"),
    @JsonSubTypes.Type(value = ShortTermRentalJson.class, name = "SHORT_TERM_RENTAL"),
    @JsonSubTypes.Type(value = CableReportJson.class, name = "CABLE_REPORT")
})
public abstract class EventJson {
  @NotNull(message = "{event.type}")
  private ApplicationType type;

  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationCategory getApplicationCategory();

  /**
   * The type of the application.
   *
   * @return type of the application.
   */
  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }
}

