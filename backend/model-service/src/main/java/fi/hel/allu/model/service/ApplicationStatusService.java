package fi.hel.allu.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.ApplicationStatusInfo;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

/**
 * Service class for application status specific operations
 */
@Service
public class ApplicationStatusService {

  private final ApplicationService applicationService;
  private final ApplicationDao applicationDao;
  private ApplicationEventPublisher statusChangeEventPublisher;

  @Autowired
  public ApplicationStatusService(ApplicationService applicationService,
      ApplicationDao applicationDao, ApplicationEventPublisher statusChangeEventPublisher) {
    this.applicationService = applicationService;
    this.applicationDao = applicationDao;
    this.statusChangeEventPublisher = statusChangeEventPublisher;
  }

  /**
   * Change application status.
   *
   * @param applicationId   Id of the application to be changed.
   * @param statusType      New status
   * @param userId          User making the status change. May be <code>null</code>, but required for decision making.
   * @return  Updated application.
   */
  @Transactional
  public Application changeApplicationStatus(int applicationId, StatusType statusType, Integer userId) {
    Application application = applicationService.changeApplicationStatus(applicationId, statusType, userId);
    statusChangeEventPublisher.publishEvent(new ApplicationStatusChangeEvent(this, application, statusType, userId));
    return applicationService.findById(applicationId);
  }

  public ApplicationStatusInfo getApplicationStatus(int id) {
    return applicationDao.getStatusWithIdentifier(id);
  }

  public Application returnToStatus(int applicationId, StatusType status) {
    return applicationDao.updateStatus(applicationId, status);
  }

}
