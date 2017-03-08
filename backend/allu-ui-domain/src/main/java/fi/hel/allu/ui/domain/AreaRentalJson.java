package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationType;

import javax.validation.constraints.NotNull;

/**
 * Area rental (aluevuokraus) specific data.
 */
public class AreaRentalJson extends ApplicationExtensionJson {
  @NotNull(message = "{application.arearental.contractor}")
  private ApplicantJson contractor;
  @NotNull(message = "{application.arearental.responsiblePerson}")
  private ContactJson responsiblePerson;
  private String additionalInfo;
  private String trafficArrangements;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  /**
   * In Finnish: Työn suorittaja (yritys yleensä).
   * The company that does the actual work instead of the party that acts as customer.
   */
  public ApplicantJson getContractor() {
    return contractor;
  }

  public void setContractor(ApplicantJson contractor) {
    this.contractor = contractor;
  }

  /**
   * In Finnish: vastuuhenkilö
   * The person responsible of the actual work.
   */
  public ContactJson getResponsiblePerson() {
    return responsiblePerson;
  }

  public void setResponsiblePerson(ContactJson responsiblePerson) {
    this.responsiblePerson = responsiblePerson;
  }

  /**
   * In Finnish: lisätiedot.
   */
  public String getAdditionalInfo() {
    return additionalInfo;
  }

  public void setAdditionalInfo(String additionalInfo) {
    this.additionalInfo = additionalInfo;
  }

  /**
   * In Finnish: suoritettavat liikennejärjestelytyöt.
   */
  public String getTrafficArrangements() {
    return trafficArrangements;
  }

  public void setTrafficArrangements(String trafficArrangements) {
    this.trafficArrangements = trafficArrangements;
  }
}
