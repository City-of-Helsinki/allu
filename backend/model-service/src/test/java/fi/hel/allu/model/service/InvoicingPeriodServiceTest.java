package fi.hel.allu.model.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.util.AnnualTimePeriod;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.dao.ApplicationDao;
import fi.hel.allu.model.dao.ChargeBasisDao;
import fi.hel.allu.model.dao.InvoicingPeriodDao;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.model.domain.InvoicingPeriod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class InvoicingPeriodServiceTest {


  private static final Integer APPLICATION_ID = 99;
  @Mock
  private InvoicingPeriodDao invoicingPeriodDao;
  @Mock
  private ApplicationDao applicationDao;
  @Mock
  private ChargeBasisDao chargeBasisDao;
  @Mock
  private WinterTimeService winterTimeService;
  @Mock
  private ApplicationEventPublisher eventPublisher;

  private static final ZonedDateTime START_TIME = LocalDate.parse("2018-01-01").atStartOfDay(TimeUtil.HelsinkiZoneId);
  private static final ZonedDateTime END_TIME = LocalDate.parse("2018-10-04").atStartOfDay(TimeUtil.HelsinkiZoneId);


  private InvoicingPeriodService invoicingPeriodService;

  @Before
  public void setup() {
    invoicingPeriodService = new InvoicingPeriodService(invoicingPeriodDao, applicationDao, chargeBasisDao,
        eventPublisher, winterTimeService);
    when(invoicingPeriodDao.insertPeriods(anyList())).then(AdditionalAnswers.returnsFirstArg());
    Application application = new Application();
    application.setStartTime(START_TIME);
    application.setEndTime(END_TIME);
    when(invoicingPeriodDao.insertPeriods(anyList())).then(AdditionalAnswers.returnsFirstArg());
    when(applicationDao.findById(APPLICATION_ID)).thenReturn(application);
  }

  @Test
  public void shouldCreatePeriods() {
    List<InvoicingPeriod> result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 3);
    validatePeriods(result, 4);
    result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 1);
    validatePeriods(result, 10);
    result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 6);
    validatePeriods(result, 2);
    result = invoicingPeriodService.createInvoicingPeriods(APPLICATION_ID, 12);
    validatePeriods(result, 1);
  }

  @Test
  public void shouldCreateRecurringPeriods() {
    Application application = applicationDao.findById(APPLICATION_ID);
    application.setRecurringEndTime(END_TIME.plusYears(2));
    // Check if period affects, even if it should be null. END_TIME - START_TIME difference should be 9 months.
    application.setInvoicingPeriodLength(5);
    List<InvoicingPeriod> result = invoicingPeriodService.createRecurringApplicationPeriods(APPLICATION_ID);
    // Not using validatePeriods() method, as it asserts if periods have 1 day between
    assertEquals(3, result.size());
  }

  @Test
  public void shouldNotUpdateClosedPeriods() {
    List<InvoicingPeriod> existingPeriods = new ArrayList<>();
    existingPeriods.add(new InvoicingPeriod(APPLICATION_ID, START_TIME, START_TIME.plusMonths(6).minusDays(1)));
    existingPeriods.add(new InvoicingPeriod(APPLICATION_ID, START_TIME.plusMonths(6), END_TIME));
    existingPeriods.get(0).setClosed(true);
    when(invoicingPeriodDao.findForApplicationId(APPLICATION_ID)).thenReturn(existingPeriods);
    invoicingPeriodService.updateInvoicingPeriods(APPLICATION_ID, 6);
    verify(invoicingPeriodDao, never()).insertInvoicingPeriod(any(InvoicingPeriod.class));
    verify(invoicingPeriodDao, never()).deletePeriods(any(Integer.class));
  }

  // --- Monthly invoicing periods for excavation announcements ---

  @Test
  public void shouldCreateMonthlyPeriodsForExcavationAnnouncementStartingAfterMonthlyPricing() {
    Application application = excavationAnnouncement("2026-04-01", "2026-06-30", null);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();
    mockNoExistingPeriods();

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    List<InvoicingPeriod> inserted = captureInsertedPeriods();
    assertEquals(3, inserted.size());
    assertPeriod(inserted.get(0), "2026-04-01", "2026-04-30", StatusType.FINISHED);
    assertPeriod(inserted.get(1), "2026-05-01", "2026-05-31", StatusType.FINISHED);
    assertPeriod(inserted.get(2), "2026-06-01", "2026-06-30", StatusType.FINISHED);
  }

  @Test
  public void shouldSplitPeriodsIntoOperationalConditionAndWorkFinishedPhases() {
    Application application = excavationAnnouncement("2026-11-11", "2027-06-06", "2027-02-02");
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();
    mockNoExistingPeriods();

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    List<InvoicingPeriod> inserted = captureInsertedPeriods();
    assertTrue(inserted.stream().anyMatch(p -> p.getInvoicableStatus() == StatusType.OPERATIONAL_CONDITION));
    assertTrue(inserted.stream().anyMatch(p -> p.getInvoicableStatus() == StatusType.FINISHED));
    assertFalse(inserted.stream().anyMatch(p -> p.getStartTime() == null));
  }

  @Test
  public void shouldUseLegacyPeriodsForExcavationAnnouncementStartingBeforeMonthlyPricing() {
    Application application = excavationAnnouncement("2026-02-01", "2026-06-30", null);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockNoExistingPeriods();

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    ArgumentCaptor<InvoicingPeriod> captor = ArgumentCaptor.forClass(InvoicingPeriod.class);
    verify(invoicingPeriodDao).insertInvoicingPeriod(captor.capture());
    assertEquals(StatusType.FINISHED, captor.getValue().getInvoicableStatus());
    assertEquals(null, captor.getValue().getStartTime());
    verify(invoicingPeriodDao, never()).insertPeriods(anyList());
  }

  @Test
  public void shouldUseLegacyPeriodsForAlreadyDecidedExcavationAnnouncement() {
    Application application = excavationAnnouncement("2026-04-01", "2026-06-30", null);
    application.setStatus(StatusType.DECISION);
    mockApplication(application);
    mockNoExistingPeriods();

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    verify(invoicingPeriodDao).insertInvoicingPeriod(any(InvoicingPeriod.class));
    verify(invoicingPeriodDao, never()).insertPeriods(anyList());
  }

  @Test
  public void shouldKeepMonthlyPeriodsForDecidedApplicationThatAlreadyHasDatedPeriods() {
    Application application = excavationAnnouncement("2026-04-01", "2026-06-30", null);
    application.setStatus(StatusType.FINISHED);
    mockApplication(application);
    mockWinterTime();
    mockExistingPeriods(List.of(datedPeriod("2026-04-01", "2026-05-31", StatusType.FINISHED)));

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    // Periods change, so they are reconciled instead of falling back to the legacy model
    verify(invoicingPeriodDao, never()).deletePeriods(APPLICATION_ID);
    verify(invoicingPeriodDao).deletePeriod(any());
    List<InvoicingPeriod> inserted = captureInsertedPeriods();
    assertEquals(3, inserted.size());
    assertPeriod(inserted.get(0), "2026-04-01", "2026-04-30", StatusType.FINISHED);
    assertPeriod(inserted.get(1), "2026-05-01", "2026-05-31", StatusType.FINISHED);
    assertPeriod(inserted.get(2), "2026-06-01", "2026-06-30", StatusType.FINISHED);
  }

  @Test
  public void shouldNotRecreateMonthlyPeriodsWhenTheyAreUnchanged() {
    Application application = excavationAnnouncement("2026-04-01", "2026-05-31", null);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();
    mockExistingPeriods(List.of(
        datedPeriod("2026-04-01", "2026-04-30", StatusType.FINISHED),
        datedPeriod("2026-05-01", "2026-05-31", StatusType.FINISHED)));

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    verify(invoicingPeriodDao, never()).deletePeriods(APPLICATION_ID);
    verify(invoicingPeriodDao, never()).insertPeriods(anyList());
    verify(eventPublisher, never()).publishEvent(any());
  }
  @Test
  public void shouldKeepInvoicedPeriodAndReconcileOtherPeriods() {
    Application application = excavationAnnouncement("2026-04-01", "2026-06-30", null);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();

    // Period A is closed (invoice sent), B and C are still open
    InvoicingPeriod periodA = datedPeriod("2026-04-01", "2026-04-30", StatusType.FINISHED);
    periodA.setId(1);
    periodA.setClosed(true);
    InvoicingPeriod periodB = datedPeriod("2026-05-01", "2026-05-31", StatusType.FINISHED);
    periodB.setId(2);
    InvoicingPeriod periodC = datedPeriod("2026-06-01", "2026-06-30", StatusType.FINISHED);
    periodC.setId(3);
    mockExistingPeriods(List.of(periodA, periodB, periodC));

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    // Nothing changed in the desired periods, so no period is touched
    verify(invoicingPeriodDao, never()).deletePeriod(anyInt());
    verify(invoicingPeriodDao, never()).insertPeriods(anyList());
    verify(eventPublisher, never()).publishEvent(any());
  }

  @Test
  public void shouldNotDeleteInvoicedPeriodWhenItNoLongerMatches() {
    Application application = excavationAnnouncement("2026-04-01", "2026-06-30", null);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();

    // Period A ended on 15.4. but has locked entries, so it must be preserved
    InvoicingPeriod periodA = datedPeriod("2026-04-01", "2026-04-15", StatusType.FINISHED);
    periodA.setId(1);
    mockExistingPeriods(List.of(periodA));
    when(chargeBasisDao.getLockedEntriesOfPeriod(1)).thenReturn(List.of(10));

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    verify(invoicingPeriodDao, never()).deletePeriod(eq(1));
    // The missing periods are still created
    List<InvoicingPeriod> inserted = captureInsertedPeriods();
    assertTrue(inserted.stream().anyMatch(p -> TimeUtil.isSameDate(p.getStartTime(), date("2026-04-01"))));
  }

  @Test
  public void shouldReplaceUninvoicedPeriodThatNoLongerMatches() {
    Application application = excavationAnnouncement("2026-04-01", "2026-06-30", null);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();

    // Period B no longer matches the desired month boundary and holds no invoice
    InvoicingPeriod periodB = datedPeriod("2026-04-01", "2026-04-15", StatusType.FINISHED);
    periodB.setId(2);
    mockExistingPeriods(List.of(periodB));
    when(chargeBasisDao.getLockedEntriesOfPeriod(2)).thenReturn(List.of());

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    verify(invoicingPeriodDao).removeUnlockedEntriesFromPeriod(2);
    verify(invoicingPeriodDao).deletePeriod(2);
    List<InvoicingPeriod> inserted = captureInsertedPeriods();
    assertEquals(3, inserted.size());
  }

  @Test
  public void shouldPreservePeriodIdsOfUnchangedPeriods() {
    Application application = excavationAnnouncement("2026-04-01", "2026-06-30", null);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();

    InvoicingPeriod unchanged = datedPeriod("2026-04-01", "2026-04-30", StatusType.FINISHED);
    unchanged.setId(1);
    InvoicingPeriod stale = datedPeriod("2026-05-01", "2026-05-15", StatusType.FINISHED);
    stale.setId(2);
    mockExistingPeriods(List.of(unchanged, stale));

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    // Only the stale period is replaced; the unchanged one keeps its id
    verify(invoicingPeriodDao, never()).deletePeriod(eq(1));
    verify(invoicingPeriodDao).deletePeriod(eq(2));
    List<InvoicingPeriod> inserted = captureInsertedPeriods();
    assertEquals(2, inserted.size());
    assertPeriod(inserted.get(0), "2026-05-01", "2026-05-31", StatusType.FINISHED);
    assertPeriod(inserted.get(1), "2026-06-01", "2026-06-30", StatusType.FINISHED);
  }

  @Test
  public void shouldCreateOnlyHandlingFeePeriodsWhenNoAreaUsageFee() {
    Application application = excavationAnnouncement("2026-04-01", "2026-10-31", null);
    ((ExcavationAnnouncement) application.getExtension()).setNoAreaUsageFee(true);
    application.setStatus(StatusType.HANDLING);
    mockApplication(application);
    mockWinterTime();
    mockNoExistingPeriods();

    invoicingPeriodService.setExcavationAnnouncementPeriods(APPLICATION_ID);

    List<InvoicingPeriod> inserted = captureInsertedPeriods();
    // First period for the first handling fee part and the six month date period for the second
    assertEquals(2, inserted.size());
    assertPeriod(inserted.get(0), "2026-04-01", "2026-04-30", StatusType.FINISHED);
    assertPeriod(inserted.get(1), "2026-10-01", "2026-10-31", StatusType.FINISHED);
  }

  // --- Final period helpers ---

  @Test
  public void shouldFindLastOpenPeriodForStatus() {
    mockExistingPeriods(List.of(
        datedPeriod("2026-04-01", "2026-04-30", StatusType.OPERATIONAL_CONDITION),
        datedPeriod("2026-05-01", "2026-05-02", StatusType.OPERATIONAL_CONDITION),
        datedPeriod("2026-06-01", "2026-06-30", StatusType.FINISHED)));
    when(invoicingPeriodDao.findOpenPeriodsForApplicationId(APPLICATION_ID))
        .thenReturn(List.of(
            datedPeriod("2026-04-01", "2026-04-30", StatusType.OPERATIONAL_CONDITION),
            datedPeriod("2026-05-01", "2026-05-02", StatusType.OPERATIONAL_CONDITION),
            datedPeriod("2026-06-01", "2026-06-30", StatusType.FINISHED)));

    InvoicingPeriod period = invoicingPeriodService
        .findLastOpenPeriodForStatus(APPLICATION_ID, StatusType.OPERATIONAL_CONDITION).orElseThrow();

    assertEquals(date("2026-05-01"), period.getStartTime());
  }

  @Test
  public void shouldReturnEmptyLastOpenPeriodWhenPhaseHasNoOpenPeriods() {
    when(invoicingPeriodDao.findOpenPeriodsForApplicationId(APPLICATION_ID))
        .thenReturn(List.of(datedPeriod("2026-06-01", "2026-06-30", StatusType.FINISHED)));

    assertTrue(invoicingPeriodService
        .findLastOpenPeriodForStatus(APPLICATION_ID, StatusType.OPERATIONAL_CONDITION).isEmpty());
  }

  @Test
  public void shouldRecogniseFinalPeriodOfPhase() {
    InvoicingPeriod first = datedPeriod("2026-04-01", "2026-04-30", StatusType.OPERATIONAL_CONDITION);
    first.setId(1);
    InvoicingPeriod last = datedPeriod("2026-05-01", "2026-05-02", StatusType.OPERATIONAL_CONDITION);
    last.setId(2);
    Application application = excavationAnnouncement("2026-04-01", "2026-12-31", null);
    mockApplication(application);
    mockExistingPeriods(List.of(first, last));

    assertFalse(invoicingPeriodService.isFinalPeriod(application, first));
    assertTrue(invoicingPeriodService.isFinalPeriod(application, last));
  }

  @Test
  public void shouldReturnPhaseEndForOperationalConditionAndWorkFinished() {
    Application application = excavationAnnouncement("2026-04-01", "2027-06-06", "2027-02-02");
    ((ExcavationAnnouncement) application.getExtension()).setWorkFinished(date("2027-06-06"));

    assertEquals(date("2027-02-02"),
        invoicingPeriodService.getPhaseEnd(application, StatusType.OPERATIONAL_CONDITION));
    assertEquals(date("2027-06-06"), invoicingPeriodService.getPhaseEnd(application, StatusType.FINISHED));
  }

  // --- Helpers ---

  private Application excavationAnnouncement(String start, String end, String operationalCondition) {
    Application application = new Application();
    application.setId(APPLICATION_ID);
    application.setType(ApplicationType.EXCAVATION_ANNOUNCEMENT);
    application.setStartTime(date(start));
    application.setEndTime(date(end));
    ExcavationAnnouncement extension = new ExcavationAnnouncement();
    if (operationalCondition != null) {
      extension.setWinterTimeOperation(date(operationalCondition));
    }
    application.setExtension(extension);
    return application;
  }

  private void mockApplication(Application application) {
    when(applicationDao.findById(APPLICATION_ID)).thenReturn(application);
  }

  private void mockWinterTime() {
    when(winterTimeService.getWinterTime())
        .thenReturn(new AnnualTimePeriod(LocalDate.of(2026, 12, 1), LocalDate.of(2027, 5, 14)));
  }

  private void mockNoExistingPeriods() {
    when(invoicingPeriodDao.findForApplicationId(APPLICATION_ID)).thenReturn(new ArrayList<>());
  }

  private void mockExistingPeriods(List<InvoicingPeriod> periods) {
    when(invoicingPeriodDao.findForApplicationId(APPLICATION_ID)).thenReturn(new ArrayList<>(periods));
  }

  @SuppressWarnings("unchecked")
  private List<InvoicingPeriod> captureInsertedPeriods() {
    ArgumentCaptor<List<InvoicingPeriod>> captor = ArgumentCaptor.forClass(List.class);
    verify(invoicingPeriodDao).insertPeriods(captor.capture());
    return captor.getValue();
  }

  private InvoicingPeriod datedPeriod(String start, String end, StatusType status) {
    InvoicingPeriod period = new InvoicingPeriod(APPLICATION_ID, date(start), date(end));
    period.setInvoicableStatus(status);
    return period;
  }

  private ZonedDateTime date(String date) {
    return LocalDate.parse(date).atStartOfDay(TimeUtil.HelsinkiZoneId);
  }

  private void assertPeriod(InvoicingPeriod period, String start, String end, StatusType status) {
    assertEquals(date(start), period.getStartTime());
    assertEquals(date(end), period.getEndTime());
    assertEquals(status, period.getInvoicableStatus());
  }

  private void validatePeriods(List<InvoicingPeriod> result, int expectedAmount) {
    assertEquals(expectedAmount, result.size());
    Iterator<InvoicingPeriod> iterator = result.iterator();
    InvoicingPeriod previous = iterator.next();
    assertEquals(0, Duration.between(START_TIME, previous.getStartTime()).toDays());
    while (iterator.hasNext()) {
      InvoicingPeriod current = iterator.next();
      assertEquals(1, Duration.between(previous.getEndTime(), current.getStartTime()).toDays());
      previous = current;
    }
    assertEquals(0, Duration.between(END_TIME, previous.getEndTime()).toDays());
  }

}
