package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.DecisionDetailsJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.domain.QueryParametersJson;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class ApplicationServiceComposerTest {

  private ApplicationServiceComposer applicationServiceComposer;
  private ApplicationService applicationService;
  private ProjectService projectService;
  private SearchService searchService;
  private ApplicationJsonService applicationJsonService;
  private ApplicationHistoryService applicationHistoryService;
  private MailComposerService mailComposerService;
  private UserService userService;

  private static final int applicationId = 1;
  private static final int projectId = 12;

  private ProjectJson projectJson = Mockito.mock(ProjectJson.class);
  private QueryParametersJson queryParametersJson = Mockito.mock(QueryParametersJson.class);

  @Before
  public void init() {
    projectService = Mockito.mock(ProjectService.class);
    applicationService = Mockito.mock(ApplicationService.class);
    searchService = Mockito.mock(SearchService.class);
    applicationJsonService = Mockito.mock(ApplicationJsonService.class);
    applicationHistoryService = Mockito.mock(ApplicationHistoryService.class);
    mailComposerService = Mockito.mock(MailComposerService.class);
    userService = Mockito.mock(UserService.class);

    applicationServiceComposer = new ApplicationServiceComposer(
        applicationService,
        projectService,
        searchService,
        applicationJsonService,
        applicationHistoryService,
        mailComposerService,
        userService
    );
  }


  @Test
  public void testUpdateApplication() {
    ApplicationJson applicationJson = new ApplicationJson();
    Application updatedApplication = new Application();
    updatedApplication.setProjectId(projectJson.getId());
    ApplicationJson updatedApplicationJson = new ApplicationJson();
    updatedApplicationJson.setProject(projectJson);
    Mockito.when(projectJson.getId()).thenReturn(projectId);
    Mockito.when(applicationService.updateApplication(applicationId, applicationJson)).thenReturn(updatedApplication);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(updatedApplication))
        .thenReturn(updatedApplicationJson);
    Mockito.when(projectService.updateProjectInformation(Collections.singletonList(projectId))).thenReturn(Collections.singletonList(projectJson));

    assertEquals(updatedApplicationJson, applicationServiceComposer.updateApplication(applicationId, applicationJson));

    Mockito.verify(projectService, Mockito.times(1)).updateProjectInformation(Collections.singletonList(projectId));
    Mockito.verify(searchService, Mockito.times(1)).updateApplications(Collections.singletonList(updatedApplicationJson));
  }

  @Test
  public void testSendDecision() {
    ApplicationJson applicationJson = Mockito.mock(ApplicationJson.class);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(Mockito.any(Application.class)))
        .thenReturn(applicationJson);
    DecisionDetailsJson decisionDetailsJson = new DecisionDetailsJson();

    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson);
    Mockito.verify(mailComposerService).sendDecision(applicationJson, decisionDetailsJson);
  }

}
