package fi.hel.allu.model.service.event;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PlacementContract;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;


@Service
public class ApplicationStatusChangeListener {

  private static final int PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET = 1;
  private static final Logger logger = LoggerFactory.getLogger(ApplicationStatusChangeListener.class);

  private final DecisionDao decisionDao;
  private final ApplicationService applicationService;
  private final LocationService locationService;
  private final SupervisionTaskService supervisionTaskService;


  @Autowired
  public ApplicationStatusChangeListener(DecisionDao decisionDao, ApplicationService applicationService,
      LocationService locationService, SupervisionTaskService supervisionTaskService) {
    this.decisionDao = decisionDao;
    this.applicationService = applicationService;
    this.locationService = locationService;
    this.supervisionTaskService = supervisionTaskService;
  }

  @EventListener
  public void onApplicationStatusChange(ApplicationStatusChangeEvent event) {
    if (event.getNewStatus() == StatusType.DECISION) {
      handleDecisionStatus(event.getApplication(), event.getUserId());
    }
  }

  private void handleDecisionStatus(Application application, Integer userId) {
    cancelDanglingSupervisionTasks(application);
    switch (application.getType()) {
    case PLACEMENT_CONTRACT:
      logger.debug("Process placement contract status change to decision");
      handlePlacementContractDecision(application, userId);
      break;
    case TEMPORARY_TRAFFIC_ARRANGEMENTS:
      logger.debug("Process temporary traffic arrangement status change to decision");
      handleTemporaryTrafficArrangementDecision(application, userId);
      break;
    default:
      // No actions for other application types
    }
  }

  /**
   * Cancels open supervision tasks from applications replaced by
   * this application.
   */
  private void cancelDanglingSupervisionTasks(Application application) {
    if (application.getReplacesApplicationId() != null) {
      supervisionTaskService.cancelOpenTasksOfApplication(application.getReplacesApplicationId());
    }

  }

  private void handlePlacementContractDecision(Application application, Integer userId) {
    PlacementContract pc = (PlacementContract)application.getExtension();
    pc.setSectionNumber(decisionDao.getPlacementContractSectionNumber());
    applicationService.update(application.getId(), application, userId);
    Location location = locationService.findSingleByApplicationId(application.getId());
    final ZonedDateTime startTime = TimeUtil.startOfDay(TimeUtil.homeTime(ZonedDateTime.now()));
    location.setStartTime(startTime);
    final ZonedDateTime endTime = TimeUtil.endOfDay(startTime.plusYears(PLACEMENT_CONTRACT_END_DATE_YEAR_OFFSET));
    location.setEndTime(endTime);
    locationService.updateApplicationLocations(application.getId(), Collections.singletonList(location), userId);

  }

  private void handleTemporaryTrafficArrangementDecision(Application application, Integer userId) {
    // Create supervision task
    SupervisionTask supervisionTask = new SupervisionTask(null,
        application.getId(), SupervisionTaskType.FINAL_SUPERVISION, userId, getSupervisionTaskOwner(application), null,
        getNextDay(application.getEndTime()), null, SupervisionTaskStatusType.OPEN, null, null);
    supervisionTaskService.insert(supervisionTask);
  }

  private ZonedDateTime getNextDay(ZonedDateTime endTime) {
    return endTime != null ? endTime.plusDays(1) : null;
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



}
