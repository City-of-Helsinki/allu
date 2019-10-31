package fi.hel.allu.servicecore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.model.domain.Location;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;

public class ApplicationJsonServiceTest {

  private ApplicationMapper applicationMapper;
  private ProjectService projectService;
  private UserService userService;
  private LocationService locationService;
  private AttachmentService attachmentService;
  private CommentService commentService;
  private TerminationService terminationService;

  private static final int applicationId = 1;
  private static final int customerId = 12;
  private static final int userId = 123;
  private static final int projectId = 12345;

  private Application application = Mockito.mock(Application.class);
  private ProjectJson projectJson = Mockito.mock(ProjectJson.class);
  private CustomerJson customerJson = Mockito.mock(CustomerJson.class);
  private ContactJson contactJson = Mockito.mock(ContactJson.class);
  private List<ContactJson> contactJsons = Collections.singletonList(contactJson);
  private UserJson userJson = Mockito.mock(UserJson.class);
  private List<CommentJson> comments = new ArrayList<>();
  private UserJson currentUser = Mockito.mock(UserJson.class);

  @Before
  public void init() {
    applicationMapper = Mockito.mock(ApplicationMapper.class);
    projectService = Mockito.mock(ProjectService.class);
    userService = Mockito.mock(UserService.class);
    locationService = Mockito.mock(LocationService.class);
    attachmentService = Mockito.mock(AttachmentService.class);
    commentService = Mockito.mock(CommentService.class);
    terminationService = Mockito.mock(TerminationService.class);

    Mockito.when(application.getId()).thenReturn(applicationId);
    Customer customer = new Customer();
    customer.setId(customerId);
    Mockito.when(application.getCustomersWithContacts())
        .thenReturn(Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, Collections.emptyList())));
    Mockito.when(application.getOwner()).thenReturn(userId);
    Mockito.when(application.getProjectId()).thenReturn(projectId);
    Mockito.when(application.getType()).thenReturn(ApplicationType.SHORT_TERM_RENTAL);
    Mockito.when(application.getMetadataVersion()).thenReturn(1);

    Mockito.when(projectService.findById(projectId)).thenReturn(projectJson);
    Mockito.when(userService.findUserById(userId)).thenReturn(userJson);
    Mockito.when(attachmentService.findAttachmentsForApplication(applicationId)).thenReturn(Collections.emptyList());

    comments.add(Mockito.mock(CommentJson.class));
    comments.add(Mockito.mock(CommentJson.class));
    Mockito.when(commentService.findByApplicationId(applicationId)).thenReturn(comments);

    Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);
  }


  @Test
  public void testGetFullyPopulatedApplication() {
    Mockito.when(applicationMapper.mapApplicationToJson(application)).thenReturn(new ApplicationJson());
    Mockito.when(currentUser.getAssignedRoles()).thenReturn(Collections.singletonList(RoleType.ROLE_PROCESS_APPLICATION));
    ApplicationJsonService applicationJsonService = new ApplicationJsonService(
        applicationMapper,
        projectService,
        userService,
        locationService,
        attachmentService,
        commentService,
        terminationService);
    ApplicationJson applicationJson = applicationJsonService.getFullyPopulatedApplication(application);
    Assert.assertEquals(projectJson, applicationJson.getProject());
    Assert.assertEquals(userJson, applicationJson.getOwner());
    Assert.assertEquals(0, applicationJson.getAttachmentList().size());
    Assert.assertEquals(2, applicationJson.getComments().size());
  }
}
