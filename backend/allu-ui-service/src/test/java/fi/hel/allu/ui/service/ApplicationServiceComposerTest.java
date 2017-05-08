package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.domain.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class ApplicationServiceComposerTest {

  private ApplicationServiceComposer applicationServiceComposer;
  private ApplicationService applicationService;
  private ProjectService projectService;
  private SearchService searchService;
  private ApplicationJsonService applicationJsonService;
  private ApplicationHistoryService applicationHistoryService;
  private AttachmentService attachmentService;
  private AlluMailService alluMailService;

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
    attachmentService = Mockito.mock(AttachmentService.class);
    alluMailService = Mockito.mock(AlluMailService.class);

    applicationServiceComposer = new ApplicationServiceComposer(
        applicationService,
        projectService,
        searchService,
        applicationJsonService,
        applicationHistoryService, attachmentService, alluMailService
    );
  }

  @Test
  public void testSearch() {
    List<Integer> idsInOrder = Arrays.asList(1,2,3);
    List<Application> applications =
        Arrays.asList(Mockito.mock(Application.class), Mockito.mock(Application.class), Mockito.mock(Application.class));

    Mockito.when(applications.get(0).getId()).thenReturn(3);
    Mockito.when(applications.get(1).getId()).thenReturn(2);
    Mockito.when(applications.get(2).getId()).thenReturn(1);

    ApplicationJson applicationJson1 = new ApplicationJson();
    applicationJson1.setId(3);
    ApplicationJson applicationJson2 = new ApplicationJson();
    applicationJson2.setId(2);
    ApplicationJson applicationJson3 = new ApplicationJson();
    applicationJson3.setId(1);

    Mockito.when(searchService.searchApplication(Matchers.any(QueryParameters.class))).thenReturn(idsInOrder);
    Mockito.when(applicationService.findApplicationsById(idsInOrder)).thenReturn(applications);

    Mockito.when(applicationJsonService.getFullyPopulatedApplication(applications.get(0))).thenReturn(applicationJson1);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(applications.get(1))).thenReturn(applicationJson2);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(applications.get(2))).thenReturn(applicationJson3);

    Mockito.when(queryParametersJson.getQueryParameters()).thenReturn(Collections.singletonList(Mockito.mock(QueryParameterJson.class)));
    List<ApplicationJson> applicationJsons = applicationServiceComposer.search(queryParametersJson);

    Assert.assertEquals(idsInOrder.get(0), applicationJsons.get(0).getId());
    Assert.assertEquals(idsInOrder.get(1), applicationJsons.get(1).getId());
    Assert.assertEquals(idsInOrder.get(2), applicationJsons.get(2).getId());
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

    Assert.assertEquals(updatedApplicationJson, applicationServiceComposer.updateApplication(applicationId, applicationJson));

    Mockito.verify(projectService, Mockito.times(1)).updateProjectInformation(Collections.singletonList(projectId));
    Mockito.verify(searchService, Mockito.times(1)).updateApplications(Collections.singletonList(updatedApplicationJson));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSendDecision() {
    final int ATTACHMENT_ID_1 = 111;
    final int ATTACHMENT_ID_2 = 222;

    ApplicationJson applicationJson = Mockito.mock(ApplicationJson.class);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(Mockito.any(Application.class)))
        .thenReturn(applicationJson);
    Mockito.when(applicationJson.getApplicationId()).thenReturn("HK_BLEU");
    List<DistributionEntryJson> distribution = Collections
        .singletonList(emailDistribution("Pekka Pekanpekka", "pekkapekanpekka@pekanpekka.org"));
    Mockito.when(applicationJson.getDecisionDistributionList()).thenReturn(distribution);
    Mockito.when(applicationJson.getAttachmentList())
        .thenReturn(Arrays.asList(attachment("eka", ATTACHMENT_ID_1), attachment("toka", ATTACHMENT_ID_2)));
    DecisionDetailsJson decisionDetailsJson = new DecisionDetailsJson();
    decisionDetailsJson.setDecisionDistributionList(distribution);

    applicationServiceComposer.sendDecision(applicationId, decisionDetailsJson);

    ArgumentCaptor<Stream> streamCaptor = ArgumentCaptor.forClass(Stream.class);
    Mockito.verify(alluMailService).sendDecision(Mockito.eq(applicationId), Mockito.anyList(), Mockito.anyString(),
        Mockito.anyString(), streamCaptor.capture());
    assertEquals(2, streamCaptor.getValue().count());
  }

  private DistributionEntryJson emailDistribution(String name, String email) {
    DistributionEntryJson distributionEntryJson = new DistributionEntryJson();
    distributionEntryJson.setDistributionType(DistributionType.EMAIL);
    distributionEntryJson.setName(name);
    distributionEntryJson.setEmail(email);
    return distributionEntryJson;
  }

  private AttachmentInfoJson attachment(String name, int id) {
    AttachmentInfoJson attachmentInfoJson = new AttachmentInfoJson();
    attachmentInfoJson.setName(name);
    attachmentInfoJson.setId(id);
    return attachmentInfoJson;
  }
}
