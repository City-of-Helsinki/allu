package fi.hel.allu.external.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.TrafficArrangementImpedimentType;
import fi.hel.allu.common.validator.NotFalse;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@NotFalse(rules = {
    "applicationKind, kindMatchType, {trafficarrangements.kind}"
 })
@Schema(description ="Temporary traffic arrangement (tilapäinen liikennejärjestely) input model.")
public class TrafficArrangementExt extends BaseApplicationExt {

  @NotNull(message = "{trafficarrangements.contractor}")
  @Valid
  private CustomerWithContactsExt contractorWithContacts;
  @Valid
  private CustomerWithContactsExt propertyDeveloperWithContacts;

  @NotNull(message = "{application.kind}")
  private ApplicationKind applicationKind;
  private String trafficArrangements;
  @NotNull(message = "{application.trafficarrangements.trafficArrangementImpedimentType}")
  private TrafficArrangementImpedimentType trafficArrangementImpediment;
  @NotNull(message = "{application.workPurpose}")
  private String workPurpose;

  @Schema(description = "Contractor (työn suorittaja)", required = true)
  public CustomerWithContactsExt getContractorWithContacts() {
    return contractorWithContacts;
  }

  public void setContractorWithContacts(CustomerWithContactsExt contractorWithContacts) {
    this.contractorWithContacts = contractorWithContacts;
  }

  @Schema(description = "Property developer (rakennuttaja)")
  public CustomerWithContactsExt getPropertyDeveloperWithContacts() {
    return propertyDeveloperWithContacts;
  }

  public void setPropertyDeveloperWithContacts(CustomerWithContactsExt propertyDeveloperWithContacts) {
    this.propertyDeveloperWithContacts = propertyDeveloperWithContacts;
  }

  @Schema(description = "Application kind.", required = true)
  public ApplicationKind getApplicationKind() {
    return applicationKind;
  }

  public void setApplicationKind(ApplicationKind applicationKind) {
    this.applicationKind = applicationKind;
  }

  @Schema(description = "Traffic arrangements (suoritettavat liikennejärjestelyt)")
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }

  @Schema(description = "Traffic arrangement impediment (liikennejärjestelyn haitta)")
  public TrafficArrangementImpedimentType getTrafficArrangementImpediment() {
    return trafficArrangementImpediment;
  }

  public void setTrafficArrangementImpediment(TrafficArrangementImpedimentType trafficArrangementImpediment) {
    this.trafficArrangementImpediment = trafficArrangementImpediment;
  }

  @Schema(description = "Work purpose (työn tarkoitus)", required = true)
  public String getWorkPurpose() {
    return workPurpose;
  }

  public void setWorkPurpose(String workPurpose) {
    this.workPurpose = workPurpose;
  }

  @JsonIgnore
  public boolean getKindMatchType() {
    if (applicationKind != null) {
      return applicationKind.getTypes().contains(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS);
    }
    return true;
  }
}
