package fi.hel.allu.model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
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
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;
import fi.hel.allu.model.service.event.ApplicationStatusChangeListener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationStatusChangeEventListenerTest {


  private static final Integer USER_ID = Integer.valueOf(99);
  private static final Integer PLACEMENT_CONTRACT_SECTION_NR = Integer.valueOf(2432);
  private ApplicationStatusChangeListener statusChangeListener;
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


  @Captor
  ArgumentCaptor<Application> applicationCaptor;
  @Captor
  ArgumentCaptor<List<Location>> locationListCaptor;
  @Captor
  ArgumentCaptor<SupervisionTask> supervisionTaskCaptor;


  @Before
  public void setup() {
    supervisor = new User();
    supervisor.setId(228);
    statusChangeListener = new ApplicationStatusChangeListener(decisionDao, applicationService, locationService,
        supervisionTaskService, applicationDao, chargeBasisService, invoiceService);
    createApplicationWithLocation();
    when(locationService.findSingleByApplicationId(application.getId())).thenReturn(location);
    when(decisionDao.getPlacementContractSectionNumber()).thenReturn(PLACEMENT_CONTRACT_SECTION_NR);
    when(locationService.findSupervisionTaskOwner(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS,
        location.getCityDistrictId())).thenReturn(Optional.of(supervisor));
  }

  @Test
  public void onDecisionShouldUpdatePlacementContractDates() {
    application.setType(ApplicationType.PLACEMENT_CONTRACT);
    application.setExtension(new PlacementContract());
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(locationService, times(1)).updateApplicationLocations(eq(application.getId()), locationListCaptor.capture(), eq(USER_ID));
    assertTrue(isCurrentDate(locationListCaptor.getValue().get(0).getStartTime()));
    assertTrue(isYearAfterCurrentDate(locationListCaptor.getValue().get(0).getEndTime()));
  }

  @Test
  public void onDecisionShouldLockChargeBasisEntries() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setExtension(new ExcavationAnnouncement());
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(chargeBasisService, times(1)).lockEntries(eq(application.getId()));
  }

  @Test
  public void onOperationalConditionShouldLockChargeBasisEntries() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setExtension(new ExcavationAnnouncement());
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(chargeBasisService, times(1)).lockEntries(eq(application.getId()));
  }

  @Test
  public void onOperationalConditionShouldSetInvoicable() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    ZonedDateTime operationalConditionDate = LocalDate.parse("2018-12-22").atStartOfDay(TimeUtil.HelsinkiZoneId);
    ExcavationAnnouncement extension = new ExcavationAnnouncement();
    extension.setWinterTimeOperation(operationalConditionDate);
    application.setExtension(extension);
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(invoiceService, times(1)).setInvoicableTime(eq(application.getId()), eq(operationalConditionDate));
  }

  @Test
  public void onFinishedShouldSetInvoicable() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    ZonedDateTime workFinishedDate = LocalDate.parse("2019-05-10").atStartOfDay(TimeUtil.HelsinkiZoneId);
    ExcavationAnnouncement extension = new ExcavationAnnouncement();
    extension.setWorkFinished(workFinishedDate);
    application.setExtension(extension);
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(invoiceService, times(1)).setInvoicableTime(eq(application.getId()), eq(workFinishedDate));
  }

  @Test
  public void onDecisionShouldUpdatePlacementContractSectionNr() {
    application.setType(ApplicationType.PLACEMENT_CONTRACT);
    application.setExtension(new PlacementContract());
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(applicationService, times(1)).update(eq(application.getId()), applicationCaptor.capture(), eq(USER_ID));
    PlacementContract pc = (PlacementContract)applicationCaptor.getValue().getExtension();
    assertEquals(PLACEMENT_CONTRACT_SECTION_NR, pc.getSectionNumber());
  }

  @Test
  public void onDecisionShouldCreateSupervisionTaskForTrafficArrangement() {
    application.setEndTime(currentDate());
    application.setType(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS);
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
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

  @Test
  public void onDecisionShouldCancelDanglingSupervisionTasks() {
    Integer replacedApplicationId = Integer.valueOf(999);
    application.setReplacesApplicationId(replacedApplicationId);
    application.setType(ApplicationType.EVENT);
    statusChangeListener.onApplicationStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(supervisionTaskService, times(1)).cancelOpenTasksOfApplication(replacedApplicationId);
  }

  private void createApplicationWithLocation() {
    location = new Location();
    location.setCityDistrictId(2);
    application = new Application();
    application.setId(2);
    application.setLocations(Collections.singletonList(location));
  }

  private boolean isYearAfterCurrentDate(ZonedDateTime time) {
    return Duration.between(currentDate().plusYears(1), time).toDays() == 0;
  }

  private boolean isCurrentDate(ZonedDateTime time) {
    return Duration.between(currentDate(), time).toDays() == 0;
  }

  private static ZonedDateTime currentDate() {
    return TimeUtil.startOfDay(ZonedDateTime.now());
  }
}
