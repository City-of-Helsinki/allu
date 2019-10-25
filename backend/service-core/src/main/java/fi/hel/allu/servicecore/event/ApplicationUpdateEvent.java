package fi.hel.allu.servicecore.event;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.ApplicationNotificationType;

/**
 * Event published when application or data related to application (comments,
 * supervision tasks, attachemnts etc) are updated
 *
 */
public class ApplicationUpdateEvent {

  private final Integer applicationId;
  private final Integer updaterId;
  private final ApplicationNotificationType type;
  private final StatusType applicationStatus;
  private final String specifier;

  public ApplicationUpdateEvent(Integer applicationId,
                                Integer updaterId,
                                ApplicationNotificationType type,
                                StatusType applicationStatus,
                                String specifier) {
    this.applicationId = applicationId;
    this.updaterId = updaterId;
    this.type = type;
    this.applicationStatus = applicationStatus;
    this.specifier = specifier;
  }

  public ApplicationNotificationType getType() {
    return type;
  }

  public StatusType getApplicationStatus() {
    return applicationStatus;
  }

  public String getSpecifier() {
    return specifier;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

  public Integer getUpdaterId() {
    return updaterId;
  }

}
