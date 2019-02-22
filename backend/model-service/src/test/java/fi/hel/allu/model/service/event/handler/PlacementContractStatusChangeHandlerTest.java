package fi.hel.allu.model.service.event.handler;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import fi.hel.allu.model.dao.InformationRequestDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.model.domain.PlacementContract;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlacementContractStatusChangeHandlerTest {

  private static final Integer USER_ID = Integer.valueOf(99);
  private static final Integer PLACEMENT_CONTRACT_SECTION_NR = Integer.valueOf(2432);
  private PlacementContractStatusChangeHandler statusChangeHandler;
  private Application application;
  private Location location;

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
  private HistoryDao historyDao;
  @Mock
  private InformationRequestDao informationRequestDao;


  @Captor
  ArgumentCaptor<Application> applicationCaptor;
  @Captor
  ArgumentCaptor<List<Location>> locationListCaptor;

  @Before
  public void setup() {
    statusChangeHandler = new PlacementContractStatusChangeHandler(applicationService, supervisionTaskService,
        locationService, applicationDao, chargeBasisService, historyDao, informationRequestDao, decisionDao);
    createApplicationWithLocation();
    when(locationService.findSingleByApplicationId(application.getId())).thenReturn(location);
    when(decisionDao.getPlacementContractSectionNumber()).thenReturn(PLACEMENT_CONTRACT_SECTION_NR);
  }

  @Test
  public void onDecisionShouldUpdatePlacementContractDates() {
    application.setType(ApplicationType.PLACEMENT_CONTRACT);
    application.setExtension(new PlacementContract());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(locationService, times(1)).updateApplicationLocations(eq(application.getId()), locationListCaptor.capture(), eq(USER_ID));
    assertTrue(isCurrentDate(locationListCaptor.getValue().get(0).getStartTime()));
    assertTrue(isYearAfterCurrentDate(locationListCaptor.getValue().get(0).getEndTime()));
  }

  @Test
  public void onDecisionShouldUpdatePlacementContractSectionNr() {
    application.setType(ApplicationType.PLACEMENT_CONTRACT);
    application.setExtension(new PlacementContract());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(applicationService, times(1)).update(eq(application.getId()), applicationCaptor.capture(), eq(USER_ID));
    PlacementContract pc = (PlacementContract)applicationCaptor.getValue().getExtension();
    assertEquals(PLACEMENT_CONTRACT_SECTION_NR, pc.getSectionNumber());
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
