package fi.hel.allu.model.service.event;

/**
 * Event published when application owner is updated
 *
 */
public class ApplicationOwnerChangeEvent {

  private final Integer applicationId;
  private final Integer updaterId;
  private final Integer ownerId;

  public ApplicationOwnerChangeEvent(Integer applicationId, Integer updaterId, Integer ownerId) {
    this.applicationId = applicationId;
    this.updaterId = updaterId;
    this.ownerId = ownerId;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public Integer getUpdaterId() {
    return updaterId;
  }

  public Integer getOwnerId() {
    return ownerId;
  }
}
