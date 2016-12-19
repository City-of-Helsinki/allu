package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.hel.allu.common.types.ApplicationType;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "applicationType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Event.class, name = "EVENT"),
    @JsonSubTypes.Type(value = ShortTermRental.class, name = "SHORT_TERM_RENTAL"),
    @JsonSubTypes.Type(value = CableReport.class, name = "CABLE_REPORT"),
    @JsonSubTypes.Type(value = ExcavationAnnouncement.class, name = "EXCAVATION_ANNOUNCEMENT")
})
public abstract class ApplicationExtension {


  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationType getApplicationType();

}

