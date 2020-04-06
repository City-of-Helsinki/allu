package fi.hel.allu.servicecore.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.service.ApplicationService;

@Service
public class ApplicationEventDispatcher {

  @Autowired
  private ApplicationEventPublisher eventPublisher;

  @Autowired
  private ApplicationProperties applicationProperties;

  @Autowired
  private RestTemplate restTemplate;

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

  public void dispatchOwnerChangeEvent(int applicationId, Integer updaterId, Integer ownerId) {
    eventPublisher.publishEvent(new ApplicationOwnerChangeEvent(applicationId, updaterId, ownerId));
  }

  public void dispatchNotificationRemoval(Integer applicationId) {
    // Publish owner change event without new owner to clear notifications
    eventPublisher.publishEvent(new ApplicationOwnerChangeEvent(applicationId, null, null));
  }

  private StatusType getApplicationStatus(int applicationId) {
    return restTemplate.getForObject(applicationProperties.getApplicationStatusUrl(), ApplicationStatusInfo.class, applicationId).getStatus();
  }
}
