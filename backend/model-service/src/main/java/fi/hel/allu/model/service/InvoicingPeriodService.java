package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoicingPeriodDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.event.InvoicingPeriodChangeEvent;

@Service
public class InvoicingPeriodService {

  private final InvoicingPeriodDao invoicingPeriodDao;
  private final ApplicationDao applicationDao;
  private final ApplicationEventPublisher invoicingPeriodEventPublisher;

  @Autowired
  public InvoicingPeriodService(InvoicingPeriodDao invoicingPeriodDao, ApplicationDao applicationDao, ApplicationEventPublisher invoicingPeriodEventPublisher) {
    this.invoicingPeriodDao = invoicingPeriodDao;
    this.applicationDao = applicationDao;
    this.invoicingPeriodEventPublisher = invoicingPeriodEventPublisher;
  }

  @Transactional
  public List<InvoicingPeriod> updateInvoicingPeriods(Integer applicationId,
      int periodLength) {
    validatePeriodModificationAllowed(applicationId);
    invoicingPeriodDao.deletePeriods(applicationId);
    createInvoicingPeriods(applicationId, periodLength);
    return findForApplicationId(applicationId);
  }

  @Transactional
  public List<InvoicingPeriod> createInvoicingPeriods(Integer applicationId,
      int periodLength) {
    Application application = applicationDao.findById(applicationId);
    ZonedDateTime start = application.getStartTime().truncatedTo(ChronoUnit.DAYS);
    ZonedDateTime end = application.getEndTime().truncatedTo(ChronoUnit.DAYS);
    List<InvoicingPeriod> periods = createPeriods(applicationId, periodLength, start, end);
    applicationDao.setInvoicingPeriodLength(applicationId, periodLength);
    invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
    return periods;
  }

  private List<InvoicingPeriod> createPeriods(Integer applicationId, int periodLength, ZonedDateTime start,
      ZonedDateTime end) {
    ZonedDateTime currentStart = start;
    ZonedDateTime currentEnd = start.plusMonths(periodLength).minusDays(1);
    List<InvoicingPeriod> result = new ArrayList<>();
    while (currentEnd.isBefore(end)) {
      result.add(new InvoicingPeriod(applicationId, currentStart, currentEnd));
      currentStart = currentEnd.plusDays(1);
      currentEnd =  currentStart.plusMonths(periodLength).minusDays(1);
    }
    result.add(new InvoicingPeriod(applicationId, currentStart, end));
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
    .sorted(Comparator.comparing(InvoicingPeriod::getStartTime))
    .findFirst();
  }

  @Transactional
  public void deletePeriods(Integer applicationId) {
    validatePeriodModificationAllowed(applicationId);
    invoicingPeriodDao.deletePeriods(applicationId);
    applicationDao.setInvoicingPeriodLength(applicationId, null);
    invoicingPeriodEventPublisher.publishEvent(new InvoicingPeriodChangeEvent(this, applicationId));
  }

  private void validatePeriodModificationAllowed(Integer applicationId) {
    List<InvoicingPeriod> existingPeriods = invoicingPeriodDao.findForApplicationId(applicationId);
    if (hasInvoicedPeriods(existingPeriods)) {
      throw new IllegalOperationException("invoicingPeriod.invoiced");
    }
  }

  private boolean hasInvoicedPeriods(List<InvoicingPeriod> periods) {
    return periods.stream().anyMatch(InvoicingPeriod::isInvoiced);
  }

  @Transactional
  public void closeInvoicingPeriods(List<Integer> invoicePeriodIds) {
    invoicingPeriodDao.closeInvoicingPeriods(invoicePeriodIds);
  }
}
