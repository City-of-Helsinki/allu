package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import fi.hel.allu.common.domain.ApplicationDateReport;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

import static org.mockito.Matchers.*;


@RunWith(MockitoJUnitRunner.class)
public class DateReportingServiceTest {

  private static final Integer APP_ID = 1234;
  private static final int LOC_ID = 99;

  @Mock
  private ApplicationService applicationService;
  @Mock
  private ApplicationJsonService applicationJsonService;
  @Mock
  private SupervisionTaskService supervisionTaskService;
  @Mock
  private ApplicationServiceComposer applicationServiceComposer;
  @Mock
  private LocationService locationService;
  @Mock
  private ApplicationHistoryService applicationHistoryService;
  @Mock
  private InvoicingPeriodService invoicingPeriodService;
  @Mock
  private ApplicationEventDispatcher eventDispatcher;
  @Mock
  private UserService userService;

  private DateReportingService dateReportingService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    dateReportingService = new DateReportingService(applicationService, applicationJsonService,
        supervisionTaskService, applicationServiceComposer, locationService,
        applicationHistoryService, invoicingPeriodService, eventDispatcher,
        userService);

    final List<LocationJson> locations = new ArrayList<>();
    LocationJson location = new LocationJson();
    location.setId(LOC_ID);
    location.setStartTime(ZonedDateTime.now().minusDays(5));
    location.setEndTime(ZonedDateTime.now());
    locations.add(location);
    final ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setLocations(locations);
    applicationJson.setId(APP_ID);
    applicationJson.setStatus(StatusType.DECISION);
    Application application = new Application();
    application.setId(APP_ID);
    application.setExtension(new AreaRental());
    application.setStatus(StatusType.DECISION);

    Mockito.when(applicationJsonService.getFullyPopulatedApplication(Mockito.any())).thenReturn(applicationJson);
    Mockito.when(applicationService.setCustomerValidityDates(eq(APP_ID), any(ApplicationDateReport.class))).thenReturn(application);
    Mockito.when(userService.getCurrentUser()).thenReturn(new UserJson(15));
  }

  @Test
  public void workFinishedDateCantBeBeforeAreaStartDate() {
    Mockito.when(invoicingPeriodService.getInvoicingPeriods(Mockito.any())).thenReturn(Collections.emptyList());
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("workfinisheddate.before.area.start");

    final ZonedDateTime workFinishedDate = ZonedDateTime.now().minusDays(10);
    dateReportingService.reportWorkFinished(APP_ID, workFinishedDate);
  }

  @Test
  public void workFinishedDateIsOnInvoicedPeriod() {
    final List<InvoicingPeriod> invoicingPeriods = new ArrayList<>();
    final InvoicingPeriod period = new InvoicingPeriod(APP_ID, ZonedDateTime.now().minusDays(10), ZonedDateTime.now().plusDays(10));
    period.setClosed(true);
    invoicingPeriods.add(period);
    Mockito.when(invoicingPeriodService.getInvoicingPeriods(Mockito.any())).thenReturn(invoicingPeriods);
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("workfinisheddate.invoiced.invoicing.period");

    final ZonedDateTime workFinishedDate = ZonedDateTime.now().minusDays(10);
    dateReportingService.reportWorkFinished(APP_ID, workFinishedDate);
  }

  @Test
  public void updateUustomerLocationValidityUpdatesSupervisionTask() {
    final ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), ZonedDateTime.now().minusDays(5), ZonedDateTime.now().plusDays(5));
    final SupervisionTaskJson task = new SupervisionTaskJson();
    task.setType(SupervisionTaskType.WORK_TIME_SUPERVISION);
    task.setStatus(SupervisionTaskStatusType.OPEN);
    Mockito.when(supervisionTaskService.findByLocationId(Mockito.eq(LOC_ID))).thenReturn(Arrays.asList(task));

    dateReportingService.reportCustomerLocationValidity(APP_ID, LOC_ID, dateReport);
    Mockito.verify(supervisionTaskService).updateSupervisionTaskDate(
        eq(APP_ID), eq(SupervisionTaskType.WORK_TIME_SUPERVISION), eq(LOC_ID), eq(dateReport.getReportedEndDate().plusDays(1)));
  }

  @Test
  public void shouldPublishApplicationEventOnReportCustomerValidity() {
    final ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), ZonedDateTime.now().minusDays(5), ZonedDateTime.now().plusDays(5));
    dateReportingService.reportCustomerValidity(APP_ID, dateReport);
    Mockito.verify(eventDispatcher, Mockito.times(1)).dispatchUpdateEvent(eq(APP_ID), anyInt(),
        eq(ApplicationNotificationType.CUSTOMER_VALIDITY_PERIOD_CHANGED), any(StatusType.class));
  }

  @Test
  public void shouldPublishApplicationEventOnReportCustomerLocationValidity() {
    final ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), ZonedDateTime.now().minusDays(5), ZonedDateTime.now().plusDays(5));
    dateReportingService.reportCustomerLocationValidity(APP_ID, LOC_ID, dateReport);
    Mockito.verify(eventDispatcher, Mockito.times(1)).dispatchUpdateEvent(eq(APP_ID), anyInt(),
        eq(ApplicationNotificationType.CUSTOMER_VALIDITY_PERIOD_CHANGED), any(StatusType.class));
  }
}
