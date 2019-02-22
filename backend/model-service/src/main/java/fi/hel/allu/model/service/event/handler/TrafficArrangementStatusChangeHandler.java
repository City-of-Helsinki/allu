package fi.hel.allu.model.service.event.handler;

import fi.hel.allu.model.dao.InformationRequestDao;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;

@Service
public class TrafficArrangementStatusChangeHandler extends ApplicationStatusChangeHandler {

  public TrafficArrangementStatusChangeHandler(ApplicationService applicationService,
     SupervisionTaskService supervisionTaskService, LocationService locationService,
     ApplicationDao applicationDao, ChargeBasisService chargeBasisService, HistoryDao historyDao,
     InformationRequestDao informationRequestDao) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService, historyDao, informationRequestDao);
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);
    createSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION, userId, TimeUtil.nextDay(application.getEndTime()));
  }
}
