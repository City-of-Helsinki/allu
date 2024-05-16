package fi.hel.allu.servicecore.event;

import fi.hel.allu.model.domain.NotificationConfiguration;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class ApplicationUpdateEventListener {

  private final ApplicationServiceComposer applicationServiceComposer;
  private final ConfigurationService configurationService;

  @Autowired
  public ApplicationUpdateEventListener(ApplicationServiceComposer applicationServiceComposer,
      ConfigurationService configurationService) {
    this.applicationServiceComposer = applicationServiceComposer;
    this.configurationService = configurationService;
  }

  @EventListener
  public void onApplicationUpdateEvent(ApplicationUpdateEvent event) {
    if (shouldNotify(event)) {
      notifyOwner(event);
    }
  }

  /**
   * Adds owner notification if update is done by some other user
   */
  private void notifyOwner(ApplicationUpdateEvent event) {
    Integer applicationOwner = getApplicationOwnerId(event.getApplicationId());
    if (applicationOwner != null && !applicationOwner.equals(event.getUpdaterId())) {
      applicationServiceComposer.addOwnerNotification(Collections.singletonList(event.getApplicationId()));
    }
  }

  @EventListener
  public void onApplicationOwnerChange(ApplicationOwnerChangeEvent event) {
    if (event.getOwnerId() != null && !Objects.equals(event.getOwnerId(), event.getUpdaterId())) {
      applicationServiceComposer.addOwnerNotification(event.getApplicationIds());
    } else {
      // Owner set to current user or new owner not given -> clear owner notification
      applicationServiceComposer.removeOwnerNotification(event.getApplicationIds());
    }
  }

  private Integer getApplicationOwnerId(Integer applicationId) {
    return applicationServiceComposer.getApplicationOwnerId(applicationId);
  }

  /**
   * Checks whether application update event is configured to be notified
   */
  private boolean shouldNotify(ApplicationUpdateEvent event) {
    List<NotificationConfiguration> notificationConfiguration = configurationService.getNotificationConfiguration();
    return notificationConfiguration.stream()
        .anyMatch(configuration -> configuration.matches(event.getType(), event.getApplicationStatus(), event.getSpecifier()));
  }



}