package fi.hel.allu.servicecore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

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
  private InvoiceService invoiceService;
  private CustomerService customerService;

  private static final int applicationId = 1;
  private static final int projectId = 12;
  private static final int ownerId = 4;

  private ProjectJson projectJson = Mockito.mock(ProjectJson.class);

  @Before
  public void init() {
    projectService = Mockito.mock(ProjectService.class);
    applicationService = Mockito.mock(ApplicationService.class);
    searchService = Mockito.mock(SearchService.class);
    applicationJsonService = Mockito.mock(ApplicationJsonService.class);
    applicationHistoryService = Mockito.mock(ApplicationHistoryService.class);
    mailComposerService = Mockito.mock(MailComposerService.class);
    userService = Mockito.mock(UserService.class);
    invoiceService = Mockito.mock(InvoiceService.class);
    customerService = Mockito.mock(CustomerService.class);

    applicationServiceComposer = new ApplicationServiceComposer(
        applicationService,
        projectService,
        searchService,
        applicationJsonService,
        applicationHistoryService,
        mailComposerService,
        userService,
        invoiceService,
        customerService
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
  public void testChangeStatus() {
    final StatusChangeInfoJson info = new StatusChangeInfoJson();
    info.setOwner(ownerId);

    final UserJson user = new UserJson();
    user.setId(ownerId);

    final Application updatedApplication = new Application();
    updatedApplication.setStatus(StatusType.DECISIONMAKING);
    updatedApplication.setProjectId(projectJson.getId());

    final ApplicationJson updatedApplicationJson = new ApplicationJson();
    updatedApplicationJson.setOwner(user);
    updatedApplicationJson.setProject(projectJson);

    final Application applicationWithOwner = new Application();
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setProjectId(projectJson.getId());
    applicationWithOwner.setOwner(ownerId);

    Mockito.when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    Mockito.when(applicationService.changeApplicationStatus(applicationId, StatusType.DECISIONMAKING)).thenReturn(updatedApplication);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    assertEquals(updatedApplicationJson, applicationServiceComposer.changeStatus(applicationId, StatusType.DECISIONMAKING, info));

    Mockito.verify(searchService, Mockito.times(1)).updateApplications(Collections.singletonList(updatedApplicationJson));
    List<ApplicationJson> expected = new ArrayList<>();
    expected.add(updatedApplicationJson);
    Mockito.verify(searchService).updateApplications(Matchers.refEq(expected));
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
