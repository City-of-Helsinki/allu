package fi.hel.allu.servicecore.event;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ApplicationEventDispatcher {

  private ApplicationEventPublisher eventPublisher;
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;

  public ApplicationEventDispatcher(ApplicationEventPublisher eventPublisher,
                                    ApplicationProperties applicationProperties,
                                    RestTemplate restTemplate) {
    this.eventPublisher = eventPublisher;
    this.applicationProperties = applicationProperties;
    this.restTemplate = restTemplate;
  }

  public void dispatchUpdateEvent(int applicationId, Integer updaterId, ApplicationNotificationType type, StatusType applicationStatus, String specifier) {
    eventPublisher.publishEvent(new  ApplicationUpdateEvent(applicationId, updaterId, type, applicationStatus, specifier));
  }

  public void dispatchUpdateEvent(int applicationId, Integer updaterId, ApplicationNotificationType type, StatusType applicationStatus) {
    dispatchUpdateEvent(applicationId, updaterId, type, applicationStatus, null);
  }

  public void dispatchUpdateEvent(int applicationId, Integer updaterId, ApplicationNotificationType type, String specifier) {
    dispatchUpdateEvent(applicationId, updaterId, type, getApplicationStatus(applicationId), specifier);
  }

  public void dispatchUpdateEvent(int applicationId, Integer updaterId, ApplicationNotificationType type) {
    dispatchUpdateEvent(applicationId, updaterId, type, getApplicationStatus(applicationId), null);
  }

  public void dispatchOwnerChangeEvent(List<Integer> applicationIds, Integer updaterId, Integer ownerId) {
    eventPublisher.publishEvent(new ApplicationOwnerChangeEvent(applicationIds, updaterId, ownerId));
  }

  public void dispatchNotificationRemoval(List<Integer> applicationIds) {
    // Publish owner change event without new owner to clear notifications
    eventPublisher.publishEvent(new ApplicationOwnerChangeEvent(applicationIds, null, null));
  }

  private StatusType getApplicationStatus(int applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationStatusUrl(), ApplicationStatusInfo.class, applicationId).getStatus();
  }
}