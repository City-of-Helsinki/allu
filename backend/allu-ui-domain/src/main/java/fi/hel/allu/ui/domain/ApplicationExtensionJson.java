package fi.hel.allu.ui.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.hel.allu.common.types.ApplicationSpecifier;
import fi.hel.allu.common.types.ApplicationType;

import java.util.List;

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

  private List<ApplicationSpecifier> specifiers;
  private String terms;

  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationType getApplicationType();

  /**
   * Get the specifiers for the application extension.
   *
   * @return List of the specifiers, empty list, or null
   */
  public List<ApplicationSpecifier> getSpecifiers() {
    return specifiers;
  }

  public void setSpecifiers(List<ApplicationSpecifier> specifiers) {
    this.specifiers = specifiers;
  }

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
