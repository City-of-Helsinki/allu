package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

/**
 * Default handler for application status change events
 */
@Service
public class ApplicationStatusChangeHandler {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusChangeHandler.class);

  private final ApplicationService applicationService;
  private final SupervisionTaskService supervisionTaskService;
  private final LocationService locationService;
  private final ApplicationDao applicationDao;
  private final ChargeBasisService chargeBasisService;

  @Autowired
  public ApplicationStatusChangeHandler(ApplicationService applicationService,
      SupervisionTaskService supervisionTaskService, LocationService locationService,
      ApplicationDao applicationDao, ChargeBasisService chargeBasisService) {
    this.applicationService = applicationService;
    this.supervisionTaskService = supervisionTaskService;
    this.locationService = locationService;
    this.applicationDao = applicationDao;
    this.chargeBasisService = chargeBasisService;
  }

  public void handleStatusChange(ApplicationStatusChangeEvent statusChangeEvent) {
    switch (statusChangeEvent.getNewStatus()) {
    case DECISIONMAKING:
      handleDecisionMakingStatus(statusChangeEvent.getApplication());
      break;
    case DECISION:
      handleDecisionStatus(statusChangeEvent.getApplication(), statusChangeEvent.getUserId());
      break;
    case OPERATIONAL_CONDITION:
      handleOperationalConditionStatus(statusChangeEvent.getApplication());
      break;
    case FINISHED:
      handleFinishedStatus(statusChangeEvent.getApplication());
      break;
    case CANCELLED:
      handleCancelledStatus(statusChangeEvent.getApplication());
      break;
    default:
      // By default nothing
        break;
    }

  }

  protected void handleDecisionStatus(Application application, Integer userId) {
    cancelDanglingSupervisionTasks(application);
    // Clear target state on decision
    clearTargetState(application);
    finishInvoicing(application);
  }

  protected void clearTargetState(Application application) {
    applicationService.setTargetState(application.getId(), null);
  }

  protected void finishInvoicing(Application application) {
    applicationDao.setInvoicingChanged(application.getId(), false);
    lockChargeBasisEntries(application.getId());
  }

  protected void handleFinishedStatus(Application application) {
  }

  protected void handleOperationalConditionStatus(Application application) {
  }

  protected void handleDecisionMakingStatus(Application application) {
    if (application.getTargetState() == null) {
      // By default, application is moved to decision state when decision is made.
      applicationService.setTargetState(application.getId(), StatusType.DECISION);
    }
  }

  protected void handleCancelledStatus(Application application) {
    supervisionTaskService.cancelOpenTasksOfApplication(application.getId());
  }

  /**
   * Cancels open supervision tasks from applications replaced by
   * this application.
   */
  protected void cancelDanglingSupervisionTasks(Application application) {
    if (application.getReplacesApplicationId() != null) {
      supervisionTaskService.cancelOpenTasksOfApplication(application.getReplacesApplicationId());
    }
  }

  protected void lockChargeBasisEntries(Integer applicationId) {
    chargeBasisService.lockEntries(applicationId);

  }

  protected void createSupervisionTask(Application application, SupervisionTaskType type, Integer userId, ZonedDateTime plannedTime) {
    SupervisionTask supervisionTask = new SupervisionTask(null,
        application.getId(), type, userId, getSupervisionTaskOwner(application), null,
        plannedTime, null, SupervisionTaskStatusType.OPEN, null, null);
    supervisionTaskService.insert(supervisionTask);
  }

  private Integer getSupervisionTaskOwner(Application application) {
    Integer cityDistrict = application.getLocations().get(0).getCityDistrictId();
    Integer supervisionTaskOwner = null;
    if (cityDistrict != null) {
      supervisionTaskOwner = locationService.findSupervisionTaskOwner(application.getType(), cityDistrict).map(u -> u.getId())
        .orElse(null);
    }
    if (supervisionTaskOwner == null) {
      logger.warn("No final supervision task owner found for application {}", application.getId());
    }
    return supervisionTaskOwner;
  }

  protected ApplicationService getApplicationService() {
    return applicationService;
  }

  protected LocationService getLocationService() {
    return locationService;
  }

}
