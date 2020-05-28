package fi.hel.allu.model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoicingPeriodDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoicingPeriod;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class InvoicingPeriodServiceTest {


  private static final Integer APPLICATION_ID = 99;
  @Mock
  private InvoicingPeriodDao invoicingPeriodDao;
  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private ApplicationEventPublisher eventPublisher;

  private static final ZonedDateTime START_TIME = LocalDate.parse("2018-01-01").atStartOfDay(TimeUtil.HelsinkiZoneId);
  private static final ZonedDateTime END_TIME = LocalDate.parse("2018-10-04").atStartOfDay(TimeUtil.HelsinkiZoneId);


  private InvoicingPeriodService invoicingPeriodService;

  @Before
  public void setup() {
    invoicingPeriodService = new InvoicingPeriodService(invoicingPeriodDao, applicationDao, eventPublisher);
    when(invoicingPeriodDao.insertPeriods(anyList())).then(AdditionalAnswers.returnsFirstArg());
    Application application = new Application();
    application.setStartTime(START_TIME);
    application.setEndTime(END_TIME);
    when(invoicingPeriodDao.insertPeriods(anyList())).then(AdditionalAnswers.returnsFirstArg());
    when(applicationDao.findById(APPLICATION_ID)).thenReturn(application);
  }

  @Test
  public void shouldCreatePeriods() {
    List<InvoicingPeriod> result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 3);
    validatePeriods(result, 4);
    result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 1);
    validatePeriods(result, 10);
    result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 6);
    validatePeriods(result, 2);
    result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 12);
    validatePeriods(result, 1);
  }

  @Test
  public void shouldCreateRecurringPeriods() {
    Application application = applicationDao.findById(APPLICATION_ID);
    application.setRecurringEndTime(END_TIME.plusYears(2));
    // Check if period affects, even if it should be null. END_TIME - START_TIME difference should be 9 months.
    application.setInvoicingPeriodLength(5);
    List<InvoicingPeriod> result = invoicingPeriodService.createRecurringApplicationPeriods(APPLICATION_ID);
    // Not using validatePeriods() method, as it asserts if periods have 1 day between
    assertEquals(3, result.size());
  }

  @Test
  public void shouldNotUpdateClosedPeriods() {
    List<InvoicingPeriod> existingPeriods = new ArrayList<>();
    existingPeriods.add(new InvoicingPeriod(APPLICATION_ID, START_TIME, START_TIME.plusMonths(6).minusDays(1)));
    existingPeriods.add(new InvoicingPeriod(APPLICATION_ID, START_TIME.plusMonths(6), END_TIME));
    existingPeriods.get(0).setClosed(true);
    when(invoicingPeriodDao.findForApplicationId(APPLICATION_ID)).thenReturn(existingPeriods);
    invoicingPeriodService.updateInvoicingPeriods(APPLICATION_ID, 6);
    verify(invoicingPeriodDao, never()).insertInvoicingPeriod(any(InvoicingPeriod.class));
    verify(invoicingPeriodDao, never()).deletePeriods(any(Integer.class));
  }

  private void validatePeriods(List<InvoicingPeriod> result, int expectedAmount) {
    assertEquals(expectedAmount, result.size());
    Iterator<InvoicingPeriod> iterator = result.iterator();
    InvoicingPeriod previous = iterator.next();
    assertEquals(0, Duration.between(START_TIME, previous.getStartTime()).toDays());
    while (iterator.hasNext()) {
      InvoicingPeriod current = iterator.next();
      assertEquals(1, Duration.between(previous.getEndTime(), current.getStartTime()).toDays());
      previous = current;
    }
    assertEquals(0, Duration.between(END_TIME, previous.getEndTime()).toDays());
  }

}
