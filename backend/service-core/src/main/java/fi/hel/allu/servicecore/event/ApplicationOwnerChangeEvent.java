package fi.hel.allu.servicecore.event;

import java.util.List;

/**
 * Event published when application owner is updated
 *
 */
public class ApplicationOwnerChangeEvent {

  private final List<Integer> applicationIds;
  private final Integer updaterId;
  private final Integer ownerId;

  public ApplicationOwnerChangeEvent(List<Integer> applicationIds, Integer updaterId, Integer ownerId) {
    this.applicationIds = applicationIds;
    this.updaterId = updaterId;
    this.ownerId = ownerId;
  }

  public List<Integer> getApplicationIds() {
    return applicationIds;
  }

  public Integer getUpdaterId() {
    return updaterId;
  }

  public Integer getOwnerId() {
    return ownerId;
  }
}