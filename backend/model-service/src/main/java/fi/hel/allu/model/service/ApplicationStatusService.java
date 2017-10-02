package fi.hel.allu.model.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Collections;

/**
 * Service class for application status specific operations
 */
@Service
public class ApplicationStatusService {

  private ApplicationService applicationService;
  private LocationService locationService;

  private static int PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET = 3;

  @Autowired
  public ApplicationStatusService(ApplicationService applicationService, LocationService locationService) {
    this.applicationService = applicationService;
    this.locationService = locationService;
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
    return updateApplicationByNewStatus(application);
  }

  private Application updateApplicationByNewStatus(Application application) {
    if (application.getStatus() == StatusType.DECISION) {
      return updateDecisionApplication(application);
    } else {
      return application;
    }
  }

  private Application updateDecisionApplication(Application application) {
    if (application.getType() == ApplicationType.PLACEMENT_CONTRACT) {
      Location location = locationService.findSingleByApplicationId(application.getId());
      ZonedDateTime newEndTime = application.getDecisionTime().plusYears(PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET);
      location.setEndTime(newEndTime);
      locationService.updateApplicationLocations(application.getId(), Collections.singletonList(location));
      return applicationService.findById(application.getId());
    } else {
        return application;
    }
  }
}
