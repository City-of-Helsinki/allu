package fi.hel.allu.model.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoicingPeriodDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoicingPeriod;

@Service
public class InvoicingPeriodService {


  private final InvoicingPeriodDao invoicingPeriodDao;
  private final ApplicationDao applicationDao;

  @Autowired
  public InvoicingPeriodService(InvoicingPeriodDao invoicingPeriodDao, ApplicationDao applicationDao) {
    this.invoicingPeriodDao = invoicingPeriodDao;
    this.applicationDao = applicationDao;
  }

  @Transactional
  public List<InvoicingPeriod> update(Integer applicationId,
      int periodLength) {
    List<InvoicingPeriod> existingPeriods = invoicingPeriodDao.findForApplicationId(applicationId);
    if (!hasInvoicedPeriods(existingPeriods)) {
      deletePeriods(applicationId);
      return createInvoicingPeriods(applicationId, periodLength);
    } else {
      InvoicingPeriod latestInvoiced = findLatestInvoicedPeriod(existingPeriods);
      Application application = applicationDao.findById(applicationId);
      invoicingPeriodDao.deleteUninvoicedPeriods(applicationId);
      createPeriods(applicationId, periodLength, latestInvoiced.getEndTime().plusDays(1),
          application.getEndTime().truncatedTo(ChronoUnit.DAYS));
    }
    return findForApplicationId(applicationId);
  }

  private boolean hasInvoicedPeriods(List<InvoicingPeriod> periods) {
    return periods.stream().anyMatch(InvoicingPeriod::isInvoiced);
  }

  private InvoicingPeriod findLatestInvoicedPeriod(List<InvoicingPeriod> periods) {
    return periods.stream()
        .filter(p -> p.isInvoiced())
        .sorted(Comparator.comparing(InvoicingPeriod::getEndTime, Comparator.reverseOrder()))
        .findFirst().get();
  }

  @Transactional
  public List<InvoicingPeriod> createInvoicingPeriods(Integer applicationId,
      int periodLength) {
    Application application = applicationDao.findById(applicationId);
    ZonedDateTime start = application.getStartTime().truncatedTo(ChronoUnit.DAYS);
    ZonedDateTime end = application.getEndTime().truncatedTo(ChronoUnit.DAYS);
    return createPeriods(applicationId, periodLength, start, end);
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
    invoicingPeriodDao.deletePeriods(applicationId);
  }

  @Transactional
  public void closeInvoicingPeriods(List<Integer> invoicePeriodIds) {
    invoicingPeriodDao.closeInvoicingPeriods(invoicePeriodIds);
  }
}
