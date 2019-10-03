package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    // Handle termination of recurring rental
    handleRecurringRentalTermination(application);
    finishInvoicing(application);
  }

  private void handleRecurringRentalTermination(Application application) {
    TerminationInfo termination = getTermination(application.getId());
    if (termination != null && isRecurringRental(application)) {
      handleRecurringPeriodTermination(application, termination.getExpirationTime());
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

  private void handleInvoicingPeriodOnTermination(InvoicingPeriod periodOnTermination, Application application,
      ZonedDateTime expirationTime) {
    Optional<Invoice> existingInvoice = findInvoiceForPeriod(periodOnTermination);
    InvoicingPeriod updatedPeriod = invoicingPeriodService.insertInvoicingPeriod(new InvoicingPeriod(application.getId(), periodOnTermination.getStartTime(), expirationTime));
    invoicingPeriodService.deletePeriods(application.getId(), Collections.singletonList(periodOnTermination.getId()));
    existingInvoice.ifPresent(e ->  getInvoiceService().addInvoiceForPeriod(updatedPeriod, e.getRecipientId(), e.isSapIdPending()));
  }

  private Optional<Invoice> findInvoiceForPeriod(InvoicingPeriod period) {
    return getInvoiceService().findByApplication(period.getApplicationId()).stream()
        .filter(i -> period.getId().equals(i.getInvoicingPeriodId()))
        .findFirst();
  }

  private boolean periodStartsAfter(InvoicingPeriod period, ZonedDateTime date) {
    return period.getStartTime() != null && TimeUtil.isDateAfter(period.getStartTime(), date);
  }

  private boolean isDateOnPeriod(InvoicingPeriod period, ZonedDateTime date) {
    return period.getStartTime() != null && TimeUtil.isSameDateOrLater(date, period.getStartTime())
        && period.getEndTime() != null && TimeUtil.isSameDateOrLater(period.getEndTime(), date);
  }

  private boolean isRecurringRental(Application application) {
    return application.getKind().isTerrace() &&
        application.getRecurringEndTime() != null &&
        application.getEndTime() != null &&
        application.getRecurringEndTime().getYear() > application.getEndTime().getYear();
  }
}
