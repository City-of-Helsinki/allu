package fi.hel.allu.model.service.event.handler;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Event;
import fi.hel.allu.model.service.*;
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
  @Mock
  private InvoiceService invoiceService;

  @Before
  public void setup() {
    statusChangeHandler = new ApplicationStatusChangeHandler(applicationService,
        supervisionTaskService, locationService, applicationDao, chargeBasisService,
        historyDao, informationRequestDao, invoiceService);
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

  @Test
  public void onDecisionUpdatesReplacedApplicationStatus() {
    Integer replacedApplicationId = Integer.valueOf(999);
    application.setReplacesApplicationId(replacedApplicationId);
    application.setType(ApplicationType.EVENT);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    Mockito.verify(applicationDao).updateStatus(replacedApplicationId, StatusType.REPLACED);
  }

  @Test
  public void onDecisionShouldRemoveSupervisionDoneTag() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(applicationService, times(1)).removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  @Test
  public void onCancelShouldCancelOpenSupervisionTasks() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.CANCELLED, USER_ID));
    verify(supervisionTaskService, times(1)).cancelOpenTasksOfApplication(application.getId());
  }

  @Test
  public void onCancelShouldCloseInformationRequests() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.CANCELLED, USER_ID));
    verify(informationRequestDao, times(1)).closeInformationRequestOf(application.getId());
  }

  @Test
  public void onCancelShouldRemoveOpenInvoices() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.CANCELLED, USER_ID));
    verify(invoiceService, times(1)).deleteUninvoicedInvoices(application.getId());
  }

  @Test
  public void onFinishedShouldClearOwner() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(applicationDao, times(1)).removeOwner(eq(Collections.singletonList(application.getId())));
  }

  @Test
  public void onFinishedShouldRemoveSupervisionDoneTag() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(applicationService, times(1)).removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }


  private Application createApplication() {
    application = new Application();
    application.setId(2);
    return application;
  }
}
