package fi.hel.allu.ui.service;

import fi.hel.allu.common.types.*;
import fi.hel.allu.model.domain.ApplicationChange;
import fi.hel.allu.model.domain.ApplicationFieldChange;
import fi.hel.allu.ui.config.ApplicationProperties;
import fi.hel.allu.ui.domain.ApplicationChangeJson;
import fi.hel.allu.ui.domain.ApplicationJson;
import fi.hel.allu.ui.domain.ApplicationTagJson;
import fi.hel.allu.ui.domain.UserJson;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

  private ApplicationChange capturedChange = null;

  private ApplicationChange captureChange(ApplicationChange change) {
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
    applicationHistoryService.setupPattern();
  }

  @Test
  public void testGetChanges() {
    final int APPLICATION_ID = 432;
    ApplicationChange[] changes = new ApplicationChange[] {
        new ApplicationChange(12, ChangeType.CONTENTS_CHANGED, null, ZonedDateTime.parse("2017-02-03T10:15:30+02:00"),
            Arrays.asList(new ApplicationFieldChange("foo", "oldFoo", "newFoo"))) };
    Mockito.when(mockRestTemplate.getForObject(Mockito.eq(APPLICATION_HISTORY_URL),
        Mockito.eq(ApplicationChange[].class), Mockito.eq(APPLICATION_ID))).thenReturn(changes);

    List<ApplicationChangeJson> result = applicationHistoryService.getChanges(APPLICATION_ID);
    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(ChangeType.CONTENTS_CHANGED, result.get(0).getChangeType());
    assertEquals("foo", result.get(0).getFieldChanges().get(0).getFieldName());
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

    assertNotNull(capturedChange);
    assertEquals(ChangeType.CONTENTS_CHANGED, capturedChange.getChangeType());
    assertEquals(MOCK_USER_ID, capturedChange.getUserId().intValue());
    List<ApplicationFieldChange> fieldChanges = capturedChange.getFieldChanges();
    assertNotNull(fieldChanges);
    assertEquals(1, fieldChanges.stream().filter(fc -> fc.getFieldName().equals("/name")).count());
    assertEquals("Changed Name", fieldChanges.stream().filter(fc -> fc.getFieldName().equals("/name"))
        .map(fc -> fc.getNewValue()).findFirst().orElse(null));
    assertEquals(1, fieldChanges.stream().filter(fc -> fc.getFieldName().equals("/project/additionalInfo")).count());
    assertEquals("new.email@company.org",
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

    assertNotNull(capturedChange);
    assertEquals(ChangeType.STATUS_CHANGED, capturedChange.getChangeType());
    assertEquals(StatusType.PRE_RESERVED, capturedChange.getNewStatus());
  }

  @Test
  public void testAddApplicationCreated() {
    final int APPLICATION_ID = 432;
    setupChangeCapture(APPLICATION_ID);

    applicationHistoryService.addApplicationCreated(APPLICATION_ID);
    assertNotNull(capturedChange);
    assertEquals(ChangeType.CREATED, capturedChange.getChangeType());
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

    assertNotNull(capturedChange);
    assertEquals(ChangeType.CONTENTS_CHANGED, capturedChange.getChangeType());
    assertEquals(MOCK_USER_ID, capturedChange.getUserId().intValue());
    List<ApplicationFieldChange> fieldChanges = capturedChange.getFieldChanges();
    List<ApplicationFieldChange> tagChanges = fieldChanges.stream()
        .filter(chg -> chg.getFieldName().startsWith("/applicationTags/")).collect(Collectors.toList());
    assertEquals(2, tagChanges.size());
    assertEquals(0, tagChanges.stream().filter(c -> c.getFieldName().endsWith("/id")).count());
  }

  /* Make sure that handler changes are not generated */
  @Test
  public void testNoHandlerChanges() {
    final int APPLICATION_ID = 432;
    setupChangeCapture(APPLICATION_ID);

    ApplicationJson oldApplication = createMockApplicationJson(APPLICATION_ID);
    oldApplication.setHandler(new UserJson(123, "pera", "Pertti", "pera@xxx.eu", "perustaja", true,
        Collections.singletonList(ApplicationType.AREA_RENTAL), Collections.singletonList(RoleType.ROLE_DECISION),
        Collections.singletonList(1)));
    ApplicationJson newApplication = createMockApplicationJson(APPLICATION_ID);
    newApplication.setHandler(new UserJson(123, "riku", "Risto", "rike@xxx.ca", "romuttaja", true,
        Collections.singletonList(ApplicationType.CABLE_REPORT),
        Collections.singletonList(RoleType.ROLE_CREATE_APPLICATION), Collections.singletonList(2)));
    applicationHistoryService.addFieldChanges(APPLICATION_ID, oldApplication, newApplication);

    assertNotNull(capturedChange);
    assertEquals(ChangeType.CONTENTS_CHANGED, capturedChange.getChangeType());
    assertEquals(MOCK_USER_ID, capturedChange.getUserId().intValue());
    List<ApplicationFieldChange> fieldChanges = capturedChange.getFieldChanges();
    List<ApplicationFieldChange> tagChanges = fieldChanges.stream()
        .filter(chg -> chg.getFieldName().startsWith("/handler/")).collect(Collectors.toList());
    assertEquals(0, tagChanges.size());
  }

  // TODO: add tests to verify that ApplicationJson contains fields
  // "/applicationTags/*/id" and "/extension/infoEntries/*/id"

  /*
   * Setup Mockito to store ApplicationChange to capturedChange when
   * restTemplate.postForObject is called.
   */
  private void setupChangeCapture(int applicationId) {
    Mockito
        .when(mockRestTemplate.postForObject(Mockito.eq(ADD_APPLICATION_HISTORY_URL), Mockito.any(),
            Mockito.eq(Void.class), Mockito.eq(applicationId)))
        .then(invocation -> captureChange(invocation.getArgumentAt(1, ApplicationChange.class)));
    capturedChange = null;
  }
}
