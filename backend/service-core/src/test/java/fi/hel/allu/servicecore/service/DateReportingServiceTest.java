package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AreaRental;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.LocationJson;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DateReportingServiceTest {

  private static final Integer APP_ID = 1234;
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

  private DateReportingService dateReportingService;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    dateReportingService = new DateReportingService(applicationService, applicationJsonService,
        supervisionTaskService, applicationServiceComposer, locationService,
        applicationHistoryService, invoicingPeriodService);

    final List<LocationJson> locations = new ArrayList<>();
    LocationJson location = new LocationJson();
    location.setStartTime(ZonedDateTime.now().minusDays(5));
    location.setEndTime(ZonedDateTime.now());
    locations.add(location);
    final ApplicationJson applicationJson = new ApplicationJson();
    applicationJson.setLocations(locations);
    applicationJson.setId(APP_ID);
    Application application = new Application();
    application.setId(APP_ID);
    application.setExtension(new AreaRental());

    Mockito.when(applicationJsonService.getFullyPopulatedApplication(Mockito.anyObject())).thenReturn(applicationJson);
    Mockito.when(applicationService.setTargetState(APP_ID, StatusType.FINISHED)).thenReturn(application);
  }

  @Test
  public void workFinishedDateCantBeBeforeAreaStartDate() {
    Mockito.when(invoicingPeriodService.getInvoicingPeriods(Mockito.anyObject())).thenReturn(Collections.emptyList());
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("workfinisheddate.before.area.start");

    final ZonedDateTime workFinishedDate = ZonedDateTime.now().minusDays(10);
    dateReportingService.reportWorkFinished(APP_ID, workFinishedDate);
  }

  @Test
  public void workFinishedDateIsOnInvoicedPeriod() {
    final List<InvoicingPeriod> invoicingPeriods = new ArrayList<>();
    final InvoicingPeriod period = new InvoicingPeriod(APP_ID, ZonedDateTime.now().minusDays(10), ZonedDateTime.now().plusDays(10));
    period.setInvoiced(true);
    invoicingPeriods.add(period);
    Mockito.when(invoicingPeriodService.getInvoicingPeriods(Mockito.anyObject())).thenReturn(invoicingPeriods);
    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("workfinisheddate.invoiced.invoicing.period");

    final ZonedDateTime workFinishedDate = ZonedDateTime.now().minusDays(10);
    dateReportingService.reportWorkFinished(APP_ID, workFinishedDate);
  }
}
