package fi.hel.allu.model.domain;

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
    @JsonSubTypes.Type(value = OutdoorEvent.class, name = "EVENT"),
    @JsonSubTypes.Type(value = ShortTermRental.class, name = "SHORT_TERM_RENTAL"),
    @JsonSubTypes.Type(value = CableReport.class, name = "CABLE_REPORT")
})
public abstract class Event {

  @NotNull
  private ApplicationType type;

  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationCategory getApplicationCategory();

  /**
   * Get the application type. The application type is a more fine-grained
   * specifier than category.
   */
  public ApplicationType getType() {
    return type;
  }

  public void setType(ApplicationType type) {
    this.type = type;
  }

}

