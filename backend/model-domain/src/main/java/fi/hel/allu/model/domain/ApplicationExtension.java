package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.hel.allu.common.domain.types.ApplicationType;

import org.springframework.validation.annotation.Validated;


@Validated
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "applicationType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Event.class, name = "EVENT"),
    @JsonSubTypes.Type(value = ShortTermRental.class, name = "SHORT_TERM_RENTAL"),
    @JsonSubTypes.Type(value = CableReport.class, name = "CABLE_REPORT"),
    @JsonSubTypes.Type(value = ExcavationAnnouncement.class, name = "EXCAVATION_ANNOUNCEMENT"),
    @JsonSubTypes.Type(value = Note.class, name = "NOTE"),
    @JsonSubTypes.Type(value = TrafficArrangement.class, name = "TEMPORARY_TRAFFIC_ARRANGEMENTS"),
    @JsonSubTypes.Type(value = PlacementContract.class, name = "PLACEMENT_CONTRACT"),
    @JsonSubTypes.Type(value = AreaRental.class, name = "AREA_RENTAL")
})
public abstract class ApplicationExtension {

  private String terms;

  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationType getApplicationType();

  /**
   * Terms for application extension.
   *
   * @return Terms as string which can be null.
   */
  public String getTerms() {
    return terms;
  }

  public void setTerms(String terms) {
    this.terms = terms;
  }

}

