package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.CableReport;
import fi.hel.allu.model.service.*;

@Service
public class CableReportStatusChangeHandler extends ApplicationStatusChangeHandler {

  @Autowired
  public CableReportStatusChangeHandler(ApplicationService applicationService,
      SupervisionTaskService supervisionTaskService, LocationService locationService,
      ApplicationDao applicationDao, ChargeBasisService chargeBasisService,
      HistoryDao historyDao, InformationRequestDao informationRequestDao, InvoiceService invoiceService) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService, historyDao,
        informationRequestDao, invoiceService);
  }

  @Override
  protected void handleHandlingStatus(Application application) {
    getApplicationService().setTargetState(application.getId(), StatusType.DECISION);
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);
    // Validity time of cable report to decision time + one month
    ZonedDateTime validityTime = ZonedDateTime.now().plusMonths(1);
    CableReport cableReport = (CableReport)application.getExtension();
    cableReport.setValidityTime(validityTime);
    getApplicationService().update(application.getId(), application, userId);
    setOwner(userId, application.getId());
    setHandler(userId, application.getId());
  }
}
