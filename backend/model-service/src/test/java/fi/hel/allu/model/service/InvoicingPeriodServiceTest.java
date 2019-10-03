package fi.hel.allu.model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.collection.IsCollectionWithSize;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.common.exception.IllegalOperationException;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.InvoicingPeriodDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.InvoicingPeriod;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class InvoicingPeriodServiceTest {


  private static final Integer APPLICATION_ID = Integer.valueOf(99);
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
    when(invoicingPeriodDao.insertPeriods(anyListOf(InvoicingPeriod.class))).then(AdditionalAnswers.returnsFirstArg());
    Application application = new Application();
    application.setStartTime(START_TIME);
    application.setEndTime(END_TIME);
    when(invoicingPeriodDao.insertPeriods(anyListOf(InvoicingPeriod.class))).then(AdditionalAnswers.returnsFirstArg());
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

  @Test(expected = IllegalOperationException.class)
  public void shouldNotUpdateClosedPeriods() {
    List<InvoicingPeriod> existingPeriods = new ArrayList<>();
    existingPeriods.add(new InvoicingPeriod(APPLICATION_ID, START_TIME, START_TIME.plusMonths(6).minusDays(1)));
    existingPeriods.add(new InvoicingPeriod(APPLICATION_ID, START_TIME.plusMonths(6), END_TIME));
    existingPeriods.get(0).setClosed(true);
    when(invoicingPeriodDao.findForApplicationId(APPLICATION_ID)).thenReturn(existingPeriods);
    invoicingPeriodService.updateInvoicingPeriods(APPLICATION_ID, 6);
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
