package fi.hel.allu.model.service.event;

import org.springframework.context.ApplicationEvent;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;

public class ApplicationStatusChangeEvent extends ApplicationEvent {

  private final Application application;
  private final StatusType newStatus;
  private final Integer userId;

  public ApplicationStatusChangeEvent(Object source, Application application, StatusType newStatus, Integer userId) {
    super(source);
    this.application = application;
    this.newStatus = newStatus;
    this.userId = userId;
  }

  public StatusType getNewStatus() {
    return newStatus;
  }

  public Application getApplication() {
    return application;
  }

  public Integer getUserId() {
    return userId;
  }
}
