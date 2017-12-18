package fi.hel.allu.servicecore.service;

import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import fi.hel.allu.servicecore.config.ApplicationProperties;
import fi.hel.allu.servicecore.domain.AttachmentInfoJson;
import fi.hel.allu.servicecore.domain.UserJson;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for AttachmentService API
 *
 * Simply verify that all API calls result in proper calls to RestTemplate.
 */
public class AttachmentServiceTest {

  protected AttachmentService attachmentService;
  private ApplicationProperties applicationProperties;
  private RestTemplate restTemplate;
  private UserService userService;

  private final int USER_ID = 1;
  private final int APPLICATION_ID = 1234;

  @Before
  public void setUp() {
    applicationProperties = Mockito.mock(ApplicationProperties.class);
    restTemplate = Mockito.mock(RestTemplate.class);
    userService = Mockito.mock(UserService.class);
    attachmentService = new AttachmentService(applicationProperties, restTemplate, userService);
    UserJson userJson = new UserJson();
    userJson.setId(USER_ID);
    userJson.setRealName("real name");
    Mockito.when(userService.getCurrentUser()).thenReturn(userJson);
    Mockito.when(userService.findUserById(USER_ID)).thenReturn(userJson);
  }

  @Test
  public void testAddAttachment() throws IllegalArgumentException, IOException {
    Mockito.when(restTemplate.exchange(
        Mockito.anyString(),
        Mockito.eq(HttpMethod.POST),
        Mockito.any(HttpEntity.class),
        Mockito.eq(AttachmentInfo.class),
        Mockito.eq(99))).thenAnswer(
            (Answer<ResponseEntity<AttachmentInfo>>) invocation -> new ResponseEntity<>(createMockAttachmentInfo(),
                HttpStatus.CREATED));

    final int ITEMS = 5;
    AttachmentInfoJson infos[] = new AttachmentInfoJson[ITEMS];
    MultipartFile files[] = new MultipartFile[ITEMS];
    for (int i = 0; i < ITEMS; ++i) {
      infos[i] = newAttachmentInfoJson();
      files[i] = new MockMultipartFile("dumdedoo.bin", generateMockData(4321));
    }
    List<AttachmentInfoJson> results = attachmentService.addAttachments(99, infos, files);

    for (AttachmentInfoJson result : results) {
      checkThatIsMockResult(result);
    }
    assertEquals(ITEMS, results.size());
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
    attachmentService.deleteAttachment(123, 1);
    Mockito.verify(restTemplate).delete(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
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
      Matchers.eq(applicationProperties.getModelServiceUrl(ApplicationProperties.PATH_MODEL_APPLICATION_FIND_ATTACHMENTS_BY_APPLICATION)),
      Matchers.eq(AttachmentInfo[].class),
      Matchers.anyInt())).thenReturn(responseEntity);

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
