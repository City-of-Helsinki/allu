package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;

@Service
public class TrafficArrangementStatusChangeHandler extends ApplicationStatusChangeHandler {

  public TrafficArrangementStatusChangeHandler(ApplicationService applicationService,
      SupervisionTaskService supervisionTaskService, LocationService locationService,
      ApplicationDao applicationDao, ChargeBasisService chargeBasisService) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService);
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    cancelDanglingSupervisionTasks(application);
    clearTargetState(application);
    createSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION, userId, getNextDay(application.getEndTime()));
  }

  private ZonedDateTime getNextDay(ZonedDateTime endTime) {
    return endTime != null ? endTime.plusDays(1) : null;
  }




}
