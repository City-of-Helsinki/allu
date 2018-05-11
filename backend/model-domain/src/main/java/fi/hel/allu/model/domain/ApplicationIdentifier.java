package fi.hel.allu.model.domain;

/**
 * Simple structure for fetching application's identifier
 */
public class ApplicationIdentifier {
  private int id;
  private String applicationId;
  private String identificationNumber;

  public ApplicationIdentifier() {
  }

  public ApplicationIdentifier(int id, String applicationId, String identificationNumber) {
    this.id = id;
    this.applicationId = applicationId;
    this.identificationNumber = identificationNumber;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getApplicationId() {
    return applicationId;
  }

  public void setApplicationId(String applicationId) {
    this.applicationId = applicationId;
  }

  public String getIdentificationNumber() {
    return identificationNumber;
  }

  public void setIdentificationNumber(String identificationNumber) {
    this.identificationNumber = identificationNumber;
  }
}
