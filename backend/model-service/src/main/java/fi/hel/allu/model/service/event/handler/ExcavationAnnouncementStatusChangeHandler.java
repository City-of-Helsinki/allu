package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.SupervisionDates;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.WinterTime;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.service.*;

@Service
public class ExcavationAnnouncementStatusChangeHandler extends ApplicationStatusChangeHandler {

  private final WinterTimeService winterTimeService;
  private final InvoiceService invoiceService;

  public ExcavationAnnouncementStatusChangeHandler(ApplicationService applicationService,
      SupervisionTaskService supervisionTaskService, LocationService locationService, ApplicationDao applicationDao,
      ChargeBasisService chargeBasisService, HistoryDao historyDao, InvoiceService invoiceService,
      WinterTimeService winterTimeService) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService, historyDao);
    this.invoiceService = invoiceService;
    this.winterTimeService = winterTimeService;
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);

    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    if (extension.getWinterTimeOperation() != null &&
        !hasSupervisionTask(application, SupervisionTaskType.OPERATIONAL_CONDITION)) {
      createSupervisionTask(application, SupervisionTaskType.OPERATIONAL_CONDITION, userId,
          SupervisionDates.operationalConditionSupervisionDate(extension.getWinterTimeOperation()));
    }
    if (application.getEndTime() != null) {
      if (!hasSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION)) {
        createSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION, userId,
            SupervisionDates.finalSupervisionDate(application.getEndTime()));
      }
      if (!hasSupervisionTask(application, SupervisionTaskType.WARRANTY)) {
        createSupervisionTask(application, SupervisionTaskType.WARRANTY, userId,
            SupervisionDates.warrantySupervisionDate(application.getEndTime()));
      }
    }
    removeTag(application.getId(), ApplicationTagType.PRELIMINARY_SUPERVISION_DONE);
  }

  protected void setExcavationAnnouncementInvoicable(Application application, ZonedDateTime invoicableTime) {
    invoiceService.lockInvoices(application.getId());
    invoiceService.setInvoicableTime(application.getId(), invoicableTime);
    finishInvoicing(application);
  }

  @Override
  protected void handleOperationalConditionStatus(Application application) {
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    ZonedDateTime invoicableTime = getInvoicableTimeForOperationalCondition(extension);
    setExcavationAnnouncementInvoicable(application, invoicableTime);
    removeTag(application.getId(), ApplicationTagType.OPERATIONAL_CONDITION_REPORTED);
    clearTargetState(application);
  }

  private ZonedDateTime getInvoicableTimeForOperationalCondition(ExcavationAnnouncement extension) {
    // If operational condition is before winter start it's invoiced on first day of winter
    WinterTime winterTime = winterTimeService.getWinterTime();
    if (!winterTime.isInWinterTime(extension.getWinterTimeOperation())) {
      return winterTime.getWinterTimeStart(extension.getWinterTimeOperation()).atStartOfDay(TimeUtil.HelsinkiZoneId);
    }
    return extension.getWinterTimeOperation();
  }

  @Override
  protected void handleFinishedStatus(Application application) {
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    setExcavationAnnouncementInvoicable(application, extension.getWorkFinished());
    clearTargetState(application);
  }
}
