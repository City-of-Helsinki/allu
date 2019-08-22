package fi.hel.allu.model.service.event;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.service.ApplicationService;

@Service
public class ApplicationUpdateEventListener {

  private final ApplicationService applicationService;

  @Autowired
  public ApplicationUpdateEventListener(ApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  @EventListener
  public void onApplicationUpdate(ApplicationUpdateEvent event) {
    Integer applicationOwner = getApplicationOwnerId(event.getApplicationId());
    if (applicationOwner != null && !applicationOwner.equals(event.getUpdaterId())) {
      applicationService.addOwnerNotification(event.getApplicationId());
    }
  }

  @EventListener
  public void onApplicationOwnerChange(ApplicationOwnerChangeEvent event) {
    if (Objects.equals(event.getOwnerId(), event.getUpdaterId())) {
      // Owner set to current user, clear owner notification
      applicationService.removeOwnerNotification(event.getApplicationId());
    } else if (event.getOwnerId() != null) {
      applicationService.addOwnerNotification(event.getApplicationId());
    }
  }

  private Integer getApplicationOwnerId(Integer applicationId) {
    return applicationService.getApplicationOwner(applicationId);
  }

}
