package fi.hel.allu.servicecore.service;

import fi.hel.allu.common.domain.types.ApplicationTagType;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.domain.types.RoleType;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.types.ChangeType;
import fi.hel.allu.model.domain.ChangeHistoryItem;
import fi.hel.allu.model.domain.FieldChange;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.ChangeHistoryItemJson;
import fi.hel.allu.servicecore.domain.ApplicationJson;
import fi.hel.allu.servicecore.domain.ApplicationTagJson;
import fi.hel.allu.servicecore.domain.UserJson;

import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ApplicationHistoryServiceTest extends MockServices {

  @Mock
  private ApplicationProperties mockApplicationProperties;
  @Mock
  private RestTemplate mockRestTemplate;
  @Mock
  private UserService mockUserService;
  @Mock
  private UserJson mockUserJson;

  private ApplicationHistoryService applicationHistoryService;

  private static final String APPLICATION_HISTORY_URL = "ApplicationHistoryUrl";
  private static final String ADD_APPLICATION_HISTORY_URL = "AddApplicationHistoryUrl";
  private static final int MOCK_USER_ID = 911;

  private ChangeHistoryItem capturedChange = null;

  private ChangeHistoryItem captureChange(ChangeHistoryItem change) {
    capturedChange = change;
    return capturedChange;
  }

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mockito.when(mockApplicationProperties.getApplicationHistoryUrl()).thenReturn(APPLICATION_HISTORY_URL);
    Mockito.when(mockApplicationProperties.getAddApplicationHistoryUrl()).thenReturn(ADD_APPLICATION_HISTORY_URL);
    Mockito.when(mockUserService.getCurrentUser()).thenReturn(mockUserJson);
    Mockito.when(mockUserJson.getId()).thenReturn(MOCK_USER_ID);
    applicationHistoryService = new ApplicationHistoryService(mockApplicationProperties, mockRestTemplate,
        mockUserService);
  }

  @Test
  public void testGetChanges() {
    final int APPLICATION_ID = 432;
    ChangeHistoryItem[] changes = new ChangeHistoryItem[] {
        new ChangeHistoryItem(12, ChangeType.CONTENTS_CHANGED, null, ZonedDateTime.parse("2017-02-03T10:15:30+02:00"),
            Arrays.asList(new FieldChange("foo", "oldFoo", "newFoo"))) };
    Mockito.when(mockRestTemplate.getForObject(Mockito.eq(APPLICATION_HISTORY_URL),
        Mockito.eq(ChangeHistoryItem[].class), Mockito.eq(APPLICATION_ID))).thenReturn(changes);

    List<ChangeHistoryItemJson> result = applicationHistoryService.getChanges(APPLICATION_ID);
    Assert.assertNotNull(result);
    Assert.assertEquals(1, result.size());
    Assert.assertEquals(ChangeType.CONTENTS_CHANGED, result.get(0).getChangeType());
    Assert.assertEquals("foo", result.get(0).getFieldChanges().get(0).getFieldName());
  }

  /*
   * Test that adding field changes properly detects two changed fields
   */
  @Test
  public void testAddFieldChanges() {
    final int APPLICATION_ID = 432;
    setupChangeCapture(APPLICATION_ID);

    ApplicationJson oldApplication = createMockApplicationJson(APPLICATION_ID);
    oldApplication.getProject().setAdditionalInfo(null);

    ApplicationJson newApplication = createMockApplicationJson(APPLICATION_ID);
    newApplication.setName("Changed Name");
    newApplication.getProject().setAdditionalInfo("new.email@company.org");
    applicationHistoryService.addFieldChanges(APPLICATION_ID, oldApplication, newApplication);

    Assert.assertNotNull(capturedChange);
    Assert.assertEquals(ChangeType.CONTENTS_CHANGED, capturedChange.getChangeType());
    Assert.assertEquals(MOCK_USER_ID, capturedChange.getUserId().intValue());
    List<FieldChange> fieldChanges = capturedChange.getFieldChanges();
    Assert.assertNotNull(fieldChanges);
    Assert.assertEquals(1, fieldChanges.stream().filter(fc -> fc.getFieldName().equals("/name")).count());
    Assert.assertEquals("Changed Name", fieldChanges.stream().filter(fc -> fc.getFieldName().equals("/name"))
        .map(fc -> fc.getNewValue()).findFirst().orElse(null));
    Assert.assertEquals(1, fieldChanges.stream().filter(fc -> fc.getFieldName().equals("/project/additionalInfo")).count());
    Assert.assertEquals("new.email@company.org",
        fieldChanges.stream().filter(fc -> fc.getFieldName().equals("/project/additionalInfo")).map(fc -> fc.getNewValue())
            .findFirst().orElse(null));
  }

  /*
   * Test adding a status change
   */
  @Test
  public void testAddStatusChange() {
    final int APPLICATION_ID = 432;
    setupChangeCapture(APPLICATION_ID);

    applicationHistoryService.addStatusChange(APPLICATION_ID, StatusType.PRE_RESERVED);

    Assert.assertNotNull(capturedChange);
    Assert.assertEquals(ChangeType.STATUS_CHANGED, capturedChange.getChangeType());
    Assert.assertEquals(StatusType.PRE_RESERVED, capturedChange.getNewStatus());
  }

  @Test
  public void testAddApplicationCreated() {
    final int APPLICATION_ID = 432;
    setupChangeCapture(APPLICATION_ID);

    applicationHistoryService.addApplicationCreated(APPLICATION_ID);
    Assert.assertNotNull(capturedChange);
    Assert.assertEquals(ChangeType.CREATED, capturedChange.getChangeType());
  }

  /* Make sure that applicationTag id is skipped from changes */
  @Test
  public void testSkipApplicationTagId() {
    final int APPLICATION_ID = 432;
    setupChangeCapture(APPLICATION_ID);

    ApplicationJson oldApplication = createMockApplicationJson(APPLICATION_ID);
    oldApplication.setApplicationTags(Arrays.asList(new ApplicationTagJson(11, ApplicationTagType.DEPOSIT_PAID, ZonedDateTime.now())));
    ApplicationJson newApplication = createMockApplicationJson(APPLICATION_ID);
    newApplication.setApplicationTags(
        Arrays.asList(new ApplicationTagJson(12, ApplicationTagType.DEPOSIT_PAID, ZonedDateTime.now().plusDays(1))));
    applicationHistoryService.addFieldChanges(APPLICATION_ID, oldApplication, newApplication);

    Assert.assertNotNull(capturedChange);
    Assert.assertEquals(ChangeType.CONTENTS_CHANGED, capturedChange.getChangeType());
    Assert.assertEquals(MOCK_USER_ID, capturedChange.getUserId().intValue());
    List<FieldChange> fieldChanges = capturedChange.getFieldChanges();
    List<FieldChange> tagChanges = fieldChanges.stream()
        .filter(chg -> chg.getFieldName().startsWith("/applicationTags/")).collect(Collectors.toList());
    Assert.assertEquals(2, tagChanges.size());
    Assert.assertEquals(0, tagChanges.stream().filter(c -> c.getFieldName().endsWith("/id")).count());
  }

  /* Make sure that owner changes are not generated */
  @Test
  public void testNoOwnerChanges() {
    final int APPLICATION_ID = 432;
    setupChangeCapture(APPLICATION_ID);

    ApplicationJson oldApplication = createMockApplicationJson(APPLICATION_ID);
    oldApplication.setOwner(new UserJson(123, "pera", "Pertti", "pera@xxx.eu", "perustaja", true, null,
        Collections.singletonList(ApplicationType.AREA_RENTAL), Collections.singletonList(RoleType.ROLE_DECISION),
        Collections.singletonList(1)));
    oldApplication.setName("Old application");
    ApplicationJson newApplication = createMockApplicationJson(APPLICATION_ID);
    newApplication.setOwner(new UserJson(123, "riku", "Risto", "rike@xxx.ca", "romuttaja", true, null,
        Collections.singletonList(ApplicationType.CABLE_REPORT),
        Collections.singletonList(RoleType.ROLE_CREATE_APPLICATION), Collections.singletonList(2)));
    newApplication.setName("New application");

    applicationHistoryService.addFieldChanges(APPLICATION_ID, oldApplication, newApplication);

    Assert.assertNotNull(capturedChange);
    Assert.assertEquals(ChangeType.CONTENTS_CHANGED, capturedChange.getChangeType());
    Assert.assertEquals(MOCK_USER_ID, capturedChange.getUserId().intValue());
    List<FieldChange> fieldChanges = capturedChange.getFieldChanges();
    List<FieldChange> tagChanges = fieldChanges.stream()
        .filter(chg -> chg.getFieldName().startsWith("/handler/")).collect(Collectors.toList());
    fieldChanges.forEach(change -> System.out.println(change.toString()));
    Assert.assertEquals(0, tagChanges.size());
  }

  // TODO: add tests to verify that ApplicationJson contains fields
  // "/applicationTags/*/id" and "/extension/infoEntries/*/id"

  /*
   * Setup Mockito to store ChangeHistoryItem to capturedChange when
   * restTemplate.postForObject is called.
   */
  private void setupChangeCapture(int applicationId) {
    Mockito
        .when(mockRestTemplate.postForObject(Mockito.eq(ADD_APPLICATION_HISTORY_URL), Mockito.any(),
            Mockito.eq(Void.class), Mockito.eq(applicationId)))
        .then(invocation -> captureChange(invocation.getArgumentAt(1, ChangeHistoryItem.class)));
    capturedChange = null;
  }
}
