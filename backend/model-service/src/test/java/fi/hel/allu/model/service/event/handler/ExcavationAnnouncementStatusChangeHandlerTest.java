package fi.hel.allu.model.service.event.handler;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.DecisionDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.service.*;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExcavationAnnouncementStatusChangeHandlerTest {

  private static final Integer USER_ID = Integer.valueOf(99);
  private ExcavationAnnouncementStatusChangeHandler statusChangeHandler;
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
  @Mock
  private InvoiceService invoiceService;
  @Mock
  private WinterTimeService winterTimeService;

  @Before
  public void setup() {
    statusChangeHandler = new ExcavationAnnouncementStatusChangeHandler(applicationService,
        supervisionTaskService, locationService, applicationDao, chargeBasisService, invoiceService,
        winterTimeService);
    createApplication();
  }

  @Test
  public void onDecisionShouldNotLockChargeBasisEntries() {
    when(winterTimeService.isInWinterTime(any(ZonedDateTime.class))).thenReturn(true);
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setExtension(new ExcavationAnnouncement());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(chargeBasisService, never()).lockEntries(eq(application.getId()));
  }

  @Test
  public void onOperationalConditionShouldLockChargeBasisEntries() {
    when(winterTimeService.isInWinterTime(any(ZonedDateTime.class))).thenReturn(true);
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setExtension(new ExcavationAnnouncement());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(chargeBasisService, times(1)).lockEntries(eq(application.getId()));
  }

  @Test
  public void onFinishedShuoldLockChargeBasisEntries() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setExtension(new ExcavationAnnouncement());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(chargeBasisService, times(1)).lockEntries(eq(application.getId()));
  }

  @Test
  public void onOperationalConditionShouldSetInvoicable() {
    when(winterTimeService.isInWinterTime(any(ZonedDateTime.class))).thenReturn(true);
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    ZonedDateTime operationalConditionDate = LocalDate.parse("2018-12-22").atStartOfDay(TimeUtil.HelsinkiZoneId);
    ExcavationAnnouncement extension = new ExcavationAnnouncement();
    extension.setWinterTimeOperation(operationalConditionDate);
    application.setExtension(extension);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(invoiceService, times(1)).setInvoicableTime(eq(application.getId()), eq(operationalConditionDate));
  }

  @Test
  public void onFinishedShouldSetInvoicable() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    ZonedDateTime workFinishedDate = LocalDate.parse("2019-05-10").atStartOfDay(TimeUtil.HelsinkiZoneId);
    ExcavationAnnouncement extension = new ExcavationAnnouncement();
    extension.setWorkFinished(workFinishedDate);
    application.setExtension(extension);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(invoiceService, times(1)).setInvoicableTime(eq(application.getId()), eq(workFinishedDate));
  }

  private void createApplication() {
    application = new Application();
    application.setId(2);
  }
}
