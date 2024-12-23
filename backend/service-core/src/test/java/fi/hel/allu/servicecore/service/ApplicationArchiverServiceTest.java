package fi.hel.allu.servicecore.service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.CableReport;
import fi.hel.allu.model.domain.ExcavationAnnouncement;
import fi.hel.allu.servicecore.domain.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.servicecore.domain.supervision.SupervisionTaskJson;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationArchiverServiceTest {

  private ApplicationArchiverService archiverService;

  private Integer APPLICATION_ID = 2;
  private Integer USER_ID = 3;
  private ApplicationJson applicationJson;
  private CableReportJson extensionJson;

  @Mock
  private ApplicationServiceComposer applicationServiceComposer;
  @Mock
  private SupervisionTaskService supervisionTaskService;
  @Mock
  private TerminationService terminationService;


  @Before
  public void setup() {
    archiverService = new ApplicationArchiverService(applicationServiceComposer, supervisionTaskService, terminationService);
    createApplication();
    when(applicationServiceComposer.findApplicationById(anyInt())).thenReturn(applicationJson);
    when(supervisionTaskService.findByApplicationId(anyInt())).thenReturn(Collections.emptyList());
    when(applicationServiceComposer.findFinishedApplications(anyList(), anyList()))
        .thenReturn(Collections.singletonList(APPLICATION_ID));
    when(terminationService.fetchTerminatedApplications())
        .thenReturn(Collections.singletonList(APPLICATION_ID));
  }

  // Both the end time and validity time are expired

  @Test
  public void shouldArchiveCableReportWithExpiredEndAndValidityTime() {
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, times(1))
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  @Test
  public void shouldArchiveOtherTypesWithExpiredEndAndValidityTime() {
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, times(1))
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  @Test
  public void shouldNotArchiveRecurringApplicationBeforeRecurringEnd() {
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    applicationJson.setRecurringEndTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never()).changeStatus(eq(APPLICATION_ID), any(StatusType.class), any(StatusChangeInfoJson.class));
  }

  @Test
  public void shouldArchiveRecurringAfterRecurringEnd() {
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(2));
    applicationJson.setRecurringEndTime(ZonedDateTime.now().minusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, times(1))
        .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  // Only the end time is expired

  @Test
  public void shouldNotArchiveCableReportWithExpiredEndTime() {
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }


  @Test
  public void shouldArchiveOtherTypesWithExpiredEndTime() {
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, times(1))
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  // Neither time is expired

  @Test
  public void shouldNotArchiveCableReportWithNoExpiredTimes() {
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().plusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }


  @Test
  public void shouldNotArchiveOtherTypesWithNoExpiredTimes() {
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().plusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  @Test
  public void shouldUpdateToArchivedOtherTypesWithExpiredEndAndValidityTime() {
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));

    archiverService.updateStatusForFinishedApplications();
    verify(applicationServiceComposer, times(1))
        .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  @Test
  public void shouldUpdateNothingWhenSurveyRequired() {
    ApplicationTagJson surveyRequired = new ApplicationTagJson(USER_ID, ApplicationTagType.SURVEY_REQUIRED, ZonedDateTime.now());
    applicationJson.setApplicationTags(Collections.singletonList(surveyRequired));
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().plusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.updateStatusForFinishedApplications();
    verify(applicationServiceComposer, never()).changeStatus(eq(APPLICATION_ID), eq(StatusType.FINISHED));
    verify(applicationServiceComposer, never()).changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED));
  }

  @Test
  public void shouldArchiveTerminatedWithExpiredTerminationDate() {
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    applicationJson.setStatus(StatusType.TERMINATED);
    when(terminationService.getTerminationInfo(APPLICATION_ID))
        .thenReturn(createTerminationInfo(ZonedDateTime.now().minusDays(1)));

    archiverService.updateStatusForTerminatedApplications();
    verify(applicationServiceComposer, times(1))
        .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  @Test
  public void shouldNotArchiveTerminatedWithoutExpiredTerminationDate() {
    applicationJson.setType(ApplicationType.SHORT_TERM_RENTAL);
    applicationJson.setStatus(StatusType.TERMINATED);
    when(terminationService.getTerminationInfo(APPLICATION_ID))
        .thenReturn(createTerminationInfo(ZonedDateTime.now()));

    archiverService.updateStatusForTerminatedApplications();
    verify(applicationServiceComposer, never()).changeStatus(eq(APPLICATION_ID), eq(StatusType.FINISHED));
    verify(applicationServiceComposer, never()).changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED));
  }

  @Test
  public void shouldNotFinishTrafficArrangementWithOpenFinalSupervision() {
    applicationJson.setType(ApplicationType.TEMPORARY_TRAFFIC_ARRANGEMENTS);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));
    when(supervisionTaskService.findByApplicationId(anyInt())).thenReturn(Collections.singletonList(
      createSupervisionTask(11, SupervisionTaskType.FINAL_SUPERVISION, SupervisionTaskStatusType.OPEN)
    ));

    archiverService.updateStatusForFinishedApplications();

    verify(applicationServiceComposer, never()).changeStatus(eq(APPLICATION_ID), eq(StatusType.FINISHED));
    verify(applicationServiceComposer, never()).changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED));
  }

  @Test
  public void shouldNotArchiveCableReportAssociatedWithUnfinishedExcavationAnnouncement() {
    applicationJson.setApplicationId("JS2400701");
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));

    when(applicationServiceComposer.fetchActiveExcavationAnnouncements()).thenReturn(createExcavationAnnouncementList());

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
  }

  @Test
  public void shouldNotFinishCableReportAssociatedWithUnfinishedExcavationAnnouncement() {
    applicationJson.setApplicationId("JS2400701");
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));

    when(applicationServiceComposer.fetchActiveExcavationAnnouncements()).thenReturn(createExcavationAnnouncementList());

    archiverService.moveToFinishedOrArchived(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull());
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.FINISHED));
  }

  @Test
  public void shouldAnonymizeSuitableCableReports() {
    when(applicationServiceComposer.fetchPotentiallyAnonymizableApplications()).thenReturn(createAnonymizableCableReports());
    when(applicationServiceComposer.fetchActiveExcavationAnnouncements()).thenReturn(createExcavationAnnouncementList());

    archiverService.checkForAnonymizableApplications();

    verify(applicationServiceComposer, times(1)).addToAnonymizableApplications(eq(List.of(1,3)));
  }

  private void createApplication() {
    applicationJson = new ApplicationJson();
    applicationJson.setStatus(StatusType.FINISHED);
    applicationJson.setInvoiced(true);
    applicationJson.setApplicationTags(Collections.emptyList());
    applicationJson.setId(APPLICATION_ID);

    extensionJson = new CableReportJson();
    applicationJson.setExtension(extensionJson);
  }

  private TerminationInfo createTerminationInfo(ZonedDateTime expirationTime) {
    TerminationInfo info = new TerminationInfo();
    info.setExpirationTime(expirationTime);
    info.setReason("For testing");
    info.setTerminator(USER_ID);
    return info;
  }

  private SupervisionTaskJson createSupervisionTask(int id, SupervisionTaskType taskType, SupervisionTaskStatusType status) {
    SupervisionTaskJson task = new SupervisionTaskJson();
    task.setId(id);
    task.setType(taskType);
    task.setStatus(status);
    return task;
  }

  private List<Application> createExcavationAnnouncementList() {
    Application excavationAnnouncement1 = new Application();
    ExcavationAnnouncement extensionJson1 = new ExcavationAnnouncement();
    extensionJson1.setCableReports(List.of("JS2400602"));
    excavationAnnouncement1.setExtension(extensionJson1);

    Application excavationAnnouncement2 = new Application();
    ExcavationAnnouncement extensionJson2 = new ExcavationAnnouncement();
    extensionJson2.setCableReports(List.of("JS2400901", "JS2400701"));
    excavationAnnouncement2.setExtension(extensionJson2);

    Application excavationAnnouncement3 = new Application();
    ExcavationAnnouncement extensionJson3 = new ExcavationAnnouncement();
    extensionJson3.setCableReports(List.of("JS2400507"));
    excavationAnnouncement3.setExtension(extensionJson3);

    return List.of(excavationAnnouncement1, excavationAnnouncement2, excavationAnnouncement3);
  }

  private List<Application> createAnonymizableCableReports() {
    Application cableReport1 = new Application();
    cableReport1.setType(ApplicationType.CABLE_REPORT);
    cableReport1.setId(1);
    cableReport1.setApplicationId("JS240000001");
    cableReport1.setStatus(StatusType.DECISION);
    cableReport1.setEndTime(ZonedDateTime.now().minusYears(2).minusMonths(6));
    CableReport extension1 = new CableReport();
    extension1.setValidityTime(ZonedDateTime.now().minusYears(2).minusMonths(5));
    cableReport1.setExtension(extension1);

    Application cableReport2 = new Application();
    cableReport2.setType(ApplicationType.CABLE_REPORT);
    cableReport2.setId(2);
    cableReport2.setApplicationId("JS240000002");
    cableReport2.setStatus(StatusType.DECISION);
    cableReport2.setEndTime(ZonedDateTime.now().minusYears(2).minusMonths(1));
    CableReport extension2 = new CableReport();
    extension2.setValidityTime(ZonedDateTime.now().minusYears(1).minusMonths(11));
    cableReport2.setExtension(extension2);

    Application cableReport3 = new Application();
    cableReport3.setType(ApplicationType.CABLE_REPORT);
    cableReport3.setId(3);
    cableReport3.setApplicationId("JS240000003");
    cableReport3.setStatus(StatusType.DECISION);
    cableReport3.setEndTime(ZonedDateTime.now().minusYears(2).minusMonths(3));
    CableReport extension3 = new CableReport();
    extension3.setValidityTime(ZonedDateTime.now().minusYears(2).minusMonths(2));
    cableReport3.setExtension(extension3);

    Application cableReport4 = new Application();
    cableReport4.setType(ApplicationType.CABLE_REPORT);
    cableReport4.setId(4);
    cableReport4.setApplicationId("JS2400701");
    cableReport4.setStatus(StatusType.DECISION);
    cableReport4.setEndTime(ZonedDateTime.now().minusYears(2).minusMonths(5));
    CableReport extension4 = new CableReport();
    extension4.setValidityTime(ZonedDateTime.now().minusYears(2).minusMonths(3));
    cableReport4.setExtension(extension4);

    return List.of(cableReport1, cableReport2, cableReport3, cableReport4);
  }
}
