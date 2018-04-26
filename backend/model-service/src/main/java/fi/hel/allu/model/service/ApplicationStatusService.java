package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;

/**
 * Service class for application status specific operations
 */
@Service
public class ApplicationStatusService {

  private ApplicationService applicationService;
  private LocationService locationService;
  private ApplicationDao applicationDao;

  private static int PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET = 3;

  @Autowired
  public ApplicationStatusService(ApplicationService applicationService, LocationService locationService, ApplicationDao applicationDao) {
    this.applicationService = applicationService;
    this.locationService = locationService;
    this.applicationDao = applicationDao;
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
    return updateApplicationByNewStatus(application, userId);
  }

  private Application updateApplicationByNewStatus(Application application, Integer userId) {
    if (application.getStatus() == StatusType.DECISION) {
      return updateDecisionApplication(application, userId);
    } else {
      return application;
    }
  }

  private Application updateDecisionApplication(Application application, int userId) {
    if (application.getType() == ApplicationType.PLACEMENT_CONTRACT) {
      Location location = locationService.findSingleByApplicationId(application.getId());
      ZonedDateTime newEndTime = application.getDecisionTime().plusYears(PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET);
      location.setEndTime(newEndTime);
      locationService.updateApplicationLocations(application.getId(), Collections.singletonList(location), userId);
      return applicationService.findById(application.getId());
    } else {
        return application;
    }
  }

  public StatusType getApplicationStatus(int id) {
    return applicationDao.getStatus(id);
  }
}
