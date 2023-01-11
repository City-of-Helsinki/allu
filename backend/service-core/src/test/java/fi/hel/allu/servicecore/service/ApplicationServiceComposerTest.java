package fi.hel.allu.servicecore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mockito.Mockito;

import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceComposerTest {

  @Mock
  private ApplicationService applicationService;
  @Mock
  private ProjectService projectService;
  @Mock
  private SearchService searchService;
  @Mock
  private ApplicationJsonService applicationJsonService;
  @Mock
  private ApplicationHistoryService applicationHistoryService;
  @Mock
  private MailComposerService mailComposerService;
  @Mock
  private SupervisionTaskService supervisionTaskService;
  @InjectMocks
  private ApplicationServiceComposer applicationServiceComposer;

  private static final int applicationId = 1;
  private static final int projectId = 12;
  private static final int ownerId = 4;

  private final ProjectJson projectJson = Mockito.mock(ProjectJson.class);

  private StatusChangeInfoJson info;
  private Application applicationWithOwner;
  private UserJson user;
  private Application updatedApplication;
  private ApplicationJson updatedApplicationJson;


  @BeforeEach
  public void init() {
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
    when(projectJson.getId()).thenReturn(projectId);
    when(applicationService.updateApplication(applicationId, applicationJson)).thenReturn(updatedApplication);
    when(applicationService.findApplicationById(applicationId)).thenReturn(updatedApplication);
    when(applicationJsonService.getFullyPopulatedApplication(updatedApplication))
        .thenReturn(updatedApplicationJson);
    when(projectService.updateProjectInformation(Collections.singletonList(projectId))).thenReturn(Collections.singletonList(projectJson));

    assertEquals(updatedApplicationJson, applicationServiceComposer.updateApplication(applicationId, applicationJson));

    Mockito.verify(projectService, Mockito.times(1)).updateProjectInformation(Collections.singletonList(projectId));
    Mockito.verify(searchService, Mockito.times(1)).updateApplications(Collections.singletonList(updatedApplicationJson));
  }

  @Test
  public void testChangeStatus() {
    when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    when(applicationService.changeApplicationStatus(applicationId, StatusType.DECISIONMAKING)).thenReturn(updatedApplication);
    when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    assertEquals(updatedApplicationJson, applicationServiceComposer.changeStatus(applicationId, StatusType.DECISIONMAKING, info));

    Mockito.verify(searchService, Mockito.times(1)).updateApplications(Collections.singletonList(updatedApplicationJson));
    List<ApplicationJson> expected = new ArrayList<>();
    expected.add(updatedApplicationJson);
    Mockito.verify(searchService).updateApplications(Mockito.refEq(expected));
  }

  @Test
  public void testSendDecision() {
    when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    ApplicationJson applicationJson = Mockito.mock(ApplicationJson.class);
    when(applicationJsonService.getFullyPopulatedApplication(Mockito.any(Application.class)))
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
    when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    when(applicationService.returnToStatus(applicationId, StatusType.OPERATIONAL_CONDITION)).thenReturn(updatedApplication);
    when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.OPERATIONAL_CONDITION)).thenReturn(true);
    when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    ApplicationJson json = applicationServiceComposer.returnToEditing(applicationId, info);
    assertEquals(StatusType.OPERATIONAL_CONDITION, json.getStatus());
  }

  @Test
  public void testReturnToEditingFromFinishedToDecision() {
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.FINISHED);
    updatedApplication.setStatus(StatusType.DECISION);
    updatedApplicationJson.setStatus(StatusType.DECISION);
    when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    when(applicationService.returnToStatus(applicationId, StatusType.DECISION)).thenReturn(updatedApplication);
    when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.OPERATIONAL_CONDITION)).thenReturn(false);
    when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    ApplicationJson json = applicationServiceComposer.returnToEditing(applicationId, info);
    assertEquals(StatusType.DECISION, json.getStatus());
  }

  @Test
  public void testReturnToEditingFromOperationalConditionToDecision() {
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.OPERATIONAL_CONDITION);
    updatedApplication.setStatus(StatusType.DECISION);
    updatedApplicationJson.setStatus(StatusType.DECISION);
    when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner);
    when(applicationService.returnToStatus(applicationId, StatusType.DECISION)).thenReturn(updatedApplication);
    when(applicationJsonService.getFullyPopulatedApplication(applicationWithOwner)).thenReturn(updatedApplicationJson);

    ApplicationJson json = applicationServiceComposer.returnToEditing(applicationId, info);
    assertEquals(StatusType.DECISION, json.getStatus());
  }

  @Test
  public void testShouldReturnToDecisionWhenNoFinishedInHistory() {
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.TERMINATED);
    updatedApplication.setStatus(StatusType.DECISION);
    updatedApplicationJson.setStatus(StatusType.DECISION);
    when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner, updatedApplication);
    when(applicationService.returnToStatus(applicationId, StatusType.DECISION)).thenReturn(updatedApplication);
    when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.FINISHED)).thenReturn(false);
    when(applicationJsonService.getFullyPopulatedApplication(updatedApplication)).thenReturn(updatedApplicationJson);

    applicationServiceComposer.returnToEditing(applicationId, info);
    Mockito.verify(applicationService, Mockito.times(1)).returnToStatus(applicationId, StatusType.DECISION);
  }

  @Test
  public void testShouldReturnToFinishedWhenFinishedInHistory() {
    applicationWithOwner.setStatus(StatusType.DECISIONMAKING);
    applicationWithOwner.setTargetState(StatusType.TERMINATED);
    updatedApplication.setStatus(StatusType.FINISHED);
    updatedApplicationJson.setStatus(StatusType.FINISHED);
    when(applicationService.findApplicationById(applicationId)).thenReturn(applicationWithOwner, updatedApplication);
    when(applicationService.returnToStatus(applicationId, StatusType.FINISHED)).thenReturn(updatedApplication);
    when(applicationHistoryService.hasStatusInHistory(applicationId, StatusType.FINISHED)).thenReturn(true);
    when(applicationJsonService.getFullyPopulatedApplication(updatedApplication)).thenReturn(updatedApplicationJson);

    applicationServiceComposer.returnToEditing(applicationId, info);
    Mockito.verify(applicationService, Mockito.times(1)).returnToStatus(applicationId, StatusType.FINISHED);
  }
}