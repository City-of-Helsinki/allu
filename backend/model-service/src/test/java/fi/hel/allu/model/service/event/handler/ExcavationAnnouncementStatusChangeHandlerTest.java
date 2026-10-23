package fi.hel.allu.model.service.event.handler;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import fi.hel.allu.model.service.chargeBasis.ChargeBasisService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.HistoryDao;
import fi.hel.allu.model.dao.InformationRequestDao;
import fi.hel.allu.model.dao.TerminationDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;
import fi.hel.allu.model.service.*;
import fi.hel.allu.model.service.event.ApplicationStatusChangeEvent;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExcavationAnnouncementStatusChangeHandlerTest {

  private static final Integer USER_ID = Integer.valueOf(99);
  private static final Integer OPERATIONAL_CONDITION_PERIOD_ID = Integer.valueOf(3);
  private static final Integer LAST_OPERATIONAL_CONDITION_PERIOD_ID = Integer.valueOf(1);
  private static final Integer LAST_WORK_FINISHED_PERIOD_ID = Integer.valueOf(6);
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
  private AnnualTimePeriod winterTime;
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
    when(winterTime.getAnnualPeriodStart(any(ZonedDateTime.class))).thenReturn(LocalDate.parse("2019-12-01"));
    when(winterTime.isInAnnualPeriod(any(ZonedDateTime.class))).thenReturn(true);
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
    application.setExtension(extension);
    extension.setWinterTimeOperation(ZonedDateTime.now());
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));
    verify(chargeBasisService, times(1)).lockEntriesOfPeriod(eq(OPERATIONAL_CONDITION_PERIOD_ID));
  }

  @Test
  public void onOperationalConditionShouldRemoveSupervisionDoneTag() {
    extension.setWinterTimeOperation(ZonedDateTime.now());
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
    when(winterTime.isInAnnualPeriod(extension.getWinterTimeOperation())).thenReturn(false);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verifyInvoicingAdjustment(true);
  }

  @Test
  public void onFinishedShouldNotAdjustInvoicingIfInvoiced() {
    when(invoiceService.applicationHasInvoiced(application.getId())).thenReturn(true);
    extension.setWorkFinished(LocalDate.parse("2019-08-31").atStartOfDay(TimeUtil.HelsinkiZoneId));
    extension.setWinterTimeOperation(LocalDate.parse("2019-08-10").atStartOfDay(TimeUtil.HelsinkiZoneId));
    when(winterTime.isInAnnualPeriod(extension.getWinterTimeOperation())).thenReturn(false);
    statusChangeHandler.handleStatusChange(new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));
    verifyInvoicingAdjustment(false);
  }

  @Test
  public void onFinishedShouldNotAdjustInvoicingIfNotSummertimeOperational() {
    extension.setWorkFinished(LocalDate.parse("2019-12-12").atStartOfDay(TimeUtil.HelsinkiZoneId));
    extension.setWinterTimeOperation(LocalDate.parse("2019-12-10").atStartOfDay(TimeUtil.HelsinkiZoneId));
    when(winterTime.isInAnnualPeriod(extension.getWinterTimeOperation())).thenReturn(false);
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

  // --- Monthly invoicing periods ---

  @Test
  public void onOperationalConditionShouldReleaseLastMonthlyPeriodOnDecisionDay() {
    ZonedDateTime operationalConditionDate = LocalDate.parse("2027-02-02").atStartOfDay(TimeUtil.HelsinkiZoneId);
    extension.setWinterTimeOperation(operationalConditionDate);
    mockMonthlyPeriods();

    statusChangeHandler.handleStatusChange(
        new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));

    verify(invoicingPeriodService).findLastOpenPeriodForStatus(application.getId(), StatusType.OPERATIONAL_CONDITION);
    verify(invoiceService).lockInvoicesOfPeriod(LAST_OPERATIONAL_CONDITION_PERIOD_ID);
    verify(invoiceService).setInvoicableTimeForPeriod(eq(LAST_OPERATIONAL_CONDITION_PERIOD_ID), any(ZonedDateTime.class));
    verify(invoicingPeriodService).closeInvoicingPeriod(LAST_OPERATIONAL_CONDITION_PERIOD_ID);
    verify(chargeBasisService).lockEntriesOfPeriod(LAST_OPERATIONAL_CONDITION_PERIOD_ID);
  }

  @Test
  public void onOperationalConditionShouldNotUseReportedDateAsInvoicingDate() {
    ZonedDateTime operationalConditionDate = LocalDate.parse("2027-02-02").atStartOfDay(TimeUtil.HelsinkiZoneId);
    extension.setWinterTimeOperation(operationalConditionDate);
    mockMonthlyPeriods();
    ZonedDateTime before = ZonedDateTime.now();

    statusChangeHandler.handleStatusChange(
        new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));

    ArgumentCaptor<ZonedDateTime> captor = ArgumentCaptor.forClass(ZonedDateTime.class);
    verify(invoiceService).setInvoicableTimeForPeriod(eq(LAST_OPERATIONAL_CONDITION_PERIOD_ID), captor.capture());
    // Invoiced on the decision day, not on the reported operational condition date
    assertNotEquals(operationalConditionDate, captor.getValue());
    assertFalse(captor.getValue().isBefore(before));
  }

  @Test
  public void onOperationalConditionShouldMoveLastPeriodEndToReportedDate() {
    ZonedDateTime operationalConditionDate = LocalDate.parse("2027-02-02").atStartOfDay(TimeUtil.HelsinkiZoneId);
    extension.setWinterTimeOperation(operationalConditionDate);
    mockMonthlyPeriods();

    statusChangeHandler.handleStatusChange(
        new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));

    verify(invoicingPeriodService).updatePeriodEndDate(eq(LAST_OPERATIONAL_CONDITION_PERIOD_ID),
        eq(operationalConditionDate));
  }

  @Test
  public void onOperationalConditionShouldNotMovePeriodEndWhenDateIsUnchanged() {
    ZonedDateTime operationalConditionDate = LocalDate.parse("2027-02-02").atStartOfDay(TimeUtil.HelsinkiZoneId);
    extension.setWinterTimeOperation(operationalConditionDate);
    mockMonthlyPeriodsEndingOn(operationalConditionDate);

    statusChangeHandler.handleStatusChange(
        new ApplicationStatusChangeEvent(this, application, StatusType.OPERATIONAL_CONDITION, USER_ID));

    verify(invoicingPeriodService, never()).updatePeriodEndDate(anyInt(), any(ZonedDateTime.class));
  }

  @Test
  public void onFinishedShouldReleaseLastMonthlyPeriodOnDecisionDay() {
    ZonedDateTime workFinished = LocalDate.parse("2027-06-06").atStartOfDay(TimeUtil.HelsinkiZoneId);
    extension.setWorkFinished(workFinished);
    mockMonthlyPeriods();

    statusChangeHandler.handleStatusChange(
        new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));

    verify(invoicingPeriodService).findLastOpenPeriodForStatus(application.getId(), StatusType.FINISHED);
    verify(invoiceService).lockInvoicesOfPeriod(LAST_WORK_FINISHED_PERIOD_ID);
    verify(invoiceService).setInvoicableTimeForPeriod(eq(LAST_WORK_FINISHED_PERIOD_ID), any(ZonedDateTime.class));
    verify(invoicingPeriodService).closeInvoicingPeriod(LAST_WORK_FINISHED_PERIOD_ID);
  }

  @Test
  public void onFinishedShouldMoveLastPeriodEndToWorkFinishedDate() {
    ZonedDateTime workFinished = LocalDate.parse("2027-06-06").atStartOfDay(TimeUtil.HelsinkiZoneId);
    extension.setWorkFinished(workFinished);
    mockMonthlyPeriods();

    statusChangeHandler.handleStatusChange(
        new ApplicationStatusChangeEvent(this, application, StatusType.FINISHED, USER_ID));

    verify(invoicingPeriodService).updatePeriodEndDate(eq(LAST_WORK_FINISHED_PERIOD_ID), eq(workFinished));
  }

  /**
   * Makes the service report dated monthly periods, so the handler takes the monthly path.
   */
  private void mockMonthlyPeriods() {
    mockMonthlyPeriodsEndingOn(LocalDate.parse("2027-05-31").atStartOfDay(TimeUtil.HelsinkiZoneId));
  }

  private void mockMonthlyPeriodsEndingOn(ZonedDateTime lastOperationalConditionEnd) {
    InvoicingPeriod lastOperationalCondition = new InvoicingPeriod(1,
        LocalDate.parse("2027-02-01").atStartOfDay(TimeUtil.HelsinkiZoneId), lastOperationalConditionEnd);
    lastOperationalCondition.setId(LAST_OPERATIONAL_CONDITION_PERIOD_ID);
    lastOperationalCondition.setInvoicableStatus(StatusType.OPERATIONAL_CONDITION);
    // Ends before the work finished date so the handler has to move it
    InvoicingPeriod lastWorkFinished = datedPeriod(LAST_WORK_FINISHED_PERIOD_ID, "2027-06-01", "2027-06-05",
        StatusType.FINISHED);

    when(invoicingPeriodService.findLastOpenPeriodForStatus(application.getId(), StatusType.OPERATIONAL_CONDITION))
        .thenReturn(Optional.of(lastOperationalCondition));
    when(invoicingPeriodService.findLastOpenPeriodForStatus(application.getId(), StatusType.FINISHED))
        .thenReturn(Optional.of(lastWorkFinished));
  }

  private InvoicingPeriod datedPeriod(int id, String start, String end, StatusType status) {
    InvoicingPeriod period = new InvoicingPeriod(1,
        LocalDate.parse(start).atStartOfDay(TimeUtil.HelsinkiZoneId),
        LocalDate.parse(end).atStartOfDay(TimeUtil.HelsinkiZoneId));
    period.setId(id);
    period.setInvoicableStatus(status);
    return period;
  }

  private void createApplication() {
    application = new Application();
    application.setId(2);
  }
}
