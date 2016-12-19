package fi.hel.allu.ui.domain;

import fi.hel.allu.common.types.ApplicationType;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Excavation announcement (Kaivuilmoitus) specific data.
 */
public class ExcavationAnnouncementJson extends ApplicationExtensionJson {
  @NotNull(message = "{application.excavationAnnouncement.contractor}")
  private ApplicantJson contractor;
  @NotNull(message = "{application.excavationAnnouncement.responsiblePerson}")
  private ContactJson responsiblePerson;
  private ZonedDateTime winterTimeOperation;
  private ZonedDateTime summerTimeOperation;
  private ZonedDateTime workFinished;
  private ZonedDateTime guaranteeEndTime;
  private Integer cableReportId;
  private String additionalInfo;
  private String trafficArrangements;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.EXCAVATION_ANNOUNCEMENT;
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
   * In Finnish: Talvityön toiminnallinen kunto (päivämäärä, jolloin valmis).
   */
  public ZonedDateTime getWinterTimeOperation() {
    return winterTimeOperation;
  }

  public void setWinterTimeOperation(ZonedDateTime winterTimeOperation) {
    this.winterTimeOperation = winterTimeOperation;
  }

  /**
   * In Finnish: Kesätyön toiminnallinen kunto (päivämäärä, jolloin valmis).
   */
  public ZonedDateTime getSummerTimeOperation() {
    return summerTimeOperation;
  }

  public void setSummerTimeOperation(ZonedDateTime summerTimeOperation) {
    this.summerTimeOperation = summerTimeOperation;
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
   * In Finnish: Takuun päättymispäivämäärä.
   */
  public ZonedDateTime getGuaranteeEndTime() {
    return guaranteeEndTime;
  }

  public void setGuaranteeEndTime(ZonedDateTime guaranteeEndTime) {
    this.guaranteeEndTime = guaranteeEndTime;
  }

  /**
   * In Finnish: johtoselvitys kaivuilmoitukselle.
   */
  public Integer getCableReportId() {
    return cableReportId;
  }

  public void setCableReportId(Integer cableReportId) {
    this.cableReportId = cableReportId;
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
