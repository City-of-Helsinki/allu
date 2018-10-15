package fi.hel.allu.model.service.event.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.service.ApplicationService;
import fi.hel.allu.model.service.ChargeBasisService;
import fi.hel.allu.model.service.LocationService;
import fi.hel.allu.model.service.SupervisionTaskService;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationStatusChangeHandlerTest {

  private static final Integer USER_ID = Integer.valueOf(99);
  private ApplicationStatusChangeHandler statusChangeHandler;
  private Application application;

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

  @Before
  public void setup() {
    statusChangeHandler = new ApplicationStatusChangeHandler(applicationService,
        supervisionTaskService, locationService, applicationDao, chargeBasisService);
    application = createApplication();
  }

  @Test
  public void onDecisionShouldLockChargeBasisEntries() {
    application.setType(ApplicationType.EVENT);
    application.setExtension(new Event());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(chargeBasisService, times(1)).lockEntries(eq(application.getId()));
  }

  @Test
  public void onDecisionShouldCancelDanglingSupervisionTasks() {
    Integer replacedApplicationId = Integer.valueOf(999);
    application.setReplacesApplicationId(replacedApplicationId);
    application.setType(ApplicationType.EVENT);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(supervisionTaskService, times(1)).cancelOpenTasksOfApplication(replacedApplicationId);
  }

  private Application createApplication() {
    application = new Application();
    application.setId(2);
    return application;
  }
}
