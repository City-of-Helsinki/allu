package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.model.service.*;

@Service
public class AreaRentalStatusChangeHandler extends ApplicationStatusChangeHandler {

  private final InvoicingPeriodService invoicingPeriodService;

  public AreaRentalStatusChangeHandler(ApplicationService applicationService,
       SupervisionTaskService supervisionTaskService, LocationService locationService,
       ApplicationDao applicationDao, ChargeBasisService chargeBasisService,
       HistoryDao historyDao, InformationRequestDao informationRequestDao,
       InvoiceService invoiceService, TerminationDao terminationDao, InvoicingPeriodService invoicingPeriodService) {
    super(applicationService, supervisionTaskService, locationService,
            applicationDao, chargeBasisService, historyDao, informationRequestDao,
            invoiceService, terminationDao);
    this.invoicingPeriodService = invoicingPeriodService;

  }

  @Override
  protected void handleDecisionStatus(Application application, Integer userId) {
    handleReplacedApplicationOnDecision(application, userId);
    clearTargetState(application);

    boolean replacingApplication = application.getReplacesApplicationId() != null;
    List<Location> locationsOfApprovedSupervisionTasks = new ArrayList<>();
    // If replacing application, get the locations of all present supervision tasks of the replacing application.
    // As only approved locations should yet have a supervision task on the replacing application,
    // getting the locations of existing supervision tasks should enable filtering out approved supervision
    // task locations from locations that should be given a work time supervision.
    if (replacingApplication) {
      List<SupervisionTask> supervisionTasks = getSupervisionTaskService().findByApplicationIdAndType(
        application.getId(), SupervisionTaskType.WORK_TIME_SUPERVISION);
      locationsOfApprovedSupervisionTasks.addAll(
        getSupervisionTaskService().getLocationsOfSupervisionTasks(supervisionTasks));
    }
    // There is no need to create work time supervision task for each location when
    // there is only single location
    if (application.getLocations().size() > 1) {
      application.getLocations().stream()
        .filter(l -> !replacingApplication ||
          !l.getOptionalLocationIfExistsInList(locationsOfApprovedSupervisionTasks).isPresent())
        .forEach(l ->
          createSupervisionTask(application, SupervisionTaskType.WORK_TIME_SUPERVISION, userId,
            TimeUtil.nextDay(l.getEndTime()), l.getId()));
    }

    createSupervisionTask(application, SupervisionTaskType.FINAL_SUPERVISION, userId,
                          TimeUtil.nextDay(application.getEndTime()));
    removeTag(application.getId(), ApplicationTagType.PRELIMINARY_SUPERVISION_DONE);
    removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
    // Lock invoicing of periods having period end date. These cannot be changed
    // after decision because invoices are sent before work is finished
    invoicingPeriodService.findOpenPeriodsForApplicationId(application.getId())
      .stream()
      .filter(p -> p.getEndTime() != null)
      .forEach(p -> finishInvoicingForPeriod(application, p.getId()));
  }

  @Override
  protected void handleFinishedStatus(Application application) {
    AreaRental extension = (AreaRental)application.getExtension();
    updateInvoicingPeriodEndDates(application.getId(), extension.getWorkFinished());
    getInvoiceService().lockInvoices(application.getId());
    getInvoiceService().setInvoicableTime(application.getId(), extension.getWorkFinished());

    lockChargeBasisEntries(application.getId());
    cancelOpenSupervisionTasks(application.getId());
    clearTargetState(application);
    clearOwner(application);
    removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  private void updateInvoicingPeriodEndDates(Integer applicationId, ZonedDateTime workFinishedDate) {
    ZonedDateTime endTime = TimeUtil.homeTime(workFinishedDate).truncatedTo(ChronoUnit.DAYS);
    // Update period end date for periods not having end date or ending after work finished date
    invoicingPeriodService.findOpenPeriodsForApplicationId(applicationId)
        .stream()
        .filter(period -> period.getEndTime() == null || period.getEndTime().isAfter(endTime))
        .forEach(period -> invoicingPeriodService.updatePeriodEndDate(period.getId(), endTime));
  }

}
