package fi.hel.allu.servicecore.service;

import java.time.ZonedDateTime;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.*;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.CableReportJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
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
}