package fi.hel.allu.ui.service;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ProjectJson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectServiceComposerTest {

  private ApplicationService applicationService;
  private ProjectService projectService;
  private SearchService searchService;
  private ApplicationJsonService applicationJsonService;
  private ProjectServiceComposer projectServiceComposer;
  private static final int projectId1 = 1;
  private static final int projectId2 = 2;

  @Before
  public void init() {
    applicationService = Mockito.mock(ApplicationService.class);
    projectService = Mockito.mock(ProjectService.class);
    searchService = Mockito.mock(SearchService.class);
    applicationJsonService = Mockito.mock(ApplicationJsonService.class);
    projectServiceComposer = new ProjectServiceComposer(applicationService, projectService, applicationJsonService, searchService);
  }

  @Test
  public void testUpdateProjectApplications() {
    Application application1 = Mockito.mock(Application.class);
    Mockito.when(application1.getId()).thenReturn(1);
    Mockito.when(application1.getProjectId()).thenReturn(projectId1);
    Application application2 = Mockito.mock(Application.class);
    Mockito.when(application2.getId()).thenReturn(2);
    Mockito.when(application2.getProjectId()).thenReturn(projectId2);
    Application application3 = Mockito.mock(Application.class);
    Mockito.when(application3.getId()).thenReturn(3);

    ApplicationJson applicationJson1 = Mockito.mock(ApplicationJson.class);
    ApplicationJson applicationJson2 = Mockito.mock(ApplicationJson.class);
    ApplicationJson applicationJson3 = Mockito.mock(ApplicationJson.class);

    ProjectJson projectJson = Mockito.mock(ProjectJson.class);
    ProjectJson projectJsonParent = Mockito.mock(ProjectJson.class);

    List<Application> applications = Arrays.asList(application1, application2, application3);
    List<Integer> applicationIds = applications.stream().map(Application::getId).collect(Collectors.toList());

    Mockito.when(applicationService.findApplicationsById(applicationIds)).thenReturn(applications);
    Mockito.when(projectService.findProjectParents(projectId2)).thenReturn(Collections.singletonList(projectJsonParent));
    Mockito.when(projectService.updateProjectApplications(projectId1, applicationIds)).thenReturn(projectJson);
    Mockito.when(projectService.findApplicationsByProject(projectId1)).thenReturn(Collections.singletonList(application3));
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(application1)).thenReturn(applicationJson1);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(application2)).thenReturn(applicationJson2);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(application3)).thenReturn(applicationJson3);

    Assert.assertEquals(projectJson, projectServiceComposer.updateProjectApplications(projectId1, applicationIds));

    ArgumentCaptor<List> applicationListArgumentCaptor = ArgumentCaptor.forClass(List.class);

    Mockito.verify(searchService).updateProjects(Arrays.asList(projectJson, projectJsonParent));
    // have to use argument captor, because tested code uses Set, which may change the order of given list in random way
    Mockito.verify(searchService).updateApplications(applicationListArgumentCaptor.capture());
    List<ApplicationJson> searchUpdateApplication = applicationListArgumentCaptor.getValue();
    List<ApplicationJson> expectedSearchUpdateApplications = Arrays.asList(applicationJson1, applicationJson2, applicationJson3);
    Assert.assertEquals(expectedSearchUpdateApplications.size(), searchUpdateApplication.size());
    Assert.assertTrue(searchUpdateApplication.containsAll(expectedSearchUpdateApplications));
  }
}
