package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.CableReportJson;
import fi.hel.allu.servicecore.domain.StatusChangeInfoJson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationArchiverServiceTest {

  private ApplicationArchiverService archiverService;

  private Integer APPLICATION_ID = 2;
  private ApplicationJson applicationJson;
  private CableReportJson extensionJson;

  @Mock
  private ApplicationServiceComposer applicationServiceComposer;
  @Mock
  private SupervisionTaskService supervisionTaskService;


  @Before
  public void setup() {
    archiverService = new ApplicationArchiverService(applicationServiceComposer, supervisionTaskService);
    createApplication();
    when(applicationServiceComposer.findApplicationById(anyInt())).thenReturn(applicationJson);
    when(supervisionTaskService.findByApplicationId(anyInt())).thenReturn(Collections.emptyList());
  }

  // Both the end time and validity time are expired

  @Test
  public void shouldArchiveCableReportWithExpiredEndAndValidityTime() {
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, times(1))
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull(StatusChangeInfoJson.class));
  }

  @Test
  public void shouldArchiveOtherTypesWithExpiredEndAndValidityTime() {
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().minusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, times(1))
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull(StatusChangeInfoJson.class));
  }

  // Only the end time is expired

  @Test
  public void shouldNotArchiveCableReportWithExpiredEndTime() {
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull(StatusChangeInfoJson.class));
  }


  @Test
  public void shouldArchiveOtherTypesWithExpiredEndTime() {
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().minusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, times(1))
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull(StatusChangeInfoJson.class));
  }

  // Neither time is expired

  @Test
  public void shouldNotArchiveCableReportWithNoExpiredTimes() {
    applicationJson.setType(ApplicationType.CABLE_REPORT);
    applicationJson.setEndTime(ZonedDateTime.now().plusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull(StatusChangeInfoJson.class));
  }


  @Test
  public void shouldNotArchiveOtherTypesWithNoExpiredTimes() {
    applicationJson.setType(ApplicationType.EVENT);
    applicationJson.setEndTime(ZonedDateTime.now().plusDays(1));
    extensionJson.setValidityTime(ZonedDateTime.now().plusDays(1));

    archiverService.archiveApplicationIfNecessary(APPLICATION_ID);
    verify(applicationServiceComposer, never())
      .changeStatus(eq(APPLICATION_ID), eq(StatusType.ARCHIVED), isNotNull(StatusChangeInfoJson.class));
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
}
