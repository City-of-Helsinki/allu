package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;

/**
 * Area rental (Aluevuokraus) specific data.
 */
public class AreaRental extends ApplicationExtension {
  private Applicant contractor;
  private Contact responsiblePerson;
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
  public Applicant getContractor() {
    return contractor;
  }

  public void setContractor(Applicant contractor) {
    this.contractor = contractor;
  }

  /**
   * In Finnish: vastuuhenkilö
   * The person responsible of the actual work.
   */
  public Contact getResponsiblePerson() {
    return responsiblePerson;
  }

  public void setResponsiblePerson(Contact responsiblePerson) {
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
