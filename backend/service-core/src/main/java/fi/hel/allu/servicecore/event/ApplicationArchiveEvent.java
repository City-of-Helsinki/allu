package fi.hel.allu.servicecore.event;

/**
 * Event raised when application is possibly ready for archive
 *
 */
public class ApplicationArchiveEvent {

  private final int applicationId;

  public ApplicationArchiveEvent(int applicationId) {
    this.applicationId = applicationId;
  }

  public int getApplicationId() {
    return applicationId;
  }

}
