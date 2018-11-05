package fi.hel.allu.model.service.event.handler;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskStatusType;
import fi.hel.allu.common.domain.types.SupervisionTaskType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.SupervisionTask;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class AreaRentalStatusChangeHandlerTest {

  private static final Integer USER_ID = 99;

  private AreaRentalStatusChangeHandler statusChangeHandler;
  private Application application;
  private Location location1;
  private Location location2;
  private Map<Integer, Location> locations;
  private User supervisor;

  @Mock
  private ApplicationService applicationService;
  @Mock
  private SupervisionTaskService supervisionTaskService;
  @Mock
  private LocationService locationService;
  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private ChargeBasisService chargeBasisService;
  @Mock
  private HistoryDao historyDao;
  @Captor
  ArgumentCaptor<SupervisionTask> supervisionTaskCaptor;

  @Before
  public void setup() {
    supervisor = new User();
    supervisor.setId(228);
    createApplicationWithLocations();
    statusChangeHandler = new AreaRentalStatusChangeHandler(applicationService,
        supervisionTaskService, locationService, applicationDao, chargeBasisService, historyDao);
    when(locationService.findSupervisionTaskOwner(ApplicationType.AREA_RENTAL,
        location1.getCityDistrictId())).thenReturn(Optional.of(supervisor));
  }

  @Test
  public void onDecisionShouldCreateSupervisionTaskForAreaRental() {
    application.setEndTime(location2.getEndTime());
    application.setType(ApplicationType.AREA_RENTAL);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(supervisionTaskService, times(3)).insert(supervisionTaskCaptor.capture());
    final List<SupervisionTask> insertedTasks = supervisionTaskCaptor.getAllValues();

    final List<SupervisionTask> finalTasks = insertedTasks.stream()
        .filter(t -> t.getType() == SupervisionTaskType.FINAL_SUPERVISION).collect(Collectors.toList());
    assertEquals(1, finalTasks.size());
    final SupervisionTask finalTask = finalTasks.get(0);
    assertEquals(application.getId(), finalTask.getApplicationId());
    assertEquals(USER_ID, finalTask.getCreatorId());
    assertEquals(supervisor.getId(), finalTask.getOwnerId());
    assertEquals(SupervisionTaskStatusType.OPEN, finalTask.getStatus());
    assertEquals(application.getEndTime().plusDays(1), finalTask.getPlannedFinishingTime());

    final List<SupervisionTask> workTimeTasks = insertedTasks.stream()
        .filter(t -> t.getType() == SupervisionTaskType.WORK_TIME_SUPERVISION).collect(Collectors.toList());
    assertEquals(2, workTimeTasks.size());

    for (SupervisionTask task : workTimeTasks) {
      assertEquals(application.getId(), task.getApplicationId());
      assertEquals(USER_ID, finalTask.getCreatorId());
      assertEquals(supervisor.getId(), finalTask.getOwnerId());
      assertEquals(SupervisionTaskStatusType.OPEN, finalTask.getStatus());
      assertNotNull("Task must have locationId", task.getLocationId());
      assertEquals(locations.get(task.getLocationId()).getEndTime().plusDays(1), task.getPlannedFinishingTime());
    }
  }

  private void createApplicationWithLocations() {
    locations = new HashMap<>();
    location1 = new Location();
    location1.setId(1);
    location1.setCityDistrictId(2);
    locations.put(location1.getId(), location1);
    location1.setEndTime(TimeUtil.endOfDay(ZonedDateTime.now()));
    location2 = new Location();
    location2.setId(2);
    location2.setCityDistrictId(2);
    location2.setEndTime(TimeUtil.endOfDay(ZonedDateTime.now().plusDays(1)));
    locations.put(location2.getId(), location2);
    application = new Application();
    application.setId(2);
    application.setLocations(Arrays.asList(location1, location2));
  }
}
