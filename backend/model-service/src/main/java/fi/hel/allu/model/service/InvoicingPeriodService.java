package fi.hel.allu.model.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import fi.hel.allu.common.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.InvoicingPeriodDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.event.InvoicingPeriodChangeEvent;

@Service
public class InvoicingPeriodService {

  private static final Logger logger = LoggerFactory.getLogger(InvoicingPeriodService.class);

  /**
   * Start of the pricing model where excavation announcement fees are charged per calendar month.
   */
  private static final LocalDateTime MONTHLY_PERIOD_PRICING_START = LocalDateTime.of(2026, 3, 1, 0, 0, 0, 0);

  private final InvoicingPeriodDao invoicingPeriodDao;
  private final ApplicationDao applicationDao;
  private final ChargeBasisDao chargeBasisDao;
  private final ApplicationEventPublisher invoicingPeriodEventPublisher;
  private final WinterTimeService winterTimeService;

  @Autowired
  public InvoicingPeriodService(InvoicingPeriodDao invoicingPeriodDao, ApplicationDao applicationDao,
      ChargeBasisDao chargeBasisDao, ApplicationEventPublisher invoicingPeriodEventPublisher,
      WinterTimeService winterTimeService) {
    this.invoicingPeriodDao = invoicingPeriodDao;
    this.applicationDao = applicationDao;
    this.chargeBasisDao = chargeBasisDao;
    this.invoicingPeriodEventPublisher = invoicingPeriodEventPublisher;
    this.winterTimeService = winterTimeService;
  }

  @Transactional
  public List<InvoicingPeriod> updateInvoicingPeriods(Integer applicationId,
      int periodLength) {
    if (isPeriodModificationAllowed(applicationId)) {
      invoicingPeriodDao.deletePeriods(applicationId);
      createInvoicingPeriods(applicationId, periodLength);
      return findForApplicationId(applicationId);
    } else {
      return findForApplicationId(applicationId);
    }
  }

  @Transactional
  public List<InvoicingPeriod> createInvoicingPeriods(Integer applicationId,
      int periodLength) {
    Application application = applicationDao.findById(applicationId);
    ZonedDateTime start = application.getStartTime().truncatedTo(ChronoUnit.DAYS);
    ZonedDateTime end = application.getEndTime().truncatedTo(ChronoUnit.DAYS);
    List<InvoicingPeriod> periods = createPeriods(applicationId, periodLength, start, end, application.getType() == ApplicationType.AREA_RENTAL);
    applicationDao.setInvoicingPeriodLength(applicationId, periodLength);
    invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    return periods;
  }

  @Transactional
  public List<InvoicingPeriod> createRecurringApplicationPeriods(Integer applicationId) {
    Application application = applicationDao.findById(applicationId);
    List<InvoicingPeriod> recurringPeriods = new ArrayList<>();
    ZonedDateTime periodStart = application.getStartTime();
    ZonedDateTime periodEnd = application.getEndTime();

    while (periodEnd.getYear() <= application.getRecurringEndTime().getYear()) {
      recurringPeriods.add(new InvoicingPeriod(applicationId, periodStart, periodEnd));
      periodStart = periodStart.plusYears(1);
      periodEnd = periodEnd.plusYears(1);
    }

    List<InvoicingPeriod> result;
    List<InvoicingPeriod> currentPeriods = findForApplicationId(applicationId);
    if (isPeriodModificationAllowed(applicationId) && periodsChanged(currentPeriods, recurringPeriods)) {
      invoicingPeriodDao.deletePeriods(applicationId);
      result = invoicingPeriodDao.insertPeriods(recurringPeriods);
      invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    } else {
      result = currentPeriods;
    }
    return result;
  }

  @Transactional
  public InvoicingPeriod insertInvoicingPeriod(InvoicingPeriod period) {
    return invoicingPeriodDao.insertInvoicingPeriod(period);
  }

  private boolean periodsChanged(List<InvoicingPeriod> currentPeriods, List<InvoicingPeriod> newPeriods) {
    return currentPeriods.size() != newPeriods.size() || !currentPeriods.containsAll(newPeriods);
  }

  private List<InvoicingPeriod> createPeriods(Integer applicationId, int periodLength, ZonedDateTime start,
      ZonedDateTime end, boolean lastPeriodOpen) {
    ZonedDateTime currentStart = start;
    ZonedDateTime currentEnd = start.plusMonths(periodLength).minusDays(1);
    List<InvoicingPeriod> result = new ArrayList<>();
    while (currentEnd.isBefore(end)) {
      result.add(new InvoicingPeriod(applicationId, currentStart, currentEnd));
      currentStart = currentEnd.plusDays(1);
      currentEnd =  currentStart.plusMonths(periodLength).minusDays(1);
    }
    result.add(new InvoicingPeriod(applicationId, currentStart, lastPeriodOpen ? null : end));
    return invoicingPeriodDao.insertPeriods(result);
  }

  @Transactional(readOnly = true)
  public List<InvoicingPeriod> findForApplicationId(Integer applicationId) {
    return invoicingPeriodDao.findForApplicationId(applicationId);
  }

  @Transactional(readOnly = true)
  public List<InvoicingPeriod> findOpenPeriodsForApplicationId(Integer applicationId) {
    return invoicingPeriodDao.findOpenPeriodsForApplicationId(applicationId);
  }

  @Transactional(readOnly = true)
  public Optional<InvoicingPeriod> findFirstOpenPeriod(Integer applicationId) {
    return invoicingPeriodDao.findOpenPeriodsForApplicationId(applicationId).stream()
    .sorted()
    .findFirst();
  }

  /**
   * Returns the last open invoicing period of the given phase, ordered by start time.
   *
   * <p>For excavation announcements using the monthly model this is the period that is left open
   * until the phase decision and invoiced when the decision is made.</p>
   */
  @Transactional(readOnly = true)
  public Optional<InvoicingPeriod> findLastOpenPeriodForStatus(Integer applicationId, StatusType status) {
    return invoicingPeriodDao.findOpenPeriodsForApplicationId(applicationId).stream()
        .filter(period -> period.getInvoicableStatus() == status)
        .filter(period -> period.getStartTime() != null)
        .max(Comparator.comparing(InvoicingPeriod::getStartTime));
  }

  @Transactional
  public void deletePeriods(Integer applicationId) {
    if (isPeriodModificationAllowed(applicationId)) {
      invoicingPeriodDao.deletePeriods(applicationId);
      applicationDao.setInvoicingPeriodLength(applicationId, null);
      invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    }
  }

  private boolean isPeriodModificationAllowed(Integer applicationId) {
    List<InvoicingPeriod> existingPeriods = invoicingPeriodDao.findForApplicationId(applicationId);
    return !hasClosedPeriods(existingPeriods);
  }

  private boolean hasClosedPeriods(List<InvoicingPeriod> periods) {
    return periods.stream().anyMatch(InvoicingPeriod::isClosed);
  }

  @Transactional
  public void closeInvoicingPeriods(List<Integer> invoicePeriodIds) {
    invoicingPeriodDao.closeInvoicingPeriods(invoicePeriodIds);
  }

  @Transactional
  public void deletePeriods(Integer applicationId, List<Integer> periodIds) {
    periodIds.forEach(id -> invoicingPeriodDao.deletePeriod(id));
    if (!CollectionUtils.isEmpty(periodIds)) {
      invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    }
  }

  @Transactional
  public void closeInvoicingPeriod(Integer invoicePeriodId) {
    invoicingPeriodDao.closeInvoicingPeriod(invoicePeriodId);
  }

  /**
   * Creates or updates the invoicing periods of an excavation announcement.
   *
   * <p>Applications starting on or after 1.3.2026 and not yet decided use the monthly model where
   * area usage fees are charged per calendar month for the operational condition and the work
   * finished phases separately. Other applications keep the legacy model with one period per
   * invoicable status.</p>
   */
  @Transactional
  public void setExcavationAnnouncementPeriods(Integer applicationId) {
    Application application = applicationDao.findById(applicationId);
    List<InvoicingPeriod> currentPeriods = findForApplicationId(applicationId);
    if (usesMonthlyPeriods(application, currentPeriods)) {
      setMonthlyExcavationPeriods(application, currentPeriods);
    } else {
      setLegacyExcavationPeriods(application, currentPeriods);
    }
  }

  /**
   * Returns true when the application uses the monthly invoicing model.
   *
   * <p>The model applies to excavation announcements starting on or after 1.3.2026. Applications
   * that were already decided before the model was taken into use keep the legacy model, unless
   * they already have dated periods. Dated periods are recognised by a period start time, which the
   * legacy status-only periods never have.</p>
   */
  private boolean usesMonthlyPeriods(Application application, List<InvoicingPeriod> currentPeriods) {
    if (application.getType() != ApplicationType.EXCAVATION_ANNOUNCEMENT || application.getStartTime() == null) {
      return false;
    }
    if (hasDatedPeriods(currentPeriods)) {
      return true;
    }
    if (!isPostMonthlyPricingStart(application.getStartTime())) {
      return false;
    }
    // Legacy model is kept for applications that are already decided or further in the process
    // (REJECTED, OPERATIONAL_CONDITION, FINISHED, ...). Returning to a status before the decision
    // moves the application back to the monthly model.
    StatusType status = application.getStatus();
    return status == null || status.getOrderNumber() < StatusType.DECISION.getOrderNumber();
  }

  private boolean hasDatedPeriods(List<InvoicingPeriod> periods) {
    return periods.stream().anyMatch(period -> period.getStartTime() != null);
  }

  private boolean isPostMonthlyPricingStart(ZonedDateTime startTime) {
    return !startTime.withZoneSameInstant(TimeUtil.HelsinkiZoneId)
        .isBefore(ZonedDateTime.of(MONTHLY_PERIOD_PRICING_START, TimeUtil.HelsinkiZoneId));
  }

  private void setMonthlyExcavationPeriods(Application application, List<InvoicingPeriod> currentPeriods) {
    List<InvoicingPeriod> desiredPeriods = ExcavationMonthlyPeriodCalculator.calculateForExcavation(
        application.getId(),
        getPeriodStart(application),
        getPeriodEnd(application),
        getWinterTimeOperation(application),
        winterTimeService.getWinterTime(),
        isNoAreaUsageFee(application));

    PeriodReconciliation reconciliation = InvoicingPeriodReconciler.reconcile(currentPeriods, desiredPeriods);
    if (!reconciliation.hasChanges()) {
      return;
    }

    List<InvoicingPeriod> removable = new ArrayList<>();
    for (InvoicingPeriod period : reconciliation.getPeriodsToDelete()) {
      if (isPeriodInUse(period)) {
        logger.warn("Invoicing period {} ({} - {}) of application {} is invoiced and cannot be removed",
            period.getId(), TimeUtil.dateAsString(period.getStartTime()), TimeUtil.dateAsString(period.getEndTime()),
            application.getId());
      } else {
        removable.add(period);
      }
    }

    // Detach charge basis entries from periods that are going away, but keep entries that are
    // already locked to an invoice so that invoiced history stays intact
    removable.forEach(period -> {
      invoicingPeriodDao.removeUnlockedEntriesFromPeriod(period.getId());
      invoicingPeriodDao.deletePeriod(period.getId());
    });
    List<InvoicingPeriod> toInsert = new ArrayList<>(reconciliation.getPeriodsToInsert());
    if (!toInsert.isEmpty()) {
      invoicingPeriodDao.insertPeriods(toInsert);
    }
    invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, application.getId()));
  }

  /**
   * Returns true when the period is closed or carries charge basis entries locked to an invoice.
   * Such periods must not be removed.
   */
  private boolean isPeriodInUse(InvoicingPeriod period) {
    return period.isClosed() || hasLockedEntries(period.getId());
  }

  private boolean hasLockedEntries(Integer periodId) {
    return periodId != null && !chargeBasisDao.getLockedEntriesOfPeriod(periodId).isEmpty();
  }

  private ZonedDateTime getPeriodStart(Application application) {
    return application.getStartTime().withZoneSameInstant(TimeUtil.HelsinkiZoneId);
  }

  private ZonedDateTime getPeriodEnd(Application application) {
    ZonedDateTime workFinished = getWorkFinished(application);
    ZonedDateTime endTime = workFinished != null ? workFinished : application.getEndTime();
    return endTime != null ? endTime.withZoneSameInstant(TimeUtil.HelsinkiZoneId) : application.getEndTime();
  }

  private ZonedDateTime getWinterTimeOperation(Application application) {
    if (application.getExtension() instanceof ExcavationAnnouncement excavationAnnouncement) {
      return excavationAnnouncement.getWinterTimeOperation();
    }
    return null;
  }

  private ZonedDateTime getWorkFinished(Application application) {
    if (application.getExtension() instanceof ExcavationAnnouncement excavationAnnouncement) {
      return excavationAnnouncement.getWorkFinished();
    }
    return null;
  }

  private boolean isNoAreaUsageFee(Application application) {
    if (application.getExtension() instanceof ExcavationAnnouncement excavationAnnouncement) {
      return Boolean.TRUE.equals(excavationAnnouncement.getNoAreaUsageFee());
    }
    return false;
  }

  private void setLegacyExcavationPeriods(Application application, List<InvoicingPeriod> currentPeriods) {
    Integer applicationId = application.getId();
    Map<StatusType, InvoicingPeriod> periods = new HashMap<>();
    if (getWinterTimeOperation(application) != null) {
      periods.put(StatusType.OPERATIONAL_CONDITION, new InvoicingPeriod(applicationId, StatusType.OPERATIONAL_CONDITION));
    }
    periods.put(StatusType.FINISHED, new InvoicingPeriod(applicationId, StatusType.FINISHED));
    Map<StatusType, InvoicingPeriod> currentPeriodsByStatus = currentPeriods.stream()
        .collect(Collectors.toMap(InvoicingPeriod::getInvoicableStatus, Function.identity()));
    periods.entrySet().stream().filter(p -> !currentPeriodsByStatus.containsKey(p.getKey()))
        .forEach(p -> invoicingPeriodDao.insertInvoicingPeriod(p.getValue()));
    currentPeriodsByStatus.entrySet().stream().filter(c -> !periods.containsKey(c.getKey()))
        .forEach(c -> deletePeriod(c.getValue()));
    if (currentPeriodsByStatus.keySet().size() != periods.size()) {
      invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    }
  }

  private void deletePeriod(InvoicingPeriod invoicingPeriod) {
    if (invoicingPeriod.isClosed()) {
      throw new IllegalOperationException("invoicingPeriod.invoiced");
    }
    invoicingPeriodDao.removeEntriesFromPeriod(invoicingPeriod.getId());
    invoicingPeriodDao.deletePeriod(invoicingPeriod.getId());
  }

  @Transactional
  public void updatePeriodEndDate(Integer periodId, ZonedDateTime endTime) {
    invoicingPeriodDao.updateEndTime(periodId, endTime);
  }

  @Transactional
  public boolean isLockedPeriod(Integer periodId){
   return invoicingPeriodDao.findInvoicingPeriod(periodId).isClosed();
  }

  /**
   * Returns true when the given period is the last period of its phase.
   *
   * <p>The last period of the operational condition and the work finished phases is left open
   * until the phase decision and invoiced when the decision is made.</p>
   */
  @Transactional(readOnly = true)
  public boolean isFinalPeriod(Application application, InvoicingPeriod period) {
    if (period.getInvoicableStatus() == null || period.getStartTime() == null) {
      return false;
    }
    List<InvoicingPeriod> samePhasePeriods = findForApplicationId(application.getId()).stream()
        .filter(p -> p.getInvoicableStatus() == period.getInvoicableStatus())
        .filter(p -> p.getStartTime() != null)
        .collect(Collectors.toList());
    Optional<InvoicingPeriod> last = samePhasePeriods.stream()
        .max(Comparator.comparing(InvoicingPeriod::getStartTime));
    return last.isPresent() && Objects.equals(last.get().getId(), period.getId());
  }

  /**
   * Returns the last day of the given phase in the application's timeline.
   *
   * @param application the application
   * @param status      the phase
   * @return the phase end date, or {@code null} when the phase has no end date yet
   */
  @Transactional(readOnly = true)
  public ZonedDateTime getPhaseEnd(Application application, StatusType status) {
    ZonedDateTime winterTimeOperation = getWinterTimeOperation(application);
    ZonedDateTime workFinished = getWorkFinished(application);
    ZonedDateTime applicationEnd = application.getEndTime();
    return switch (status) {
      case OPERATIONAL_CONDITION -> winterTimeOperation;
      case FINISHED -> workFinished != null ? workFinished : applicationEnd;
      default -> applicationEnd;
    };
  }
}
