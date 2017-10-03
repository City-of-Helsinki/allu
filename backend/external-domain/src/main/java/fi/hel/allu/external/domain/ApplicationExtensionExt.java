package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fi.hel.allu.common.domain.types.ApplicationType;
import org.springframework.validation.annotation.Validated;

/**
 * Allu application extension, which is exposed to external users.
 */
@Validated
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "applicationType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ExcavationAnnouncementExt.class, name = "EXCAVATION_ANNOUNCEMENT"),
    @JsonSubTypes.Type(value = PlacementContractExt.class, name = "PLACEMENT_CONTRACT")
})
public abstract class ApplicationExtensionExt {

//  private List<ApplicationSpecifier> specifiers;
  private String terms;

  /**
   * Get the application category for the event. Each subclass must provide unique
   * value that matches class-specific name field in the above @JsonSubtypes
   * array.
   */
  public abstract ApplicationType getApplicationType();

  // TODO: implement once the fate of application specifiers has been decided
//  /**
//   * Get the specifiers for the application extension.
//   *
//   * @return List of the specifiers, empty list, or null
//   */
//  public List<ApplicationSpecifier> getSpecifiers() {
//    return specifiers;
//  }
//
//  public void setSpecifiers(List<ApplicationSpecifier> specifiers) {
//    this.specifiers = specifiers;
//  }

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
