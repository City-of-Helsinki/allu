package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Invoice;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.*;

import static fi.hel.allu.model.common.ApplicationUtil.isRecurringRental;

@Service
public class ShortTermRentalStatusChangeHandler extends ApplicationStatusChangeHandler {

  private final InvoicingPeriodService invoicingPeriodService;

  @Autowired
  public ShortTermRentalStatusChangeHandler(ApplicationService applicationService,
                                            SupervisionTaskService supervisionTaskService, LocationService locationService, ApplicationDao applicationDao,
                                            ChargeBasisService chargeBasisService, HistoryDao historyDao, InformationRequestDao informationRequestDao,
                                            InvoiceService invoiceService, TerminationDao terminationDao, InvoicingPeriodService invoicingPeriodService) {
    super(applicationService, supervisionTaskService, locationService, applicationDao, chargeBasisService, historyDao,
        informationRequestDao, invoiceService, terminationDao);
    this.invoicingPeriodService = invoicingPeriodService;
  }

  @Override
  protected void handleTerminatedStatus(Application application, Integer userId) {
    updateTerminationDecisionInfo(application, userId);
    clearTargetState(application);
    clearOwner(application);
    createSupervisionTaskForTerminated(application, userId);
    // Handle termination of short term rental
    handleShortTermRentalTermination(application);
    finishInvoicing(application);
  }

  private void handleShortTermRentalTermination(Application application) {
    TerminationInfo termination = getTermination(application.getId());
    if (termination != null && isRecurringRental(application)) {
      handleRecurringPeriodTermination(application, termination.getExpirationTime());
    }
    else if (termination != null) {
      handleUpdateChargeBasisOnTermination(application);
    }
  }

  private void handleRecurringPeriodTermination(Application application, ZonedDateTime expirationTime) {
    List<InvoicingPeriod> openInvoicingPeriods = invoicingPeriodService.findOpenPeriodsForApplicationId(application.getId());
    // If there's period on termination date, create new period ending on termination date and create new
    // invoice for it
    openInvoicingPeriods.stream()
        .filter(p -> isDateOnPeriod(p, expirationTime)).findFirst()
        .ifPresent(p -> handleInvoicingPeriodOnTermination(p, application, expirationTime));

    // Remove invoicing periods (and invoices) after termination date
    List<Integer> periodsToRemove = openInvoicingPeriods.stream()
      .filter(p -> periodStartsAfter(p, expirationTime))
      .map(InvoicingPeriod::getId)
      .collect(Collectors.toList());
    invoicingPeriodService.deletePeriods(application.getId(), periodsToRemove);
  }

  private void handleUpdateChargeBasisOnTermination(Application application) {
    getChargeBasisService().unlockEntries(application.getId());
    getApplicationService().updateChargeBasis(application.getId());
  }

  private void handleInvoicingPeriodOnTermination(InvoicingPeriod periodOnTermination, Application application,
                                                  ZonedDateTime expirationTime) {
    invoicingPeriodService.insertInvoicingPeriod(
      new InvoicingPeriod(application.getId(), periodOnTermination.getStartTime(), expirationTime));
    invoicingPeriodService.deletePeriods(application.getId(), Collections.singletonList(periodOnTermination.getId()));
  }

  private boolean periodStartsAfter(InvoicingPeriod period, ZonedDateTime date) {
    return period.getStartTime() != null && TimeUtil.isDateAfter(period.getStartTime(), date);
  }

  private boolean isDateOnPeriod(InvoicingPeriod period, ZonedDateTime date) {
    return period.getStartTime() != null && TimeUtil.isSameDateOrLater(date, period.getStartTime())
        && period.getEndTime() != null && TimeUtil.isSameDateOrLater(period.getEndTime(), date);
  }
}
