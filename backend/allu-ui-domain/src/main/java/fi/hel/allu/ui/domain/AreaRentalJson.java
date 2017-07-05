package fi.hel.allu.ui.domain;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.TrafficArrangementImpedimentType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Area rental (aluevuokraus) specific data.
 */
public class AreaRentalJson extends ApplicationExtensionJson {
  private Boolean pksCard;
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
