package fi.hel.allu.ui.domain;

/**
 * DecisionJson is used to transfer all needed data to PDF service for decision
 * generation.
 */
public class DecisionJson {
  private ApplicationJson application;

  /**
   * @return the application
   */
  public ApplicationJson getApplication() {
    return application;
  }

  /**
   * @param application
   *          the application to set
   */
  public void setApplication(ApplicationJson application) {
    this.application = application;
  }

}
