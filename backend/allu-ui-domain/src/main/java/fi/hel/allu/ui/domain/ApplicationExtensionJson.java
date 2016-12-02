package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.hel.allu.common.types.ApplicationType;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "applicationType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EventJson.class, name = "EVENT"),
    @JsonSubTypes.Type(value = ShortTermRentalJson.class, name = "SHORT_TERM_RENTAL"),
    @JsonSubTypes.Type(value = CableReportJson.class, name = "CABLE_REPORT")
})
public abstract class ApplicationExtensionJson {

  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationType getApplicationType();

}

