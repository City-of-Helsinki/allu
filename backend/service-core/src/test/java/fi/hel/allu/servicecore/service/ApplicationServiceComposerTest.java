package fi.hel.allu.servicecore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fi.hel.allu.servicecore.mapper.CustomerMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

import static org.junit.Assert.assertEquals;

import java.time.ZonedDateTime;
import java.util.Arrays;


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
  private SupervisionTaskService supervisionTaskService;
  private CustomerMapper customerMapper;

  private static final int applicationId = 1;
  private static final int projectId = 12;
  private static final int ownerId = 4;

  private final ProjectJson projectJson = Mockito.mock(ProjectJson.class);

  private StatusChangeInfoJson info;
  private Application applicationWithOwner;
  private UserJson user;
  private Application updatedApplication;
  private ApplicationJson updatedApplicationJson;

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
    supervisionTaskService = Mockito.mock(SupervisionTaskService.class);
    customerMapper = Mockito.mock(CustomerMapper.class);

    applicationServiceComposer = new ApplicationServiceComposer(
        applicationService,
        projectService,
        searchService,
        applicationJsonService,
        applicationHistoryService,
        mailComposerService,
        userService,
        invoiceService,
        customerService,
        supervisionTaskService,
        customerMapper
    );

    info = new StatusChangeInfoJson();
    info.setOwner(ownerId);

    applicationWithOwner = new Application();
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setProjectId(projectJson.getId());
    applicationWithOwner.setOwner(ownerId);
    applicationWithOwner.setId(applicationId);

    user = new UserJson();
    user.setId(ownerId);

    updatedApplicationJson = new ApplicationJson();
    updatedApplicationJson.setOwner(user);
    updatedApplicationJson.setProject(projectJson);

    updatedApplication = new Application();
    updatedApplication.setStatus(StatusType.DECISIONMAKING);
    updatedApplication.setProjectId(projectJson.getId());
    updatedApplication.setId(applicationId);
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

    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson, DecisionDocumentType.DECISION);
    Mockito.verify(mailComposerService).sendDecision(applicationJson, decisionDetailsJson, DecisionDocumentType.DECISION);
  }

  @Test
  public void testReturnToEditingFromFinishedToOperationalCondition() {
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.FINISHED);
    updatedApplication.setStatus(StatusType.OPERATIONAL_CONDITION);
    updatedApplicationJson.setStatus(StatusType.OPERATIONAL_CONDITION);
    Mockito.when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    Mockito.when(applicationService.returnToStatus(applicationId, StatusType.OPERATIONAL_CONDITION)).thenReturn(updatedApplication);
    Mockito.when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.OPERATIONAL_CONDITION)).thenReturn(true);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    ApplicationJson json = applicationServiceComposer.returnToEditing(applicationId, info);
    assertEquals(StatusType.OPERATIONAL_CONDITION, json.getStatus());
  }

  @Test
  public void testReturnToEditingFromFinishedToDecision() {
    ChangeHistoryItemJson[] history = {};
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.FINISHED);
    updatedApplication.setStatus(StatusType.DECISION);
    updatedApplicationJson.setStatus(StatusType.DECISION);
    Mockito.when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    Mockito.when(applicationService.returnToStatus(applicationId, StatusType.DECISION)).thenReturn(updatedApplication);
    Mockito.when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.OPERATIONAL_CONDITION)).thenReturn(false);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    ApplicationJson json = applicationServiceComposer.returnToEditing(applicationId, info);
    assertEquals(StatusType.DECISION, json.getStatus());
  }

  @Test
  public void testReturnToEditingFromOperationalConditionToDecision() {
    ChangeHistoryItemJson[] history = {};
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.OPERATIONAL_CONDITION);
    updatedApplication.setStatus(StatusType.DECISION);
    updatedApplicationJson.setStatus(StatusType.DECISION);
    Mockito.when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    Mockito.when(applicationService.returnToStatus(applicationId, StatusType.DECISION)).thenReturn(updatedApplication);
    Mockito.when(applicationHistoryService.getStatusChanges(applicationId)).thenReturn(Arrays.asList(history));
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    ApplicationJson json = applicationServiceComposer.returnToEditing(applicationId, info);
    assertEquals(StatusType.DECISION, json.getStatus());
  }

  @Test
  public void testShouldReturnToDecisionWhenNoFinishedInHistory() {
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.TERMINATED);
    updatedApplication.setStatus(StatusType.DECISION);
    updatedApplicationJson.setStatus(StatusType.DECISION);
    Mockito.when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner, updatedApplication);
    Mockito.when(applicationService.returnToStatus(applicationId, StatusType.DECISION)).thenReturn(updatedApplication);
    Mockito.when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.FINISHED)).thenReturn(false);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(updatedApplication)).thenReturn(updatedApplicationJson);

    applicationServiceComposer.returnToEditing(applicationId, info);
    Mockito.verify(applicationService, Mockito.times(1)).returnToStatus(applicationId, StatusType.DECISION);
  }

  @Test
  public void testShouldReturnToFinishedWhenFinishedInHistory() {
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.TERMINATED);
    updatedApplication.setStatus(StatusType.FINISHED);
    updatedApplicationJson.setStatus(StatusType.FINISHED);
    Mockito.when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner, updatedApplication);
    Mockito.when(applicationService.returnToStatus(applicationId, StatusType.FINISHED)).thenReturn(updatedApplication);
    Mockito.when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.FINISHED)).thenReturn(true);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(updatedApplication)).thenReturn(updatedApplicationJson);

    applicationServiceComposer.returnToEditing(applicationId, info);
    Mockito.verify(applicationService, Mockito.times(1)).returnToStatus(applicationId, StatusType.FINISHED);
  }
}
