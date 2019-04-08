package fi.hel.allu.model.service.event.handler;

import fi.hel.allu.model.dao.InformationRequestDao;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.model.service.*;

@Service
public class AreaRentalStatusChangeHandler extends ApplicationStatusChangeHandler {

  private final InvoiceService invoiceService;

  public AreaRentalStatusChangeHandler(ApplicationService applicationService,
       SupervisionTaskService supervisionTaskService, LocationService locationService,
       ApplicationDao applicationDao, ChargeBasisService chargeBasisService,
       HistoryDao historyDao, InformationRequestDao informationRequestDao,
       InvoiceService invoiceService) {
    super(applicationService, supervisionTaskService, locationService,
            applicationDao, chargeBasisService, historyDao, informationRequestDao);
    this.invoiceService = invoiceService;
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);

    application.getLocations().stream().forEach(l ->
      createSupervisionTask(application, SupervisionTaskType.WORK_TIME_SUPERVISION, userId,
                            TimeUtil.nextDay(l.getEndTime()), l.getId()));
    createSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION, userId,
                          TimeUtil.nextDay(application.getEndTime()));
    removeTag(application.getId(), ApplicationTagType.PRELIMINARY_SUPERVISION_DONE);
  }

  @Override
  protected void handleFinishedStatus(Application application) {
    AreaRental extension = (AreaRental)application.getExtension();
    invoiceService.lockInvoices(application.getId());
    invoiceService.setInvoicableTime(application.getId(), extension.getWorkFinished());
    lockChargeBasisEntries(application.getId());
    cancelOpenSupervisionTasks(application.getId());
    clearTargetState(application);
    clearOwner(application);
  }

}
