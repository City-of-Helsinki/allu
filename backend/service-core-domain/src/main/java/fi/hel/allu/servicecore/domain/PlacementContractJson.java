package fi.hel.allu.servicecore.domain;

import fi.hel.allu.common.domain.types.ApplicationType;

import java.time.ZonedDateTime;

/**
 * Placement contract (sijoitussopimus) specific data.
 */
public class PlacementContractJson extends ApplicationExtensionJson {
  private String propertyIdentificationNumber;
  private String additionalInfo;
  private String contractText;
  private ZonedDateTime terminationDate;
  private Integer sectionNumber;

  @Override
  public ApplicationType getApplicationType() {
    return ApplicationType.PLACEMENT_CONTRACT;
  }

  /**
   * In Finnish: kiinteistötunnus
   */
  public String getPropertyIdentificationNumber() {
    return propertyIdentificationNumber;
  }

  public void setPropertyIdentificationNumber(String propertyIdentificationNumber) {
    this.propertyIdentificationNumber = propertyIdentificationNumber;
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
   * In Finnish: sopimusteksti
   * @return General terms related to the application.
   */
  public String getContractText() {
    return contractText;
  }

  public void setContractText(String contractText) {
    this.contractText = contractText;
  }

  public ZonedDateTime getTerminationDate() {
    return terminationDate;
  }

  public void setTerminationDate(ZonedDateTime terminationDate) {
    this.terminationDate = terminationDate;
  }

  public Integer getSectionNumber() {
    return sectionNumber;
  }

  public void setSectionNumber(Integer sectionNumber) {
    this.sectionNumber = sectionNumber;
  }
}
