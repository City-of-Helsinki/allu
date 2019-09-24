package fi.hel.allu.servicecore.event;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.servicecore.service.ApplicationService;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;

@Service
public class ApplicationUpdateEventListener {

  private final ApplicationServiceComposer applicationServiceComposer;

  @Autowired
  public ApplicationUpdateEventListener(ApplicationServiceComposer applicationServiceComposer) {
    this.applicationServiceComposer = applicationServiceComposer;
  }

  @EventListener
  public void onApplicationUpdate(ApplicationUpdateEvent event) {
    Integer applicationOwner = getApplicationOwnerId(event.getApplicationId());
    if (applicationOwner != null && !applicationOwner.equals(event.getUpdaterId())) {
      applicationServiceComposer.addOwnerNotification(event.getApplicationId());
    }
  }

  @EventListener
  public void onApplicationOwnerChange(ApplicationOwnerChangeEvent event) {
    if (Objects.equals(event.getOwnerId(), event.getUpdaterId())) {
      // Owner set to current user, clear owner notification
      applicationServiceComposer.removeOwnerNotification(event.getApplicationId());
    } else if (event.getOwnerId() != null) {
      applicationServiceComposer.addOwnerNotification(event.getApplicationId());
    }
  }

  private Integer getApplicationOwnerId(Integer applicationId) {
    return applicationServiceComposer.getApplicationOwnerId(applicationId);
  }

}
