package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ProjectJson;
import fi.hel.allu.ui.domain.QueryParameterJson;
import fi.hel.allu.ui.domain.QueryParametersJson;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApplicationServiceComposerTest {

  private ApplicationServiceComposer applicationServiceComposer;
  private ApplicationService applicationService;
  private ProjectService projectService;
  private SearchService searchService;
  private ApplicationJsonService applicationJsonService;
  private ApplicationHistoryService applicationHistoryService;

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

    applicationServiceComposer = new ApplicationServiceComposer(
        applicationService,
        projectService,
        searchService,
        applicationJsonService,
        applicationHistoryService
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
    ApplicationJson updatedApplicationJson = new ApplicationJson();
    updatedApplicationJson.setProject(projectJson);
    Mockito.when(projectJson.getId()).thenReturn(projectId);
    Mockito.when(applicationService.updateApplication(applicationId, applicationJson)).thenReturn(updatedApplicationJson);
    Mockito.when(projectService.updateProjectInformation(Collections.singletonList(projectId))).thenReturn(Collections.singletonList(projectJson));

    Assert.assertEquals(updatedApplicationJson, applicationServiceComposer.updateApplication(applicationId, applicationJson));

    Mockito.verify(projectService, Mockito.times(1)).updateProjectInformation(Collections.singletonList(projectId));
    Mockito.verify(searchService, Mockito.times(1)).updateApplications(Collections.singletonList(updatedApplicationJson));
  }
}
