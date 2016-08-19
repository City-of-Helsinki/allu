package fi.hel.allu.ui.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.ui.domain.AttachmentInfoJson;

/**
 * Tests for AttachmentService API
 *
 * Simply verify that all API calls result in proper calls to RestTemplate.
 */
public class AttachmentServiceTest extends MockServices {

  @InjectMocks
  protected AttachmentService attachmentService;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    initSaveMocks();
    initSearchMocks();
  }

  @Test
  public void testAddAttachment() throws IllegalArgumentException, IOException {
    Mockito
        .when(restTemplate.postForObject(Mockito.any(String.class), Mockito.anyObject(),
            Mockito.eq(AttachmentInfo.class)))
        .thenAnswer((Answer<AttachmentInfo>) invocation -> createMockAttachmentInfo());
    final int ITEMS = 5;
    AttachmentInfoJson infos[] = new AttachmentInfoJson[ITEMS];
    MultipartFile files[] = new MultipartFile[ITEMS];
    for (int i = 0; i < ITEMS; ++i) {
      infos[i] = newAttachmentInfoJson();
      files[i] = new MockMultipartFile("dumdedoo.bin", generateMockData(4321));
    }
    List<AttachmentInfoJson> results = attachmentService.addAttachments(99, infos, files);
    Mockito.verify(restTemplate, Mockito.times(ITEMS)).exchange(Mockito.anyString(), Mockito.eq(HttpMethod.POST),
        Mockito.any(HttpEntity.class), Mockito.eq(String.class), Mockito.eq(12));
    for (AttachmentInfoJson result : results) {
      checkThatIsMockResult(result);
    }
    Assert.assertEquals(ITEMS, results.size());
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
    Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(AttachmentInfo.class), Mockito.anyInt()))
        .thenReturn(createMockAttachmentInfo());
    AttachmentInfoJson result = attachmentService.getAttachment(1);
    checkThatIsMockResult(result);
  }

  @Test
  public void testDeleteAttactment() {
    attachmentService.deleteAttachment(1);
    Mockito.verify(restTemplate).delete(Mockito.anyString(), Mockito.anyInt());
  }

  @Test
  public void testGetData() {
    Mockito.when(restTemplate.getForObject(Mockito.anyString(), Mockito.eq(byte[].class), Mockito.eq(112)))
        .thenReturn(generateMockData(2222));
    byte[] data = attachmentService.getAttachmentData(112);
    Assert.assertArrayEquals(generateMockData(2222), data);
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
    assertEquals(9999L, result.getSize().longValue());
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

  private AttachmentInfo createMockAttachmentInfo() {
    AttachmentInfo attachmentInfo = new AttachmentInfo();
    attachmentInfo.setId(12);
    attachmentInfo.setName("Mock.pdf");
    attachmentInfo.setDescription("Mock attachment");
    attachmentInfo.setSize(9999L);
    attachmentInfo.setCreationTime(ZonedDateTime.parse("2007-12-03T10:15:30+01:00[Europe/Paris]"));
    attachmentInfo.setApplicationId(1234);
    return attachmentInfo;
  }

}
