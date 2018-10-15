package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.CableReport;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;

@Service
public class CableReportStatusChangeHandler extends ApplicationStatusChangeHandler {

  @Autowired
  public CableReportStatusChangeHandler(ApplicationService applicationService,
      SupervisionTaskService supervisionTaskService, LocationService locationService,
      ApplicationDao applicationDao, ChargeBasisService chargeBasisService) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService);
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    cancelDanglingSupervisionTasks(application);
    clearTargetState(application);
    // Validity time of cable report to decision time + one month
    ZonedDateTime validityTime = ZonedDateTime.now().plusMonths(1);
    CableReport cableReport = (CableReport)application.getExtension();
    cableReport.setValidityTime(validityTime);
    getApplicationService().update(application.getId(), application, userId);
  }


}
