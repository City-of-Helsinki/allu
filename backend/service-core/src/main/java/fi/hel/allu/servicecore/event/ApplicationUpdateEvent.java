package fi.hel.allu.servicecore.event;

/**
 * Event published when application or data related to application (comments,
 * supervision tasks, attachemnts etc) are updated
 *
 */
public class ApplicationUpdateEvent {

  private final Integer applicationId;
  private final Integer updaterId;

  public ApplicationUpdateEvent(Integer applicationId, Integer updaterId) {
    this.applicationId = applicationId;
    this.updaterId = updaterId;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public Integer getUpdaterId() {
    return updaterId;
  }

}
