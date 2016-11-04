package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fi.hel.allu.common.types.ApplicationCategory;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "applicationCategory")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OutdoorEventJson.class, name = "EVENT"),
    @JsonSubTypes.Type(value = ShortTermRentalJson.class, name = "SHORT_TERM_RENTAL")
})
public abstract class EventJson {
  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationCategory getApplicationCategory();
}

