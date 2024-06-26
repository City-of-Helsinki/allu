package fi.hel.allu.model.service.event.handler;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.SupervisionDates;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.*;

@Service
public class ExcavationAnnouncementStatusChangeHandler extends ApplicationStatusChangeHandler {

  private final WinterTimeService winterTimeService;
  private final InvoicingPeriodService invoicingPeriodService;
  private final SupervisionTaskService supervisionTaskService;

  public ExcavationAnnouncementStatusChangeHandler(ApplicationService applicationService,
       SupervisionTaskService supervisionTaskService, LocationService locationService,
       ApplicationDao applicationDao, ChargeBasisService chargeBasisService,
       HistoryDao historyDao, InformationRequestDao informationRequestDao,
       InvoiceService invoiceService, WinterTimeService winterTimeService,
       TerminationDao terminationDao, InvoicingPeriodService invoicingPeriodService) {
    super(applicationService, supervisionTaskService, locationService,
        applicationDao, chargeBasisService, historyDao, informationRequestDao,
        invoiceService, terminationDao);
    this.winterTimeService = winterTimeService;
    this.invoicingPeriodService = invoicingPeriodService;
    this.supervisionTaskService = supervisionTaskService;
  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);

    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    if (extension.getWinterTimeOperation() != null &&
        !hasOpenSupervisionTask(application, SupervisionTaskType.OPERATIONAL_CONDITION)) {
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
    removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  private void setExcavationAnnouncementInvoicable(Application application, ZonedDateTime invoicableTime) {
    getInvoiceService().lockInvoices(application.getId());
    getInvoiceService().setInvoicableTime(application.getId(), invoicableTime);
    finishInvoicing(application);
  }

  @Override
  protected void handleOperationalConditionStatus(Application application) {
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    ZonedDateTime invoicableTime = getInvoicableTimeForOperationalCondition(extension);

    Integer operationalConditionPeriodId = getOperationalConditionPeriodId(application.getId());
    if (operationalConditionPeriodId != null) {
      setPeriodInvoicable(application, invoicableTime, operationalConditionPeriodId);
    } else {
      setExcavationAnnouncementInvoicable(application, invoicableTime);
    }
    removeTag(application.getId(), ApplicationTagType.OPERATIONAL_CONDITION_REPORTED);
    removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
    clearTargetState(application);
    setOwner(application.getHandler(), application.getId());
    // If replacing application is moved directly to operational condition without
    // operational condition approval, there's open operational condition
    // supervision task which should be removed.
    removeOpenOperationalConditionTask(application.getId());
  }

  private Integer getOperationalConditionPeriodId(Integer id) {
    return invoicingPeriodService.findOpenPeriodsForApplicationId(id).stream()
        .filter(p -> p.getInvoicableStatus() == StatusType.OPERATIONAL_CONDITION)
        .findFirst()
        .map(InvoicingPeriod::getId)
        .orElse(null);
  }

  private void setPeriodInvoicable(Application application, ZonedDateTime invoicableTime, Integer periodId) {
    if (periodId != null) {
      getInvoiceService().lockInvoicesOfPeriod(periodId);
      getInvoiceService().setInvoicableTimeForPeriod(periodId, invoicableTime);
      finishInvoicingForPeriod(application, periodId);
      invoicingPeriodService.closeInvoicingPeriod(periodId);
    }
  }

  private ZonedDateTime getInvoicableTimeForOperationalCondition(ExcavationAnnouncement extension) {
    // If operational condition is before winter start it's invoiced on first day of winter
    AnnualTimePeriod winterTime = winterTimeService.getWinterTime();
    if (!winterTime.isInAnnualPeriod(extension.getWinterTimeOperation())) {
      return winterTime.getAnnualPeriodStart(extension.getWinterTimeOperation()).atStartOfDay(TimeUtil.HelsinkiZoneId);
    }
    return extension.getWinterTimeOperation();
  }

  @Override
  protected void handleFinishedStatus(Application application) {
    ExcavationAnnouncement extension = (ExcavationAnnouncement)application.getExtension();
    adjustOperationalConditionPricing(application.getId(), extension);
    setExcavationAnnouncementInvoicable(application, extension.getWorkFinished());
    clearTargetState(application);
    clearOwner(application);
    removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  // If operational condition date is after work finished and is not yet
  // invoiced, open charge items / invoicing and update area fees of operational condition phase
  private void adjustOperationalConditionPricing(Integer applicationId, ExcavationAnnouncement excavationAnnouncement) {
    // Fix invoicing if needed and invoices are not yet invoiced
    if (pricingUpdateNeeded(excavationAnnouncement) && !getInvoiceService().applicationHasInvoiced(applicationId)) {
      getChargeBasisService().unlockEntries(applicationId);
      getApplicationService().updateChargeBasis(applicationId);
      getInvoiceService().updateInvoiceRows(applicationId);
    }
  }

  // If operational condition is in summer, area fees are charged to winter time start
  // If work finished is afterwords set to date before winter time start, we need to adjust operational
  // condition area fees.
  private boolean pricingUpdateNeeded(ExcavationAnnouncement excavationAnnouncement) {
    ZonedDateTime operationalConditionDate = excavationAnnouncement.getWinterTimeOperation();
    if (operationalConditionDate != null) {
      ZonedDateTime workFinishedDate = excavationAnnouncement.getWorkFinished();
      AnnualTimePeriod winterTime = winterTimeService.getWinterTime();
      return !winterTime.isInAnnualPeriod(operationalConditionDate) &&
          winterTime.getAnnualPeriodStart(operationalConditionDate).isAfter(LocalDate.from(workFinishedDate));
    }
    return false;
  }

  private void removeOpenOperationalConditionTask(Integer applicationId) {
    supervisionTaskService.findByApplicationIdAndType(applicationId, SupervisionTaskType.OPERATIONAL_CONDITION)
      .stream().filter(t -> t.getStatus() == SupervisionTaskStatusType.OPEN)
      .forEach(t -> supervisionTaskService.delete(t.getId()));
  }

}
