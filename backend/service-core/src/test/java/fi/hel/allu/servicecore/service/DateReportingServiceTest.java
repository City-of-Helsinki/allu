package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.model.domain.ApplicationExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

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
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;


@ExtendWith({MockitoExtension.class})
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

  @InjectMocks
  private DateReportingService dateReportingService;


  @BeforeEach
  public void setUp() {

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

    lenient().when(applicationJsonService.getFullyPopulatedApplication(any())).thenReturn(applicationJson);
    lenient().when(applicationService.setCustomerValidityDates(eq(APP_ID), any(ApplicationDateReport.class))).thenReturn(application);
    lenient().when(userService.getCurrentUser()).thenReturn(new UserJson(15));
  }

  @Test
  public void workFinishedDateCantBeBeforeAreaStartDate() {
    Mockito.when(invoicingPeriodService.getInvoicingPeriods(any())).thenReturn(Collections.emptyList());

    final ZonedDateTime workFinishedDate = ZonedDateTime.now().minusDays(10);
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      dateReportingService.reportWorkFinished(APP_ID, workFinishedDate);
    });
    assertEquals("workfinisheddate.before.area.start", exception.getMessage());

  }


  @ParameterizedTest
  @ValueSource(ints = {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4})
  public void workFinishedDateIsOnInvoicedPeriodExcludingEndDate(int day) {
    final List<InvoicingPeriod> invoicingPeriods = new ArrayList<>();
    final InvoicingPeriod period = new InvoicingPeriod(APP_ID, ZonedDateTime.now().minusDays(5), ZonedDateTime.now().plusDays(5));
    period.setClosed(true);
    invoicingPeriods.add(period);
    Mockito.when(invoicingPeriodService.getInvoicingPeriods(any())).thenReturn(invoicingPeriods);
    final ZonedDateTime workFinishedDate = ZonedDateTime.now().plusDays(day);

    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      dateReportingService.reportWorkFinished(APP_ID, workFinishedDate);
    });
    assertEquals("workfinisheddate.invoiced.invoicing.period", exception.getMessage());

  }

  @Test
  public void workFinishedDateIsLastInvoiciPeriodDate() {
    final List<InvoicingPeriod> invoicingPeriods = new ArrayList<>();
    final InvoicingPeriod period = new InvoicingPeriod(APP_ID, ZonedDateTime.now().minusDays(5), ZonedDateTime.now().plusDays(5));
    period.setClosed(true);
    invoicingPeriods.add(period);
    Mockito.when(invoicingPeriodService.getInvoicingPeriods(any())).thenReturn(invoicingPeriods);
    final ZonedDateTime workFinishedDate = ZonedDateTime.now().plusDays(5);
    Application dummApplication = new Application();
    dummApplication.setExtension(new ApplicationExtension() {
      @Override
      public ApplicationType getApplicationType() {
        return null;
      }
    });
    when(applicationService.setTargetState(anyInt(), any())).thenReturn(dummApplication);

      dateReportingService.reportWorkFinished(APP_ID, workFinishedDate);



  }

  @Test
  public void updateUustomerLocationValidityUpdatesSupervisionTask() {
    final ApplicationDateReport dateReport = new ApplicationDateReport(ZonedDateTime.now(), ZonedDateTime.now().minusDays(5), ZonedDateTime.now().plusDays(5));
    final SupervisionTaskJson task = new SupervisionTaskJson();
    task.setType(SupervisionTaskType.WORK_TIME_SUPERVISION);
    task.setStatus(SupervisionTaskStatusType.OPEN);
    Mockito.when(supervisionTaskService.findByLocationId(eq(LOC_ID))).thenReturn(Arrays.asList(task));

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
