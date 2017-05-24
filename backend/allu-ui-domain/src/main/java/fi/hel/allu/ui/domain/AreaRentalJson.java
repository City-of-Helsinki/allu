package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.TrafficArrangementImpedimentType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Area rental (aluevuokraus) specific data.
 */
public class AreaRentalJson extends ApplicationExtensionJson {
  @NotNull(message = "{application.arearental.contractor}")
  private CustomerJson contractor;
  @NotNull(message = "{application.arearental.responsiblePerson}")
  private ContactJson responsiblePerson;
  private String additionalInfo;
  private String trafficArrangements;
  private ZonedDateTime workFinished;
  @NotNull(message = "{application.arearental.trafficArrangementImpedimentType}")
  private TrafficArrangementImpedimentType trafficArrangementImpedimentType;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.AREA_RENTAL;
  }

  /**
   * In Finnish: Työn suorittaja (yritys yleensä).
   * The company that does the actual work instead of the party that acts as customer.
   */
  public CustomerJson getContractor() {
    return contractor;
  }

  public void setContractor(CustomerJson contractor) {
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
   * In Finnish: Liikennejärjestelyn haitta.
   */
  public TrafficArrangementImpedimentType getTrafficArrangementImpedimentType() {
    return trafficArrangementImpedimentType;
  }

  public void setTrafficArrangementImpedimentType(TrafficArrangementImpedimentType trafficArrangementImpedimentType) {
    this.trafficArrangementImpedimentType = trafficArrangementImpedimentType;
  }
}
