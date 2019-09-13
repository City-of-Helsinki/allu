package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;
import java.util.Collections;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.TerminationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.service.*;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static fi.hel.allu.QApplication.application;

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
  private final HistoryDao historyDao;
  private final InformationRequestDao informationRequestDao;
  private final InvoiceService invoiceService;
  private final TerminationDao terminationDao;

  @Autowired
  public ApplicationStatusChangeHandler(ApplicationService applicationService,
                                        SupervisionTaskService supervisionTaskService, LocationService locationService,
                                        ApplicationDao applicationDao, ChargeBasisService chargeBasisService,
                                        HistoryDao historyDao, InformationRequestDao informationRequestDao,
                                        InvoiceService invoiceService, TerminationDao terminationDao) {
    this.applicationService = applicationService;
    this.supervisionTaskService = supervisionTaskService;
    this.locationService = locationService;
    this.applicationDao = applicationDao;
    this.chargeBasisService = chargeBasisService;
    this.historyDao = historyDao;
    this.informationRequestDao = informationRequestDao;
    this.invoiceService = invoiceService;
    this.terminationDao = terminationDao;
  }

  public void handleStatusChange(ApplicationStatusChangeEvent statusChangeEvent) {
    switch (statusChangeEvent.getNewStatus()) {
      case HANDLING:
        handleHandlingStatus(statusChangeEvent.getApplication());
        break;
      case DECISIONMAKING:
        handleDecisionMakingStatus(statusChangeEvent.getApplication(), statusChangeEvent.getUserId());
        break;
      case DECISION:
        handleDecisionStatus(statusChangeEvent.getApplication(), statusChangeEvent.getUserId());
        break;
      case OPERATIONAL_CONDITION:
        handleOperationalConditionStatus(statusChangeEvent.getApplication());
        break;
      case TERMINATED:
        handleTerminatedStatus(statusChangeEvent.getApplication(), statusChangeEvent.getUserId());
        break;
      case FINISHED:
        handleFinishedStatus(statusChangeEvent.getApplication());
        break;
      case CANCELLED:
        handleCancelledStatus(statusChangeEvent.getApplication());
        break;
      case ARCHIVED:
        handleArchivedStatus(statusChangeEvent.getApplication(), statusChangeEvent.getUserId());
        break;
      default:
        // By default nothing
        break;
    }

  }

  protected void handleHandlingStatus(Application application) {
  }

  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    // Clear target state on decision
    clearTargetState(application);
    finishInvoicing(application);
    removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  protected void clearTargetState(Application application) {
    applicationService.setTargetState(application.getId(), null);
  }

  protected void setOwner(Integer ownerId, Integer applicationId) {
    if (ownerId != null) {
      applicationDao.updateOwner(ownerId, Collections.singletonList(applicationId));
    }
  }

  protected void clearOwner(Application application) {
    applicationDao.removeOwner(Collections.singletonList(application.getId()));
  }

  protected void setHandler(Integer handlerId, Integer applicationId) {
    if (handlerId != null) {
      applicationDao.updateHandler(applicationId, handlerId);
    }
  }

  protected void finishInvoicing(Application application) {
    applicationDao.setInvoicingChanged(application.getId(), false);
    lockChargeBasisEntries(application.getId());
  }

  protected void handleFinishedStatus(Application application) {
    clearTargetState(application);
    clearOwner(application);
    removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  protected void handleOperationalConditionStatus(Application application) {
  }

  protected void handleTerminatedStatus(Application application, Integer userId) {
    updateTerminationDecisionInfo(application, userId);
    clearTargetState(application);
    clearOwner(application);
    createSupervisionTaskForTerminated(application, userId);
  }

  protected void updateTerminationDecisionInfo(Application application, Integer userId) {
    terminationDao.updateTerminationDecisionInfo(application.getId(), userId, ZonedDateTime.now());
  }

  protected void handleDecisionMakingStatus(Application application, Integer userId) {
    StatusType targetState = application.getTargetState();
    if (targetState == null) {
      targetState = getTargetStateForDecisionMaking(application.getId());
      applicationService.setTargetState(application.getId(), targetState);
    }
    if (targetState == StatusType.DECISION) {
      // Handler set only when making first "decision"
      applicationService.updateHandler(application.getId(), userId);
    }
    if (targetState == StatusType.TERMINATED) {
      terminationDao.updateTerminationHandler(application.getId(), userId);
    }
  }



  protected void handleCancelledStatus(Application application) {
    supervisionTaskService.cancelOpenTasksOfApplication(application.getId());
    informationRequestDao.closeInformationRequestOf(application.getId());
    invoiceService.deleteUninvoicedInvoices(application.getId());
    if (application.getReplacesApplicationId() != null) {
      // If replacing application cancelled, clear replacing application ID from replaced application
      applicationDao.setApplicationReplaced(application.getReplacesApplicationId(), null);
    }
  }

  protected void handleArchivedStatus(Application application, Integer userId) {
  }

  protected void handleReplacedApplicationOnDecision(Application application, Integer userId) {
    if (application.getReplacesApplicationId() != null) {
      changeReplacedApplicationStatus(application.getReplacesApplicationId(), userId);
      cancelDanglingSupervisionTasks(application.getReplacesApplicationId());
    }
  }

  protected void createSupervisionTaskForTerminated(Application application, Integer userId) {
    TerminationInfo info = getTermination(application.getId());
    if (info != null) {
      ZonedDateTime terminationSupervision = TimeUtil.nextDay(info.getExpirationTime());
      createSupervisionTask(application, SupervisionTaskType.TERMINATION, userId, terminationSupervision);
    }
  }

  protected TerminationInfo getTermination(Integer applicationId) {
    return terminationDao.getTerminationInfo(applicationId);
  }

  /**
   * Cancels open supervision tasks from applications replaced by
   * this application.
   */
  private void cancelDanglingSupervisionTasks(Integer replacedApplicationId) {
    supervisionTaskService.cancelOpenTasksOfApplication(replacedApplicationId);
  }

  private void changeReplacedApplicationStatus(Integer replacedApplicationId, Integer userId) {
    applicationDao.updateStatus(replacedApplicationId, StatusType.REPLACED);
    ChangeHistoryItem change = new ChangeHistoryItem();
    change.setChangeType(ChangeType.STATUS_CHANGED);
    change.setChangeSpecifier(StatusType.REPLACED.name());
    change.setChangeTime(ZonedDateTime.now());
    change.setUserId(userId);
    historyDao.addApplicationChange(replacedApplicationId, change);
  }

  private StatusType getTargetStateForDecisionMaking(Integer applicationId) {
    if (terminationDao.isMarkedForTermination(applicationId)) {
      return StatusType.TERMINATED;
    } else {
      // By default, application is moved to decision state when decision is made.
      return StatusType.DECISION;
    }
  }

  protected void lockChargeBasisEntries(Integer applicationId) {
    chargeBasisService.lockEntries(applicationId);
  }

  protected void createSupervisionTask(Application application, SupervisionTaskType type, Integer userId, ZonedDateTime plannedTime) {
    createSupervisionTask(application, type, userId, plannedTime, null);
  }

  protected void createSupervisionTask(Application application, SupervisionTaskType type, Integer userId,
                                       ZonedDateTime plannedTime, Integer locationId) {
    SupervisionTask supervisionTask = new SupervisionTask(null,
            application.getId(), type, userId, getSupervisionTaskOwner(application), null,
            plannedTime, null, SupervisionTaskStatusType.OPEN, null, null, locationId);
    supervisionTaskService.insert(supervisionTask);
  }

  protected boolean hasSupervisionTask(Application application, SupervisionTaskType type) {
    return !supervisionTaskService.findByApplicationIdAndType(application.getId(), type).isEmpty();
  }

  protected void cancelOpenSupervisionTasks(Integer applicationId) {
    supervisionTaskService.cancelOpenTasksOfApplication(applicationId);
  }

  private Integer getSupervisionTaskOwner(Application application) {
    Integer cityDistrict = application.getLocations().get(0).getEffectiveCityDistrictId();
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

  protected void removeTag(Integer id, ApplicationTagType tagType) {
    applicationService.removeTag(id, tagType);
  }

  protected ApplicationService getApplicationService() {
    return applicationService;
  }

  protected LocationService getLocationService() {
    return locationService;
  }

  protected InvoiceService getInvoiceService() {
    return invoiceService;
  }

  protected ChargeBasisService getChargeBasisService() {
    return chargeBasisService;
  }

}
