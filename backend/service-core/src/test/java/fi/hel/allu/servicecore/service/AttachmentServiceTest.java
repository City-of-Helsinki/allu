package fi.hel.allu.servicecore.service;

import java.io.IOException;
import java.io.ObjectInputFilter;
import java.time.ZonedDateTime;
import java.util.List;

import fi.hel.allu.model.domain.ConfigurationKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.common.types.ApplicationNotificationType;
import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.domain.UserJson;
import fi.hel.allu.servicecore.event.ApplicationEventDispatcher;
import fi.hel.allu.servicecore.service.applicationhistory.ApplicationHistoryService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;

/**
 * Tests for AttachmentService API
 *
 * Simply verify that all API calls result in proper calls to RestTemplate.
 */
@RunWith(MockitoJUnitRunner.class)
public class AttachmentServiceTest {

  protected AttachmentService attachmentService;
  private RestTemplate restTemplate;
  private UserService userService;
  private ApplicationHistoryService applicationHistoryService = Mockito.mock(ApplicationHistoryService.class);
  private ApplicationEventDispatcher eventDispatcher = Mockito.mock(ApplicationEventDispatcher.class);
  private ConfigurationService configurationService;

  private final int USER_ID = 1;
  private final int APPLICATION_ID = 1234;

  @Before
  public void setUp() {
    restTemplate = Mockito.mock(RestTemplate.class);
    userService = Mockito.mock(UserService.class);
    configurationService = Mockito.mock(ConfigurationService.class);
    attachmentService = new AttachmentService(TestProperties.getProperties(), restTemplate, userService,
    applicationHistoryService, eventDispatcher, configurationService);
    UserJson userJson = new UserJson();
    userJson.setId(USER_ID);
    userJson.setRealName("real name");
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);
    Mockito.when(userService.findUserById(USER_ID)).thenReturn(userJson);
    Mockito.when(configurationService.getSingleValue(ConfigurationKey.ATTACHMENT_MAX_SIZE_MB)).thenReturn("5");
    Mockito.when(configurationService.getSingleValue(ConfigurationKey.ATTACHMENT_ALLOWED_TYPES)).thenReturn(".doc, .xls, .pdf, .bin");
    Mockito.when(restTemplate.exchange(
        Mockito.anyString(),
        Mockito.eq(HttpMethod.POST),
        Mockito.any(HttpEntity.class),
        Mockito.eq(AttachmentInfo.class),
        Mockito.eq(APPLICATION_ID))).thenAnswer(
            (Answer<ResponseEntity<AttachmentInfo>>) invocation -> new ResponseEntity<>(createMockAttachmentInfo(),
                HttpStatus.CREATED));
  }

  @Test
  public void testAddAttachment() throws IllegalArgumentException, IOException {
    final int ITEMS = 5;
    AttachmentInfoJson infos[] = new AttachmentInfoJson[ITEMS];
    MultipartFile files[] = new MultipartFile[ITEMS];
    for (int i = 0; i < ITEMS; ++i) {
      infos[i] = newAttachmentInfoJson();
      files[i] = new MockMultipartFile("dumdedoo.bin", generateMockData(4321));
    }
    List<AttachmentInfoJson> results = attachmentService.addAttachments(APPLICATION_ID, infos, files);

    for (AttachmentInfoJson result : results) {
      checkThatIsMockResult(result);
    }
    assertEquals(ITEMS, results.size());
  }


  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testAddAttachmentWithIncorrectType() throws IllegalArgumentException, IOException {
    final int ITEMS = 5;
    AttachmentInfoJson infos[] = new AttachmentInfoJson[ITEMS];
    MultipartFile files[] = new MultipartFile[ITEMS];
    AttachmentInfoJson attachmentInfo = newAttachmentInfoJson();
    attachmentInfo.setName("dumdedoo.mp3");
    for (int i = 0; i < ITEMS; ++i) {
      infos[i] = attachmentInfo;
      files[i] = new MockMultipartFile("dumdedoo.mp3", generateMockData(4321));
    }

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("Incorrect file type. Allowed file types are: .doc, .xls, .pdf, .bin");
    attachmentService.addAttachments(APPLICATION_ID, infos, files);
  }

  @Test
  public void testAddAttachmentWithTooLargeFile() throws IllegalArgumentException, IOException {
    final int ITEMS = 5;
    AttachmentInfoJson infos[] = new AttachmentInfoJson[ITEMS];
    MultipartFile files[] = new MultipartFile[ITEMS];
    AttachmentInfoJson attachmentInfo = newAttachmentInfoJson();
    attachmentInfo.setName("dumdedoo.doc");
    for (int i = 0; i < ITEMS; ++i) {
      infos[i] = attachmentInfo;
      files[i] = new MockMultipartFile("dumdedoo.doc", generateMockData(6 * 1024 * 1024));
    }

    thrown.expect(IllegalArgumentException.class);
    thrown.expectMessage("File size exceeds the maximum allowed limit of 5 MB");
    attachmentService.addAttachments(APPLICATION_ID, infos, files);
  }

  @Test
  public void shouldAddHistoryWhenAdded() throws IOException {
    AttachmentInfoJson infoJson = new AttachmentInfoJson();
    infoJson.setName("attachment name");
    infoJson.setType(AttachmentType.ADDED_BY_HANDLER);
    attachmentService.addAttachment(APPLICATION_ID, infoJson, new MockMultipartFile("attachment.bin", generateMockData(30)));
    Mockito.verify(applicationHistoryService, times(1)).addAttachmentAdded(APPLICATION_ID, infoJson.getName());
  }

  @Test
  public void shouldPublishApplicationEventWhenAdded() throws IOException {
    AttachmentInfoJson infoJson = new AttachmentInfoJson();
    infoJson.setName("attachment name");
    infoJson.setType(AttachmentType.ADDED_BY_HANDLER);
    attachmentService.addAttachment(APPLICATION_ID, infoJson, new MockMultipartFile("attachment.bin", generateMockData(30)));
    Mockito.verify(eventDispatcher, times(1)).dispatchUpdateEvent(anyInt(), anyInt(), eq(ApplicationNotificationType.ATTACHMENT_ADDED), eq(infoJson.getType().name()));
  }

  @Test
  public void testUpdateAttachment() {
    Mockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class),
        Mockito.eq(AttachmentInfo.class), Mockito.anyInt())).thenAnswer(
            (Answer<ResponseEntity<AttachmentInfo>>) invocation -> new ResponseEntity<>(createMockAttachmentInfo(),
                HttpStatus.OK));
    AttachmentInfoJson info = newAttachmentInfoJson();
    AttachmentInfoJson result = attachmentService.updateAttachment(1, info);
    checkThatIsMockResult(result);
  }

  @Test
  public void testGetAttachment() {
    Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(DefaultAttachmentInfo.class), Mockito.anyInt()))
        .thenReturn(createMockAttachmentInfo());
    AttachmentInfoJson result = attachmentService.getAttachment(1);
    checkThatIsMockResult(result);
  }

  @Test
  public void testDeleteAttachment() {
    Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(DefaultAttachmentInfo.class), Mockito.anyInt()))
    .thenReturn(createMockAttachmentInfo());
    attachmentService.deleteAttachment(123, 1);
    Mockito.verify(restTemplate).delete(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
  }

  @Test
  public void shouldAddHistoryWhenDeleted() {
    DefaultAttachmentInfo info = createMockAttachmentInfo();
    Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(DefaultAttachmentInfo.class), Mockito.anyInt()))
    .thenReturn(info);
    attachmentService.deleteAttachment(APPLICATION_ID, 2);
    Mockito.verify(applicationHistoryService, times(1)).addAttachmentRemoved(APPLICATION_ID, info.getName());
  }


  @Test
  public void testGetData() {
    Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(byte[].class), Mockito.eq(112)))
        .thenReturn(generateMockData(2222));
    byte[] data = attachmentService.getAttachmentData(112);
    Assert.assertArrayEquals(generateMockData(2222), data);
  }

  @Test
  public void testFindAttachmentsForApplication() {
    AttachmentInfo attachmentInfo = createMockAttachmentInfo();
    ResponseEntity<AttachmentInfo[]> responseEntity = Mockito.mock(ResponseEntity.class);
    Mockito.when(responseEntity.getBody()).thenReturn(new AttachmentInfo[] {attachmentInfo});
    Mockito.when(restTemplate.getForEntity(
      Mockito.eq(TestProperties.getProperties().getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION)),
      Mockito.eq(AttachmentInfo[].class),
      Mockito.anyInt())).thenReturn(responseEntity);

    List<AttachmentInfoJson> attachmentInfoJsons = attachmentService.findAttachmentsForApplication(APPLICATION_ID);
    assertEquals(1, attachmentInfoJsons.size());
    assertEquals(attachmentInfo.getId(), attachmentInfoJsons.get(0).getId());
  }


  private AttachmentInfoJson newAttachmentInfoJson() {
    AttachmentInfoJson attachmentInfoJson = new AttachmentInfoJson();
    attachmentInfoJson.setId(111);
    attachmentInfoJson.setName("Test_1.doc");
    attachmentInfoJson.setDescription("Test attachment");
    attachmentInfoJson.setSize(123456L);
    attachmentInfoJson.setCreationTime(ZonedDateTime.now());
    attachmentInfoJson.setType(AttachmentType.ADDED_BY_HANDLER);
    return attachmentInfoJson;
  }

  private void checkThatIsMockResult(AttachmentInfoJson result) {
    assertEquals("Mock attachment", result.getDescription());
    assertEquals("Mock.pdf", result.getName());
    assertEquals(12, result.getId().intValue());
  }

  private byte[] generateMockData(int size) {
    byte[] result = new byte[size];
    for (int i = 0; i < size; ++i) {
      result[i] = (byte) i;
    }
    return result;
  }

  private DefaultAttachmentInfo createMockAttachmentInfo() {
    DefaultAttachmentInfo attachmentInfo = new DefaultAttachmentInfo();
    attachmentInfo.setId(12);
    attachmentInfo.setUserId(USER_ID);
    attachmentInfo.setName("Mock.pdf");
    attachmentInfo.setDescription("Mock attachment");
    attachmentInfo.setCreationTime(ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]"));
    return attachmentInfo;
  }

}
