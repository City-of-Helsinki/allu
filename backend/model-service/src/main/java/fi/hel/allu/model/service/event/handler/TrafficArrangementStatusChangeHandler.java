package fi.hel.allu.model.service.event.handler;

import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.service.*;

@Service
public class TrafficArrangementStatusChangeHandler extends ApplicationStatusChangeHandler {

  public TrafficArrangementStatusChangeHandler(ApplicationService applicationService,
                                               SupervisionTaskService supervisionTaskService, LocationService locationService,
                                               ApplicationDao applicationDao, ChargeBasisService chargeBasisService, HistoryDao historyDao,
                                               InformationRequestDao informationRequestDao, InvoiceService invoiceService,
                                               TerminationDao terminationDao) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService, historyDao,
        informationRequestDao, invoiceService, terminationDao);
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);
    createSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION, userId, TimeUtil.nextDay(application.getEndTime()));
  }
}
