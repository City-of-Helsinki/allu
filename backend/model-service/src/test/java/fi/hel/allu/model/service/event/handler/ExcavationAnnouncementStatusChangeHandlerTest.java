package fi.hel.allu.model.service.event.handler;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.common.util.WinterTime;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.*;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExcavationAnnouncementStatusChangeHandlerTest {

  private static final Integer USER_ID = Integer.valueOf(99);
  private static final Integer OPERATIONAL_CONDITION_PERIOD_ID = Integer.valueOf(3);
  private ExcavationAnnouncementStatusChangeHandler statusChangeHandler;
  private Application application;
  private ExcavationAnnouncement extension;

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
  private InvoiceService invoiceService;
  @Mock
  private WinterTimeService winterTimeService;
  @Mock
  private WinterTime winterTime;
  @Mock
  private HistoryDao historyDao;
  @Mock
  private InformationRequestDao informationRequestDao;
  @Mock
  private TerminationDao terminationDao;
  @Mock
  private InvoicingPeriodService invoicingPeriodService;

  @Before
  public void setup() {
    statusChangeHandler = new ExcavationAnnouncementStatusChangeHandler(applicationService,
        supervisionTaskService, locationService, applicationDao, chargeBasisService, historyDao,
        informationRequestDao, invoiceService, winterTimeService, terminationDao, invoicingPeriodService);
    createApplication();
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    extension = new ExcavationAnnouncement();
    application.setExtension(extension);
    when(winterTimeService.getWinterTime()).thenReturn(winterTime);
    when(winterTime.getWinterTimeStart(any(ZonedDateTime.class))).thenReturn(LocalDate.parse("2019-12-01"));
    when(winterTime.isInWinterTime(any(ZonedDateTime.class))).thenReturn(true);
    InvoicingPeriod operationalConditionPeriod = new InvoicingPeriod(1, StatusType.OPERATIONAL_CONDITION);
    InvoicingPeriod workFinishedPeriod = new InvoicingPeriod(2, StatusType.FINISHED);
    operationalConditionPeriod.setId(OPERATIONAL_CONDITION_PERIOD_ID);

    when(invoicingPeriodService.findOpenPeriodsForApplicationId(anyInt()))
    .thenReturn(Arrays.asList(workFinishedPeriod, operationalConditionPeriod));
  }

  @Test
  public void onDecisionShouldNotLockChargeBasisEntries() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(chargeBasisService, never()).lockEntries(eq(application.getId()));
  }

  @Test
  public void onDecisionShouldRemoveSupervisionDoneTag() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.DECISION, USER_ID));
    verify(applicationService, times(1)).removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  @Test
  public void onOperationalConditionShouldLockChargeBasisEntriesForPeriod() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setExtension(new ExcavationAnnouncement());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(chargeBasisService, times(1)).lockEntriesOfPeriod(eq(OPERATIONAL_CONDITION_PERIOD_ID));
  }

  @Test
  public void onOperationalConditionShouldRemoveSupervisionDoneTag() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(applicationService, times(1)).removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  @Test
  public void onFinishedShuoldLockChargeBasisEntries() {
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(chargeBasisService, times(1)).lockEntries(eq(application.getId()));
  }

  @Test
  public void onOperationalConditionShouldSetPeriodInvoicable() {
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    ZonedDateTime operationalConditionDate = LocalDate.parse("2018-12-22").atStartOfDay(TimeUtil.HelsinkiZoneId);
    ExcavationAnnouncement extension = new ExcavationAnnouncement();
    extension.setWinterTimeOperation(operationalConditionDate);
    application.setExtension(extension);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(invoiceService, times(1)).setInvoicableTimeForPeriod(eq(OPERATIONAL_CONDITION_PERIOD_ID), eq(operationalConditionDate));
  }

  @Test
  public void onFinishedShouldSetInvoicable() {
    extension.setWorkFinished(LocalDate.parse("2019-05-10").atStartOfDay(TimeUtil.HelsinkiZoneId));
    application.setExtension(extension);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(invoiceService, times(1)).setInvoicableTime(eq(application.getId()), eq(extension.getWorkFinished()));
  }

  @Test
  public void onFinishedShouldRemoveSupervisionDoneTag() {
    extension.setWorkFinished(LocalDate.parse("2019-05-10").atStartOfDay(TimeUtil.HelsinkiZoneId));
    application.setExtension(extension);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verify(applicationService, times(1)).removeTag(application.getId(), ApplicationTagType.SUPERVISION_DONE);
  }

  @Test
  public void onFinishedShouldAdjustInvoicingForSummertimeOperational() {
    extension.setWorkFinished(LocalDate.parse("2019-08-31").atStartOfDay(TimeUtil.HelsinkiZoneId));
    extension.setWinterTimeOperation(LocalDate.parse("2019-08-10").atStartOfDay(TimeUtil.HelsinkiZoneId));
    when(winterTime.isInWinterTime(extension.getWinterTimeOperation())).thenReturn(false);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verifyInvoicingAdjustment(true);
  }

  @Test
  public void onFinishedShouldNotAdjustInvoicingIfInvoiced() {
    when(invoiceService.applicationHasInvoiced(application.getId())).thenReturn(true);
    extension.setWorkFinished(LocalDate.parse("2019-08-31").atStartOfDay(TimeUtil.HelsinkiZoneId));
    extension.setWinterTimeOperation(LocalDate.parse("2019-08-10").atStartOfDay(TimeUtil.HelsinkiZoneId));
    when(winterTime.isInWinterTime(extension.getWinterTimeOperation())).thenReturn(false);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verifyInvoicingAdjustment(false);
  }

  @Test
  public void onFinishedShouldNotAdjustInvoicingIfNotSummertimeOperational() {
    when(invoiceService.applicationHasInvoiced(application.getId())).thenReturn(true);
    extension.setWorkFinished(LocalDate.parse("2019-12-12").atStartOfDay(TimeUtil.HelsinkiZoneId));
    extension.setWinterTimeOperation(LocalDate.parse("2019-12-10").atStartOfDay(TimeUtil.HelsinkiZoneId));
    when(winterTime.isInWinterTime(extension.getWinterTimeOperation())).thenReturn(false);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verifyInvoicingAdjustment(false);
  }

  private void verifyInvoicingAdjustment(boolean shouldAdjust) {
    if (shouldAdjust) {
      verify(applicationService, times(1)).updateChargeBasis(application.getId());
      verify(invoiceService, times(1)).updateInvoiceRows(application.getId());
    } else {
      verify(applicationService, never()).updateChargeBasis(anyInt());
      verify(invoiceService, never()).updateInvoiceRows(anyInt());
    }
  }

  private void createApplication() {
    application = new Application();
    application.setId(2);
  }
}
