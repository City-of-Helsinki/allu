package fi.hel.allu.model.domain;

/**
 * Simple structure for fetching application's identifier
 */
public class ApplicationIdentifier {
  private int id;
  private String applicationId;

  public ApplicationIdentifier() {
  }

  public ApplicationIdentifier(int id, String applicationId) {
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
