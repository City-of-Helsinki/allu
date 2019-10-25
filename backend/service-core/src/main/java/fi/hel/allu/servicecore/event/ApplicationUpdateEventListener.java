package fi.hel.allu.servicecore.event;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.domain.NotificationConfiguration;
import fi.hel.allu.servicecore.service.ApplicationServiceComposer;
import fi.hel.allu.servicecore.service.ConfigurationService;

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

  /**
   * Checks whether application update event is configured to be notified
   */
  private boolean shouldNotify(ApplicationUpdateEvent event) {
    List<NotificationConfiguration> notificationConfiguration = configurationService.getNotificationConfiguration();
    return notificationConfiguration.stream()
        .anyMatch(configuration -> configuration.matches(event.getType(), event.getApplicationStatus(), event.getSpecifier()));
  }



}
