package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import fi.hel.allu.model.service.event.InvoicingChangeEvent;
import fi.hel.allu.model.service.event.InvoicingChangeListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.ApplicationKind;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.service.*;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ShortTermRentalStatusChangeHandlerTest {

  private static final int PERIODS_START_YEAR = 2020;
  private static final int PERIODS_END_YEAR = 2025;
  private static final int PERIOD_START_MONTH = 7;
  private static final int PERIOD_END_MONTH = 8;
  private static final int TERMINATION_YEAR = 2022;
  private static final ZonedDateTime TERMINATION_DATE = ZonedDateTime.of(TERMINATION_YEAR, 7, 31, 0, 0, 0, 0, TimeUtil.HelsinkiZoneId);
  private static final int APPLICATION_ID = 99;
  private static final int USER_ID = 999;
  private static final int INVOICE_RECIPIENT_ID = 998;

  @Mock
  private InvoiceService invoiceService;
  @Mock
  private InvoicingPeriodService invoicingPeriodService;
  @Mock
  private ApplicationService applicationService;
  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private SupervisionTaskService supervisionTaskService;
  @Mock
  private ChargeBasisService chargeBasisService;
  @Mock
  private TerminationDao terminationDao;
  @Mock
  private LocationService locationService;
  @Mock
  private InvoicingChangeListener invoicingChangeListener;

  private Application application;
  private List<InvoicingPeriod> invoicingPeriods;
  private List<Integer> periodsBeforeTermination;
  private List<Integer> periodsAfterTermination;
  private Integer periodOnTermination;

  private ShortTermRentalStatusChangeHandler statusChangeHandler;

  @Before
  public void setup() {
    createTestPeriods();
    createApplication();
    statusChangeHandler = new ShortTermRentalStatusChangeHandler(applicationService, supervisionTaskService,
        locationService, applicationDao, chargeBasisService, null, null, invoiceService, terminationDao, invoicingPeriodService);
    when(invoicingPeriodService.findOpenPeriodsForApplicationId(APPLICATION_ID)).thenReturn(invoicingPeriods);
    TerminationInfo terminationInfo = new TerminationInfo();
    terminationInfo.setExpirationTime(TERMINATION_DATE);
    when(terminationDao.getTerminationInfo(APPLICATION_ID)).thenReturn(terminationInfo);
    invoicingChangeListener = new InvoicingChangeListener(invoiceService, applicationService);
  }

  @Test
  public void shouldRemoveRecurringPeriodsAfterTermination() {
    setApplicationRecurring();
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.TERMINATED, USER_ID));
    verify(invoicingPeriodService, times(1)).deletePeriods(APPLICATION_ID, periodsAfterTermination);
  }

  @Test
  public void shouldUpdateRecurringPeriodOnTermination() {
    setApplicationRecurring();
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.TERMINATED, USER_ID));
    verify(invoicingPeriodService, times(1)).deletePeriods(APPLICATION_ID, Collections.singletonList(periodOnTermination));
    ArgumentCaptor<InvoicingPeriod> captor = ArgumentCaptor.forClass(InvoicingPeriod.class);
    verify(invoicingPeriodService, times(1)).insertInvoicingPeriod(captor.capture());
    assertEquals(TERMINATION_DATE, captor.getValue().getEndTime());
  }

  @Test
  public void shouldUpdateNonRecurringChargeBasisOnTermination() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.TERMINATED, USER_ID));
    verify(chargeBasisService, times(1)).unlockEntries(APPLICATION_ID);
    verify(applicationService, times(1)).updateChargeBasis(APPLICATION_ID);
  }

  @Test
  public void shouldNotRemoveRecurringPeriodsBeforeTermination() {
    setApplicationRecurring();
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.TERMINATED, USER_ID));
    verify(invoicingPeriodService, never()).deletePeriods(APPLICATION_ID, periodsBeforeTermination);
  }

  @Test
  public void shouldCreateInvoiceForUpdatedPeriod() {
    setApplicationRecurring();
    application.setNotBillable(false);
    InvoicingPeriod updatedPeriod = new InvoicingPeriod();
    updatedPeriod.setApplicationId(APPLICATION_ID);
    Invoice invoice = new Invoice();
    invoice.setRecipientId(INVOICE_RECIPIENT_ID);
    invoice.setInvoicingPeriodId(periodOnTermination);
    when(invoicingPeriodService.insertInvoicingPeriod(any(InvoicingPeriod.class))).thenReturn(updatedPeriod);
    when(applicationService.findById(anyInt())).thenReturn(application);
    doNothing().when(invoiceService).createInvoices(anyInt(), anyBoolean());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.TERMINATED, USER_ID));
    // Simulate event trigger, as it should be triggered on normal execution
    invoicingChangeListener.onApplicationInvoicingChange(new InvoicingChangeEvent("event source", APPLICATION_ID));
    verify(invoiceService, times(1)).createInvoices(updatedPeriod.getApplicationId(), false);
  }

  private void createTestPeriods() {
    invoicingPeriods = new ArrayList<>();
    periodsBeforeTermination = new ArrayList<>();
    periodsAfterTermination = new ArrayList<>();
    for (int year = PERIODS_START_YEAR; year < PERIODS_END_YEAR; year++) {
      invoicingPeriods.add(createPeriod(year));
      if (year < TERMINATION_DATE.getYear()) {
        periodsBeforeTermination.add(year);
      } else if (year == TERMINATION_DATE.getYear()) {
        periodOnTermination = year;
      } else {
        periodsAfterTermination.add(year);
      }
    }
  }

  private InvoicingPeriod createPeriod(int year) {
    InvoicingPeriod period = new InvoicingPeriod(APPLICATION_ID,
        TERMINATION_DATE.withYear(year).withMonth(PERIOD_START_MONTH).withDayOfMonth(1),
        TERMINATION_DATE.withYear(year).withMonth(PERIOD_END_MONTH).with(lastDayOfMonth())
        );
    period.setId(year);
    return period;
  }

  private void setApplicationRecurring() {
    application.setRecurringEndTime(application.getEndTime().withYear(PERIODS_END_YEAR));
  }

  private Application createApplication() {
    application = new Application();
    application.setId(APPLICATION_ID);
    application.setLocations(Collections.singletonList(new Location()));
    application.setType(ApplicationType.SHORT_TERM_RENTAL);
    application.setExtension(new ShortTermRental());
    application.setKind(ApplicationKind.SUMMER_TERRACE);
    application.setEndTime(TERMINATION_DATE.withYear(PERIODS_START_YEAR).withMonth(PERIOD_END_MONTH).with(lastDayOfMonth()));
    return application;
  }

}
