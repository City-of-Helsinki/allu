package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.ExcavationAnnouncementDates;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.service.*;

@Service
public class ExcavationAnnouncementStatusChangeHandler extends ApplicationStatusChangeHandler {

  private final WinterTimeService winterTimeService;
  private final InvoiceService invoiceService;

  public ExcavationAnnouncementStatusChangeHandler(ApplicationService applicationService,
      SupervisionTaskService supervisionTaskService, LocationService locationService, ApplicationDao applicationDao,
      ChargeBasisService chargeBasisService, InvoiceService invoiceService, WinterTimeService winterTimeService) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService);
    this.invoiceService = invoiceService;
    this.winterTimeService = winterTimeService;
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    cancelDanglingSupervisionTasks(application);
    clearTargetState(application);

    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    if (extension.getWinterTimeOperation() != null &&
        !hasSupervisionTask(application, SupervisionTaskType.OPERATIONAL_CONDITION)) {
      createSupervisionTask(application, SupervisionTaskType.OPERATIONAL_CONDITION, userId,
          ExcavationAnnouncementDates.operationalConditionSupervisionDate(extension.getWinterTimeOperation()));
    }
    if (application.getEndTime() != null) {
      if (!hasSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION)) {
        createSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION, userId,
            ExcavationAnnouncementDates.finalSupervisionDate(application.getEndTime()));
      }
      if (!hasSupervisionTask(application, SupervisionTaskType.WARRANTY)) {
        createSupervisionTask(application, SupervisionTaskType.WARRANTY, userId,
            ExcavationAnnouncementDates.warrantySupervisionDate(application.getEndTime()));
      }
    }
  }

  protected void setExcavationAnnouncementInvoicable(Application application, ZonedDateTime invoicableTime) {
    invoiceService.lockInvoices(application.getId());
    invoiceService.setInvoicableTime(application.getId(), invoicableTime);
    lockChargeBasisEntries(application.getId());
  }

  @Override
  protected void handleOperationalConditionStatus(Application application) {
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    if (winterTimeService.isInWinterTime(extension.getWinterTimeOperation()))  {
      setExcavationAnnouncementInvoicable(application, extension.getWinterTimeOperation());
    }
  }

  @Override
  protected void handleFinishedStatus(Application application) {
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    setExcavationAnnouncementInvoicable(application, extension.getWorkFinished());
  }
}
