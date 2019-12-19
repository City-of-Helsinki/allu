package fi.hel.allu.servicecore.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import fi.hel.allu.model.domain.Application;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ProjectJson;
import fi.hel.allu.servicecore.mapper.ProjectMapper;

public class ProjectServiceComposerTest {

  private ApplicationService applicationService;
  private ProjectService projectService;
  private SearchService searchService;
  private ApplicationJsonService applicationJsonService;
  private ProjectServiceComposer projectServiceComposer;
  private CustomerService customerService;
  private ProjectMapper projectMapper;

  private static final int projectId1 = 1;
  private static final int projectId2 = 2;
  private static final int projectParentId = 3;

  @Before
  public void init() {
    applicationService = Mockito.mock(ApplicationService.class);
    projectService = Mockito.mock(ProjectService.class);
    searchService = Mockito.mock(SearchService.class);
    applicationJsonService = Mockito.mock(ApplicationJsonService.class);
    customerService = Mockito.mock(CustomerService.class);
    projectMapper = Mockito.mock(ProjectMapper.class);
    projectServiceComposer = new ProjectServiceComposer(applicationService, projectService, applicationJsonService,
        searchService, customerService, projectMapper);
  }

  @Test
  public void testAddProjectApplicationsAddsGivenApplications() {
    Application application1 = createApplication(1);
    Application application2 = createApplication(1, projectId2);

    List<Application> applications = Arrays.asList(application1, application2);
    List<Integer> applicationIds = applications.stream().map(Application::getId).collect(Collectors.toList());

    ApplicationJson applicationJson1 = new ApplicationJson();
    ApplicationJson applicationJson2 = new ApplicationJson();

    Mockito.when(applicationService.findApplicationsById(applicationIds)).thenReturn(applications);
    Mockito.when(projectService.addApplications(projectId1, applicationIds)).thenReturn(applicationIds);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(application1)).thenReturn(applicationJson1);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(application2)).thenReturn(applicationJson2);

    Assert.assertEquals(
        Arrays.asList(applicationJson1, applicationJson2),
        projectServiceComposer.addApplications(projectId1, applicationIds));
  }

  @Test
  public void testAddProjectApplicationsUpdatesTargetProjectAndParent() {
    Application application = createApplication(1, projectId1);

    List<Application> applications = Arrays.asList(application);
    List<Integer> applicationIds = Arrays.asList(application.getId());

    ProjectJson projectJson = createProjectJson(projectId1);
    ProjectJson projectParentJson = createProjectJson(projectParentId);

    Mockito.when(applicationService.findApplicationsById(applicationIds)).thenReturn(applications);
    Mockito.when(projectService.findByIds(Arrays.asList(projectId1)))
        .thenReturn(Arrays.asList(projectJson));
    Mockito.when(projectService.findProjectParents(projectId1)).thenReturn(Collections.singletonList(projectParentJson));
    Mockito.when(projectService.addApplications(projectId1, applicationIds)).thenReturn(applicationIds);

    projectServiceComposer.addApplications(projectId1, applicationIds);

    Mockito.verify(searchService).updateProjects(Arrays.asList(projectJson, projectParentJson));
  }

  @Test
  public void testAddProjectApplicationsUpdatesProjectWithRemovedApplications() {
    Application application = createApplication(1, projectId2);
    List<Application> applications = Arrays.asList(application);
    List<Integer> applicationIds = Arrays.asList(application.getId());

    ProjectJson project1Json = createProjectJson(projectId1);
    ProjectJson project2Json = createProjectJson(projectId2);
    ProjectJson project2ParentJson = createProjectJson(projectParentId);

    Mockito.when(applicationService.findApplicationsById(applicationIds)).thenReturn(applications);
    Mockito.when(projectService.findByIds(Arrays.asList(projectId1, projectId2)))
        .thenReturn(Arrays.asList(project1Json, project2Json));
    Mockito.when(projectService.findProjectParents(projectId2)).thenReturn(Collections.singletonList(project2ParentJson));
    Mockito.when(projectService.addApplications(projectId1, applicationIds)).thenReturn(applicationIds);

    projectServiceComposer.addApplications(projectId1, applicationIds);
    Mockito.verify(searchService).updateProjects(Arrays.asList(project1Json, project2Json, project2ParentJson));
  }

  @Test
  public void testAddProjectApplicationsUpdatesApplicationsToSearch() {
    Application application1 = createApplication(1);
    Application application2 = createApplication(2);
    List<Application> applications = Arrays.asList(application1, application2);
    List<Integer> applicationIds = Arrays.asList(application1.getId(), application2.getId());

    Mockito.when(applicationService.findApplicationsById(applicationIds)).thenReturn(applications);
    Mockito.when(projectService.addApplications(projectId1, applicationIds)).thenReturn(applicationIds);

    ApplicationJson applicationJson1 = new ApplicationJson();
    ApplicationJson applicationJson2 = new ApplicationJson();

    Mockito.when(applicationJsonService.getFullyPopulatedApplication(application1)).thenReturn(applicationJson1);
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(application2)).thenReturn(applicationJson2);

    projectServiceComposer.addApplications(projectId1, applicationIds);

    verifyApplicationSearchUpdate(Arrays.asList(applicationJson1, applicationJson2));
  }

  @Test
  public void testRemoveApplicationRemovesGivenApplication() {
    projectServiceComposer.removeApplication(1);
    Mockito.verify(projectService, Mockito.times(1)).removeApplication(1);
  }

  @Test
  public void testRemoveApplicationUpdatesRelatedProjectsToSearch() {
    Application app = createApplication(1, projectId1);
    List<Application> applications = Arrays.asList(app);
    List<Integer> applicationIds = Arrays.asList(app.getId());

    ProjectJson projectJson = createProjectJson(projectId1);
    ProjectJson projectParentJson = createProjectJson(projectParentId);

    Mockito.when(applicationService.findApplicationsById(applicationIds)).thenReturn(applications);
    Mockito.when(projectService.findByIds(Arrays.asList(projectId1)))
        .thenReturn(Arrays.asList(projectJson));
    Mockito.when(projectService.findProjectParents(projectId1)).thenReturn(Collections.singletonList(projectParentJson));

    projectServiceComposer.removeApplication(app.getId());
    Mockito.verify(searchService).updateProjects(Arrays.asList(projectJson, projectParentJson));
  }

  @Test
  public void testRemoveApplicationUpdatesApplicationSearch() {
    Application app = createApplication(1);
    List<Application> applications = Arrays.asList(app);
    List<Integer> applicationIds = Arrays.asList(app.getId());

    Mockito.when(applicationService.findApplicationsById(applicationIds)).thenReturn(applications);

    ApplicationJson appJson = new ApplicationJson();
    Mockito.when(applicationJsonService.getFullyPopulatedApplication(app)).thenReturn(appJson);

    projectServiceComposer.removeApplication(app.getId());

    verifyApplicationSearchUpdate(Arrays.asList(appJson));
  }

  private ProjectJson createProjectJson(Integer id) {
    ProjectJson project = new ProjectJson();
    project.setId(id);
    return project;
  }

  private Application createApplication(Integer id) {
    Application app = new Application();
    app.setId(id);
    return app;
  }

  private Application createApplication(Integer id, Integer projectId) {
    Application app = createApplication(id);
    app.setProjectId(projectId);
    return app;
  }

  private void verifyApplicationSearchUpdate(List<ApplicationJson> applicationJsons) {
    ArgumentCaptor<List> applicationListArgumentCaptor = ArgumentCaptor.forClass(List.class);
    Mockito.verify(searchService).updateApplications(applicationListArgumentCaptor.capture());
    List<ApplicationJson> searchUpdateApplication = applicationListArgumentCaptor.getValue();
    List<ApplicationJson> expectedSearchUpdateApplications = applicationJsons;
    Assert.assertEquals(expectedSearchUpdateApplications.size(), searchUpdateApplication.size());
    Assert.assertTrue(searchUpdateApplication.containsAll(expectedSearchUpdateApplications));
  }
}
