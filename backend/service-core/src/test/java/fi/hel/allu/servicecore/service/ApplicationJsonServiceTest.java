package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.CustomerRoleType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.Customer;
import fi.hel.allu.model.domain.CustomerWithContacts;
import fi.hel.allu.servicecore.domain.*;
import fi.hel.allu.servicecore.mapper.ApplicationMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationJsonServiceTest {

  private ApplicationMapper applicationMapper;
  private ProjectService projectService;
  private CustomerService customerService;
  private ContactService contactService;
  private MetaService metaService;
  private UserService userService;
  private LocationService locationService;
  private AttachmentService attachmentService;
  private CommentService commentService;

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
  private LocationJson locationJson = Mockito.mock(LocationJson.class);
  private List<CommentJson> comments = new ArrayList<>();
  private UserJson currentUser = Mockito.mock(UserJson.class);

  @Before
  public void init() {
    applicationMapper = Mockito.mock(ApplicationMapper.class);
    projectService = Mockito.mock(ProjectService.class);
    customerService = Mockito.mock(CustomerService.class);
    contactService = Mockito.mock(ContactService.class);
    metaService = Mockito.mock(MetaService.class);
    userService = Mockito.mock(UserService.class);
    locationService = Mockito.mock(LocationService.class);
    attachmentService = Mockito.mock(AttachmentService.class);
    commentService = Mockito.mock(CommentService.class);

    Mockito.when(application.getId()).thenReturn(applicationId);
    Customer customer = new Customer();
    customer.setId(customerId);
    Mockito.when(application.getCustomersWithContacts())
        .thenReturn(Collections.singletonList(new CustomerWithContacts(CustomerRoleType.APPLICANT, customer, Collections.emptyList())));
    Mockito.when(application.getHandler()).thenReturn(userId);
    Mockito.when(application.getProjectId()).thenReturn(projectId);
    Mockito.when(application.getType()).thenReturn(ApplicationType.SHORT_TERM_RENTAL);
    Mockito.when(application.getMetadataVersion()).thenReturn(1);

    Mockito.when(projectService.findByIds(Collections.singletonList(projectId))).thenReturn(Collections.singletonList(projectJson));
    Mockito.when(userService.findUserById(userId)).thenReturn(userJson);
    Mockito.when(locationService.findLocationsByApplication(applicationId)).thenReturn(Collections.singletonList(locationJson));
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
        customerService,
        contactService,
        metaService,
        userService,
        locationService,
        attachmentService,
        commentService);
    ApplicationJson applicationJson = applicationJsonService.getFullyPopulatedApplication(application);
    Assert.assertEquals(projectJson, applicationJson.getProject());
    Assert.assertEquals(userJson, applicationJson.getHandler());
    Assert.assertEquals(locationJson, applicationJson.getLocations().get(0));
    Assert.assertEquals(0, applicationJson.getAttachmentList().size());
    Assert.assertEquals(2, applicationJson.getComments().size());
  }
}
