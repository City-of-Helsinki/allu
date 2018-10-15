package fi.hel.allu.model.service.event.handler;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.*;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TrafficArrangementStatusChangeHandlerTest {

  private static final Integer USER_ID = Integer.valueOf(99);

  private TrafficArrangementStatusChangeHandler statusChangeHandler;
  private Application application;
  private Location location;
  private User supervisor;

  @Mock
  private LocationService locationService;
  @Mock
  private DecisionDao decisionDao;
  @Mock
  private SupervisionTaskService supervisionTaskService;
  @Mock
  private ApplicationService applicationService;
  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private ChargeBasisService chargeBasisService;
  @Mock
  private InvoiceService invoiceService;
  @Mock
  private WinterTimeService winterTimeService;

  @Captor
  ArgumentCaptor<SupervisionTask> supervisionTaskCaptor;

  @Before
  public void setup() {
    supervisor = new User();
    supervisor.setId(228);
    createApplicationWithLocation();
    statusChangeHandler = new TrafficArrangementStatusChangeHandler(applicationService,
        supervisionTaskService, locationService, applicationDao, chargeBasisService);
    when(locationService.findSupervisionTaskOwner(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS,
        location.getCityDistrictId())).thenReturn(Optional.of(supervisor));
  }

  @Test
  public void onDecisionShouldCreateSupervisionTaskForTrafficArrangement() {
    application.setEndTime(currentDate());
    application.setType(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(supervisionTaskService, times(1)).insert(supervisionTaskCaptor.capture());
    SupervisionTask insertedTask = supervisionTaskCaptor.getValue();

    assertEquals(application.getId(), insertedTask.getApplicationId());
    assertEquals(USER_ID, insertedTask.getCreatorId());
    assertEquals(supervisor.getId(), insertedTask.getOwnerId());
    assertEquals(SupervisionTaskStatusType.OPEN, insertedTask.getStatus());
    assertEquals(SupervisionTaskType.FINAL_SUPERVISION, insertedTask.getType());
    // Supervision task should be one day after application end time.
    assertEquals(application.getEndTime().plusDays(1), insertedTask.getPlannedFinishingTime());
  }

  private void createApplicationWithLocation() {
    location = new Location();
    location.setCityDistrictId(2);
    application = new Application();
    application.setId(2);
    application.setLocations(Collections.singletonList(location));
  }

  private static ZonedDateTime currentDate() {
    return TimeUtil.startOfDay(ZonedDateTime.now());
  }


}
