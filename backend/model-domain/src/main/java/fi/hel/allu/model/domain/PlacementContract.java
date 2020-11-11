package fi.hel.allu.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.hel.allu.common.domain.types.ApplicationType;

/**
 * Placement contract (sijoitussopimus) specific data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacementContract extends ApplicationExtension {
  private String propertyIdentificationNumber;
  private String additionalInfo;
  private String contractText;
  private Integer sectionNumber;
  private String rationale;

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

  /**
   * In Finnish: Pykälänumero
   */
  public Integer getSectionNumber() {
    return sectionNumber;
  }

  public void setSectionNumber(Integer sectionNumber) {
    this.sectionNumber = sectionNumber;
  }

  public String getRationale() {
    return rationale;
  }

  public void setRationale(String rationale) {
    this.rationale = rationale;
  }
}
