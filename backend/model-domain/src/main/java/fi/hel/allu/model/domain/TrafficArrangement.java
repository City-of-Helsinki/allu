package fi.hel.allu.model.domain;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.TrafficArrangementImpedimentType;

import java.time.ZonedDateTime;

/**
 * Traffic arrangement (väliaikainen liikennejärjestely) specific data.
 */
public class TrafficArrangement extends ApplicationExtension {
  private Customer contractor;
  private Contact responsiblePerson;
  private Boolean pksCard;
  private ZonedDateTime workFinished;
  private String additionalInfo;
  private String trafficArrangements;
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS;
  }

  /**
   * In Finnish: Työn suorittaja (yritys yleensä).
   * The company that does the actual work instead of the party that acts as customer.
   */
  public Customer getContractor() {
    return contractor;
  }

  public void setContractor(Customer contractor) {
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
   * In Finnish: PKS-kortti (pääkaupunkiseudun katutöihin liittyvät koulutus).
   * True, if the contractor doing the actual work has PKS-card.
   */
  public Boolean getPksCard() {
    return pksCard;
  }

  public void setPksCard(Boolean pksCard) {
    this.pksCard = pksCard;
  }

  /**
   * In Finnish: Työ valmis.
   */
  public ZonedDateTime getWorkFinished() {
    return workFinished;
  }

  public void setWorkFinished(ZonedDateTime workFinished) {
    this.workFinished = workFinished;
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

  /**
   * In Finnish: Liikennejärjestelyn haitta.
   */
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }
}
