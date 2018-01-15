package fi.hel.allu.servicecore.domain;

/**
 * Simple structure for fetching application's identifier
 */
public class ApplicationIdentifierJson {
  private int id;
  private String applicationId;

  public ApplicationIdentifierJson() {
  }

  public ApplicationIdentifierJson(int id, String applicationId) {
    this.id = id;
    this.applicationId = applicationId;
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
}
