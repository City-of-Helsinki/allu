package fi.hel.allu.servicecore.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.servicecore.domain.mapper.UpdatableProperty;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "applicationType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = EventJson.class, name = "EVENT"),
    @JsonSubTypes.Type(value = ShortTermRentalJson.class, name = "SHORT_TERM_RENTAL"),
    @JsonSubTypes.Type(value = CableReportJson.class, name = "CABLE_REPORT"),
    @JsonSubTypes.Type(value = ExcavationAnnouncementJson.class, name = "EXCAVATION_ANNOUNCEMENT"),
    @JsonSubTypes.Type(value = NoteJson.class, name="NOTE"),
    @JsonSubTypes.Type(value = TrafficArrangementJson.class, name="TEMPORARY_TRAFFIC_ARRANGEMENTS"),
    @JsonSubTypes.Type(value = PlacementContractJson.class, name="PLACEMENT_CONTRACT"),
    @JsonSubTypes.Type(value = AreaRentalJson.class, name="AREA_RENTAL")
})
public abstract class ApplicationExtensionJson {

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

  @UpdatableProperty
  public void setTerms(String terms) {
    this.terms = terms;
  }
}
