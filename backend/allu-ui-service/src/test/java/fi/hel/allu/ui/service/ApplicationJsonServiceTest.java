package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.*;
import fi.hel.allu.ui.mapper.ApplicationMapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationJsonServiceTest {

  private RestTemplate restTemplate;
  private ApplicationProperties applicationProperties;
  private ApplicationMapper applicationMapper;
  private ProjectService projectService;
  private ApplicantService applicantService;
  private ContactService contactService;
  private MetaService metaService;
  private UserService userService;
  private LocationService locationService;
  private AttachmentService attachmentService;
  private CommentService commentService;

  private static final int applicationId = 1;
  private static final int applicantId = 12;
  private static final int userId = 123;
  private static final int locationId = 1234;
  private static final int projectId = 12345;

  private Application application = Mockito.mock(Application.class);
  private ProjectJson projectJson = Mockito.mock(ProjectJson.class);
  private ApplicantJson applicantJson = Mockito.mock(ApplicantJson.class);
  private ContactJson contactJson = Mockito.mock(ContactJson.class);
  private List<ContactJson> contactJsons = Collections.singletonList(contactJson);
  private UserJson userJson = Mockito.mock(UserJson.class);
  private LocationJson locationJson = Mockito.mock(LocationJson.class);
  private List<CommentJson> comments = new ArrayList<>();

  @Before
  public void init() {
    applicationProperties = Mockito.mock(ApplicationProperties.class);
    applicationMapper = Mockito.mock(ApplicationMapper.class);
    projectService = Mockito.mock(ProjectService.class);
    applicantService = Mockito.mock(ApplicantService.class);
    contactService = Mockito.mock(ContactService.class);
    metaService = Mockito.mock(MetaService.class);
    userService = Mockito.mock(UserService.class);
    locationService = Mockito.mock(LocationService.class);
    restTemplate = Mockito.mock(RestTemplate.class);
    attachmentService = Mockito.mock(AttachmentService.class);
    commentService = Mockito.mock(CommentService.class);

    Mockito.when(application.getId()).thenReturn(applicationId);
    Mockito.when(application.getApplicantId()).thenReturn(applicantId);
    Mockito.when(application.getHandler()).thenReturn(userId);
    Mockito.when(application.getProjectId()).thenReturn(projectId);
    Mockito.when(application.getType()).thenReturn(ApplicationType.SHORT_TERM_RENTAL);
    Mockito.when(application.getMetadataVersion()).thenReturn(1);

    Mockito.when(projectService.findByIds(Collections.singletonList(projectId))).thenReturn(Collections.singletonList(projectJson));
    Mockito.when(applicantService.findApplicantById(applicantId)).thenReturn(applicantJson);
    Mockito.when(contactService.findContactsForApplication(applicationId)).thenReturn(contactJsons);
    Mockito.when(userService.findUserById(userId)).thenReturn(userJson);
    Mockito.when(locationService.findLocationsByApplication(applicationId)).thenReturn(Collections.singletonList(locationJson));
    Mockito.when(attachmentService.findAttachmentsForApplication(applicationId)).thenReturn(Collections.emptyList());

    comments.add(Mockito.mock(CommentJson.class));
    comments.add(Mockito.mock(CommentJson.class));
    Mockito.when(commentService.findByApplicationId(applicationId)).thenReturn(comments);
  }


  @Test
  public void testGetFullyPopulatedApplication() {
    Mockito.when(applicationMapper.mapApplicationToJson(application)).thenReturn(new ApplicationJson());
    ApplicationJsonService applicationJsonService = new ApplicationJsonService(
        applicationMapper,
        projectService,
        applicantService,
        contactService,
        metaService,
        userService,
        locationService,
        attachmentService,
        commentService);
    ApplicationJson applicationJson = applicationJsonService.getFullyPopulatedApplication(application);
    Assert.assertEquals(projectJson, applicationJson.getProject());
    Assert.assertEquals(applicantJson, applicationJson.getApplicant());
    Assert.assertEquals(contactJsons, applicationJson.getContactList());
    Assert.assertEquals(userJson, applicationJson.getHandler());
    Assert.assertEquals(locationJson, applicationJson.getLocations().get(0));
    Assert.assertEquals(0, applicationJson.getAttachmentList().size());
    Assert.assertEquals(2, applicationJson.getComments().size());
  }
}
