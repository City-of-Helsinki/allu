package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ApplicationKind;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.search.domain.QueryParameters;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ApplicationServiceComposerTest {

  private ApplicationServiceComposer applicationServiceComposer;
  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private ApplicationMapper applicationMapper;
  private ApplicationService applicationService;
  private ProjectService projectService;
  private ApplicantService applicantService;
  private ContactService contactService;
  private MetaService metaService;
  private UserService userService;
  private LocationService locationService;
  private SearchService searchService;

  private int applicationId = 1;
  private int applicantId = 12;
  private int userId = 123;
  private int locationId = 1234;
  private int projectId = 12345;

  private Application application = Mockito.mock(Application.class);
  private ProjectJson projectJson = Mockito.mock(ProjectJson.class);
  private ApplicantJson applicantJson = Mockito.mock(ApplicantJson.class);
  private ContactJson contactJson = Mockito.mock(ContactJson.class);
  private List<ContactJson> contactJsons = Collections.singletonList(contactJson);
  private StructureMetaJson metaJson = Mockito.mock(StructureMetaJson.class);
  private UserJson userJson = Mockito.mock(UserJson.class);
  private LocationJson locationJson = Mockito.mock(LocationJson.class);
  private QueryParametersJson queryParametersJson = Mockito.mock(QueryParametersJson.class);
  private QueryParameters queryParameters = Mockito.mock(QueryParameters.class);

  @Before
  public void init() {
    applicationProperties = Mockito.mock(ApplicationProperties.class);
    applicationMapper = Mockito.mock(ApplicationMapper.class);
    projectService = Mockito.mock(ProjectService.class);
    applicationService = Mockito.mock(ApplicationService.class);
    applicantService = Mockito.mock(ApplicantService.class);
    contactService = Mockito.mock(ContactService.class);
    metaService = Mockito.mock(MetaService.class);
    userService = Mockito.mock(UserService.class);
    locationService = Mockito.mock(LocationService.class);
    restTemplate = Mockito.mock(RestTemplate.class);
    searchService = Mockito.mock(SearchService.class);
    applicationServiceComposer = new ApplicationServiceComposer(
        applicationProperties,
        restTemplate,
        applicationMapper,
        applicationService,
        projectService,
        applicantService,
        contactService,
        metaService,
        userService,
        locationService,
        searchService
    );

    Mockito.when(application.getId()).thenReturn(applicationId);
    Mockito.when(application.getApplicantId()).thenReturn(applicantId);
    Mockito.when(application.getHandler()).thenReturn(userId);
    Mockito.when(application.getLocationId()).thenReturn(locationId);
    Mockito.when(application.getProjectId()).thenReturn(projectId);
    Mockito.when(application.getType()).thenReturn(ApplicationType.SHORT_TERM_RENTAL);
    Mockito.when(application.getKind()).thenReturn(ApplicationKind.ART);
    Mockito.when(application.getMetadataVersion()).thenReturn(1);

    Mockito.when(projectService.findById(projectId)).thenReturn(projectJson);
    Mockito.when(applicantService.findApplicantById(applicantId)).thenReturn(applicantJson);
    Mockito.when(contactService.findContactsForApplication(applicationId)).thenReturn(contactJsons);
    Mockito.when(metaService.findMetadataForApplication(ApplicationType.SHORT_TERM_RENTAL, 1)).thenReturn(metaJson);
    Mockito.when(userService.findUserById(userId)).thenReturn(userJson);
    Mockito.when(locationService.findLocationById(locationId)).thenReturn(locationJson);
    ResponseEntity<AttachmentInfo[]> responseEntity = Mockito.mock(ResponseEntity.class);
    Mockito.when(responseEntity.getBody()).thenReturn(new AttachmentInfo[0]);
    Mockito.when(restTemplate.getForEntity(
        Matchers.eq(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION)),
        Matchers.eq(AttachmentInfo[].class),
        Matchers.anyInt())).thenReturn(responseEntity);
  }

  @Test
  public void testGetFullyPopulatedApplication() {
    Mockito.when(applicationMapper.mapApplicationToJson(application)).thenReturn(new ApplicationJson());
    ApplicationJson applicationJson = applicationServiceComposer.getFullyPopulatedApplication(application);
    Assert.assertEquals(projectJson, applicationJson.getProject());
    Assert.assertEquals(applicantJson, applicationJson.getApplicant());
    Assert.assertEquals(contactJsons, applicationJson.getContactList());
    Assert.assertEquals(metaJson, applicationJson.getMetadata());
    Assert.assertEquals(userJson, applicationJson.getHandler());
    Assert.assertEquals(locationJson, applicationJson.getLocation());
    Assert.assertEquals(0, applicationJson.getAttachmentList().size());
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

    Mockito.when(applicationMapper.mapApplicationToJson(applications.get(0))).thenReturn(applicationJson1);
    Mockito.when(applicationMapper.mapApplicationToJson(applications.get(1))).thenReturn(applicationJson2);
    Mockito.when(applicationMapper.mapApplicationToJson(applications.get(2))).thenReturn(applicationJson3);

    Mockito.when(searchService.search(Matchers.any(QueryParameters.class))).thenReturn(idsInOrder);
    Mockito.when(applicationService.findApplicationsById(idsInOrder)).thenReturn(applications);

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
    Mockito.verify(searchService, Mockito.times(1)).updateApplication(updatedApplicationJson);
  }
}
