package fi.hel.allu.model.service.event;

import org.springframework.context.ApplicationEvent;

public class InvoicingChangeEvent extends ApplicationEvent {

  private final Integer applicationId;

  public InvoicingChangeEvent(Object source, Integer applicationId) {
    super(source);
    this.applicationId = applicationId;
  }

  public Integer getApplicationId() {
    return applicationId;
  }

}
