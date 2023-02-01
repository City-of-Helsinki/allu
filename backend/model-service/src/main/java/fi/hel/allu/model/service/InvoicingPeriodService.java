package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoicingPeriodDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.event.InvoicingPeriodChangeEvent;

@Service
public class InvoicingPeriodService {

  private final InvoicingPeriodDao invoicingPeriodDao;
  private final ApplicationDao applicationDao;
  private final ApplicationEventPublisher invoicingPeriodEventPublisher;

  @Autowired
  public InvoicingPeriodService(InvoicingPeriodDao invoicingPeriodDao, ApplicationDao applicationDao,
      ApplicationEventPublisher invoicingPeriodEventPublisher) {
    this.invoicingPeriodDao = invoicingPeriodDao;
    this.applicationDao = applicationDao;
    this.invoicingPeriodEventPublisher = invoicingPeriodEventPublisher;
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
    invoicingPeriodDao.deleteByPeriodIds(periodIds);
    if (!CollectionUtils.isEmpty(periodIds)) {
      invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    }
  }

  @Transactional
  public void closeInvoicingPeriod(Integer invoicePeriodId) {
    invoicingPeriodDao.closeInvoicingPeriod(invoicePeriodId);
  }

  @Transactional
  public void setExcavationAnnouncementPeriods(Integer applicationId) {
    Map<StatusType, InvoicingPeriod> periods = new HashMap<>();
    if (isWinterTimeOperation(applicationId)) {
      periods.put(StatusType.OPERATIONAL_CONDITION, new InvoicingPeriod(applicationId, StatusType.OPERATIONAL_CONDITION));
    }
    periods.put(StatusType.FINISHED, new InvoicingPeriod(applicationId, StatusType.FINISHED));
    Map<StatusType, InvoicingPeriod> currentPeriods = findForApplicationId(applicationId).stream()
        .collect(Collectors.toMap(InvoicingPeriod::getInvoicableStatus, Function.identity() ));

    List<InvoicingPeriod> insertedPeriods = periods.entrySet().stream().filter(p -> !currentPeriods.containsKey(p.getKey()))
            .map(p-> p.getValue()).collect(Collectors.toList());
    invoicingPeriodDao.insertPeriods(insertedPeriods);

    List<InvoicingPeriod> periodsToDelet = currentPeriods.entrySet().stream().filter(c -> !periods.containsKey(c.getKey()))
            .map(c -> c.getValue()).collect(Collectors.toList());
    deletePeriods(periodsToDelet);

    if (currentPeriods.keySet().size() != periods.size()) {
      invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    }
  }

  private void deletePeriods(List<InvoicingPeriod> invoicingPeriod) {
    if (invoicingPeriod.stream().anyMatch(InvoicingPeriod::isClosed)) {
      throw new IllegalOperationException("invoicingPeriod.invoiced");
    }
    List<Integer> periodIds = invoicingPeriod.stream().map(InvoicingPeriod::getId).collect(Collectors.toList());
    invoicingPeriodDao.removeEntriesFromPeriods(periodIds);
    invoicingPeriodDao.deleteByPeriodIds(periodIds);
  }

  private boolean isWinterTimeOperation(Integer applicationId) {
    Application application = applicationDao.findById(applicationId);
    if (application.getType() == ApplicationType.EXCAVATION_ANNOUNCEMENT &&
        ((ExcavationAnnouncement)application.getExtension()).getWinterTimeOperation() != null) {
      return true;
    }
    return false;
  }

  public void updatePeriodEndDates(List<Integer> periodIds, ZonedDateTime endTime) {
    invoicingPeriodDao.updateEndTimes(periodIds, endTime);
  }

  @Transactional
  public boolean isLockedPeriod(Integer periodId){
   return invoicingPeriodDao.findInvoicingPeriod(periodId).isClosed();
  }
}