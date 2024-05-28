package fi.hel.allu.model.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.dao.AttachmentDao;
import fi.hel.allu.model.dao.ConfigurationDao;
import fi.hel.allu.model.domain.*;
import fi.hel.allu.model.domain.user.User;
import fi.hel.allu.model.testUtils.TestCommon;
import fi.hel.allu.model.testUtils.WebTestCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for attachment controller APIs
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class AttachmentControllerTest {

  private Application application;
  private User user;
  @Autowired
  WebTestCommon wtc;

  @Autowired
  ObjectMapper objectMapper;

  @Autowired
  TestCommon testCommon;

  @Mock
  private AttachmentDao attachmentDao;

  @InjectMocks
  private AttachmentController attachmentController;

  @Mock
  ConfigurationDao configurationDao;

  @Before
  public void setup() throws Exception {
    wtc.setup();
    user = testCommon.insertUser("testuser");

    application = testCommon.dummyOutdoorApplication("Test Application", "Handlaaja");
    ResultActions resultActions = wtc.perform(post("/applications?userId=" + user.getId()), application).andExpect(status().isOk());
    application = wtc.parseObjectFromResult(resultActions, Application.class);

    when(configurationDao.findByKey(ConfigurationKey.ATTACHMENT_ALLOWED_TYPES)).thenReturn(
      List.of(new Configuration(ConfigurationType.TEXT,ConfigurationKey.ATTACHMENT_ALLOWED_TYPES, ".pdf, .xlsx"))
    );

    when(configurationDao.findByKey(ConfigurationKey.ATTACHMENT_MAX_SIZE_MB)).thenReturn(
      List.of(new Configuration(ConfigurationType.TEXT,ConfigurationKey.ATTACHMENT_MAX_SIZE_MB, "5"))
    );
  }

  /********************************
   * Attachment tests
   ********************************/

  // Helper for inserting attachment info
  private AttachmentInfo insertAttachmentInfo(int applicationId, AttachmentInfo info, byte[] data) throws Exception {
    String infoJson = objectMapper.writeValueAsString(info);
    MockMultipartFile infoPart = new MockMultipartFile("info", "", "application/json", infoJson.getBytes());
    if (data == null) {
      data = new byte[100];
    }
    ResultActions resultActions =
        wtc.perform(multipart("/attachments/applications/" + applicationId).file(infoPart).file("data", data));
    resultActions.andExpect(status().isCreated());
    return wtc.parseObjectFromResult(resultActions, AttachmentInfo.class);
  }


  /**
   * Add attachment
   */
  @Test
  public void testAddAttachment_Success() throws IOException {
    int applicationId = 1;
    AttachmentInfo attachmentInfo = new AttachmentInfo();
    attachmentInfo.setName("filename.pdf");
    MultipartFile data = new MockMultipartFile("data", "filename.pdf", "text/plain", "some xml".getBytes());
    AttachmentInfo inserted = new AttachmentInfo();
    when(attachmentDao.insert(anyInt(), any(AttachmentInfo.class), any())).thenReturn(inserted);

    ResponseEntity<AttachmentInfo> response = attachmentController.addAttachment(applicationId, attachmentInfo, data);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(inserted, response.getBody());
  }

  /**
   * Add too large attachment
   */
  @Test
  public void testAddAttachment_FileTooLarge() throws IOException {
    AttachmentInfo attachmentInfo = new AttachmentInfo();
    attachmentInfo.setName("largefile.pdf");

    byte[] fileContent = new byte[6 * 1024 * 1024]; // 6MB
    MultipartFile data = new MockMultipartFile("data", "largefile.pdf", "text/plain", fileContent);

    ResponseEntity<AttachmentInfo> response = attachmentController.addAttachment(1, attachmentInfo, data);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  /**
   * Add attachment that has non allowed file extension
   */
  @Test
  public void testAddAttachment_NotAllowedFileExtension() throws IOException {
    AttachmentInfo attachmentInfo = new AttachmentInfo();
    attachmentInfo.setName("not-allowed.psd");
    byte[] fileContent = new byte[1024 * 1024]; // 1MB
    MultipartFile data = new MockMultipartFile("data", "not-allowed.psd", "text/plain", fileContent);

    ResponseEntity<AttachmentInfo> response = attachmentController.addAttachment(1, attachmentInfo, data);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  /**
   * Test that adding an attachment info works
   *
   * @throws Exception
   */
  @Test
  public void testAddAndGetAttachment() throws Exception {
    // Test: add attachment info and make sure result is CREATED
    AttachmentInfo info = newInfo();
    AttachmentInfo stored = insertAttachmentInfo(application.getId(), info, null);
    ResultActions resultActions = wtc
        .perform(get(String.format("/attachments/%d", stored.getId())));
    resultActions.andExpect(status().isOk());
    AttachmentInfo retrieved = wtc.parseObjectFromResult(resultActions, AttachmentInfo.class);
    verifyEqual(stored, retrieved);
    assertEquals(info.getName(), stored.getName());
  }

  /**
   * Test that updating attachment works,
   *
   * @throws Exception
   */
  @Test
  public void testUpdateAttachment() throws Exception {
    // Setup: insert an attachment info
    AttachmentInfo stored = insertAttachmentInfo(application.getId(), newInfo(), null);
    // Test: Update the attachment info
    String infoUri = String.format("/attachments/%d", stored.getId());
    AttachmentInfo updatedInfo = newInfo();
    updatedInfo.setName("Muokattu hakemus");
    ResultActions resultActions = wtc.perform(put(infoUri), updatedInfo).andExpect(status().isOk());
    AttachmentInfo updateResult = wtc.parseObjectFromResult(resultActions, AttachmentInfo.class);
    assertEquals(updatedInfo.getName(), updateResult.getName());
    // Verify that reading the same attachment info now gives the updated data
    resultActions = wtc.perform(get(infoUri)).andExpect(status().isOk());
    AttachmentInfo readInfo = wtc.parseObjectFromResult(resultActions, AttachmentInfo.class);
    verifyEqual(updateResult, readInfo);
  }

  /**
   * Test that attachment can be deleted.
   *
   * @throws Exception
   */
  @Test
  public void testDeleteAttachment() throws Exception {
    // Setup: insert an attachment info
    AttachmentInfo stored = insertAttachmentInfo(application.getId(), newInfo(), null);
    // Test: delete the attachment and verify that it doesn't exist anymore
    String deleteInfoUri = String.format("/attachments/applications/%d/%d", application.getId(), stored.getId());
    String infoUri = String.format("/attachments/%d", application.getId(), stored.getId());
    wtc.perform(delete(deleteInfoUri)).andExpect(status().isOk());
    wtc.perform(get(infoUri)).andExpect(status().isNotFound());
  }

  @Test
  public void testDeleteOrUpdateDefaultAsNormalAttachment() throws Exception {
    DefaultAttachmentInfo info = newDefaultInfo();
    DefaultAttachmentInfo inserted = insertDefaultAttachmentInfo(info, info.toString().getBytes());
    // try to delete and update the attachment as normal attachment (should fail)
    String deleteUri = String.format("/attachments/applications/%d/%d", application.getId(), inserted.getId());
    String updateUri = String.format("/attachments/%d", inserted.getId());
    wtc.perform(delete(deleteUri)).andExpect(status().is4xxClientError());
    wtc.perform(put(updateUri), inserted).andExpect(status().is4xxClientError());
  }

  @Test
  public void testDeleteDefaultAttachmentFromApplication() throws Exception {
    DefaultAttachmentInfo info = newDefaultInfo();
    DefaultAttachmentInfo inserted = insertDefaultAttachmentInfo(info, info.toString().getBytes());
    insertAttachmentInfo(application.getId(), inserted, new byte[1]);
    // make sure application has the attachment
    ResultActions resultActions = wtc.perform(get(String.format("/applications/%d/attachments", application.getId())));
    resultActions.andExpect(status().isOk());
    AttachmentInfo[] applicationAttachments = wtc.parseObjectFromResult(resultActions, AttachmentInfo[].class);
    Assert.assertEquals(1, applicationAttachments.length);
    verifyEqual(inserted, applicationAttachments[0]);
    // delete the attachment from application and verify that it doesn't exist anymore
    String deleteUri = String.format("/attachments/applications/%d/%d", application.getId(), inserted.getId());
    wtc.perform(delete(deleteUri)).andExpect(status().isOk());
    resultActions = wtc.perform(get(String.format("/applications/%d/attachments", application.getId())));
    resultActions.andExpect(status().isOk());
    applicationAttachments = wtc.parseObjectFromResult(resultActions, AttachmentInfo[].class);
    Assert.assertEquals(0, applicationAttachments.length);
  }

  /**
   * Test that setting attachment data works
   *
   * @throws Exception
   */
  @Test
  public void testGetAttachmentData() throws Exception {
    // Setup: insert an attachment with some content
    byte[] content = new byte[12345];
    for (int i = 0; i < content.length; ++i) {
      content[i] = (byte) (i);
    }
    AttachmentInfo stored = insertAttachmentInfo(application.getId(), newInfo(), content);
    // Verify that the attachment's content can now be read and is the same as
    // the stored one:
    String uri = String.format("/attachments/%d/data", stored.getId());
    ResultActions resultActions = wtc.perform(get(uri)).andExpect(status().isOk());
    byte[] readContent = resultActions.andReturn().getResponse().getContentAsByteArray();
    Assert.assertArrayEquals(content, readContent);
  }

  private AttachmentInfo newInfo() {
    AttachmentInfo info = new AttachmentInfo();
    info.setType(AttachmentType.ADDED_BY_CUSTOMER);
    info.setCreationTime(ZonedDateTime.now());
    info.setName("Test_attachment.pdf");
    info.setDescription("A test attachment");
    return info;
  }

  private void verifyEqual(AttachmentInfo expected, AttachmentInfo actual) {
    Assert.assertEquals(expected.getId(), actual.getId());
    Assert.assertEquals(expected.getName(), actual.getName());
    Assert.assertEquals(expected.getDescription(), actual.getDescription());
    Assert.assertEquals(expected.getCreationTime(), actual.getCreationTime());
  }

  /********************************
   * Default attachment tests
   ********************************/
  @Test
  public void testAddDefaultAttachment() throws Exception {
    // insert first default attachment
    DefaultAttachmentInfo info = newDefaultInfo();
    DefaultAttachmentInfo inserted = insertDefaultAttachmentInfo(info, info.toString().getBytes());
    Assert.assertEquals(info.getApplicationTypes(), inserted.getApplicationTypes());
    // insert second default attachment
    inserted = insertDefaultAttachmentInfo(info, info.toString().getBytes());
    ResultActions resultActions = wtc.perform(get("/attachments/default/applicationType/" + ApplicationType.EVENT.toString()));
    resultActions.andExpect(status().isOk());
    DefaultAttachmentInfo[] retrieved = wtc.parseObjectFromResult(resultActions, DefaultAttachmentInfo[].class);
    Assert.assertEquals(2, retrieved.length);
  }

  @Test
  public void testUpdateDefaultAttachment() throws Exception {
    DefaultAttachmentInfo info = newDefaultInfo();
    DefaultAttachmentInfo inserted = insertDefaultAttachmentInfo(info, info.toString().getBytes());
    String infoUri = String.format("/attachments/default/%d", inserted.getId());
    inserted.setName("Muokattu hakemus");
    inserted.setApplicationTypes(Collections.singletonList(ApplicationType.NOTE));
    ResultActions resultActions = wtc.perform(put(infoUri), inserted).andExpect(status().isOk());
    DefaultAttachmentInfo updateResult = wtc.parseObjectFromResult(resultActions, DefaultAttachmentInfo.class);
    Assert.assertEquals(inserted.getApplicationTypes(), updateResult.getApplicationTypes());
    Assert.assertEquals(inserted.getName(), updateResult.getName());
  }

  @Test
  public void testSearchDeleteDefaultAttachment() throws Exception {
    // insert new default attachment
    DefaultAttachmentInfo info = newDefaultInfo();
    DefaultAttachmentInfo inserted = insertDefaultAttachmentInfo(info, info.toString().getBytes());
    // search inserted
    ResultActions resultActions = wtc.perform(get("/attachments/default/applicationType/" + ApplicationType.EVENT.toString()));
    resultActions.andExpect(status().isOk());
    DefaultAttachmentInfo[] retrieved = wtc.parseObjectFromResult(resultActions, DefaultAttachmentInfo[].class);
    Assert.assertEquals(1, retrieved.length);
    // find all inserted
    resultActions = wtc.perform(get("/attachments/default"));
    resultActions.andExpect(status().isOk());
    retrieved = wtc.parseObjectFromResult(resultActions, DefaultAttachmentInfo[].class);
    Assert.assertEquals(1, retrieved.length);
    // delete inserted
    resultActions = wtc.perform(delete(String.format("/attachments/default/%d", inserted.getId())));
    resultActions.andExpect(status().isOk());
    // make sure deleted is gone
    resultActions = wtc.perform(get("/attachments/default/applicationType/" + ApplicationType.EVENT.toString()));
    resultActions.andExpect(status().isOk());
    retrieved = wtc.parseObjectFromResult(resultActions, DefaultAttachmentInfo[].class);
    Assert.assertEquals(0, retrieved.length);
  }

  @Test
  public void testDeleteDefaultAttachment() throws Exception {
    // insert new default attachment
    DefaultAttachmentInfo info = newDefaultInfo();
    DefaultAttachmentInfo inserted = insertDefaultAttachmentInfo(info, info.toString().getBytes());
    // add default as attachment to an application
    insertAttachmentInfo(application.getId(), inserted, new byte[1]);
    // delete inserted
    ResultActions resultActions = wtc.perform(delete(String.format("/attachments/default/%d", inserted.getId())));
    resultActions.andExpect(status().isOk());
    // make sure deleted is gone
    resultActions = wtc.perform(get("/attachments/default/applicationType/" + ApplicationType.EVENT.toString()));
    resultActions.andExpect(status().isOk());
    DefaultAttachmentInfo[] retrieved = wtc.parseObjectFromResult(resultActions, DefaultAttachmentInfo[].class);
    Assert.assertEquals(0, retrieved.length);
    // make sure application still has the attachment
    resultActions = wtc.perform(get(String.format("/applications/%d/attachments", application.getId())));
    resultActions.andExpect(status().isOk());
    AttachmentInfo[] applicationAttachments = wtc.parseObjectFromResult(resultActions, AttachmentInfo[].class);
    Assert.assertEquals(1, applicationAttachments.length);
  }

  private DefaultAttachmentInfo insertDefaultAttachmentInfo(DefaultAttachmentInfo info, byte[] data) throws Exception {
    String infoJson = objectMapper.writeValueAsString(info);
    MockMultipartFile infoPart = new MockMultipartFile("info", "", "application/json", infoJson.getBytes());
    if (data == null) {
      data = new byte[100];
    }
    ResultActions resultActions = wtc.perform(multipart("/attachments/default").file(infoPart).file("data", data));
    resultActions.andExpect(status().isCreated());
    return wtc.parseObjectFromResult(resultActions, DefaultAttachmentInfo.class);
  }

  private DefaultAttachmentInfo newDefaultInfo() {
    DefaultAttachmentInfo info = new DefaultAttachmentInfo();
    info.setType(AttachmentType.ADDED_BY_CUSTOMER);
    info.setCreationTime(ZonedDateTime.now());
    info.setName("Test_attachment.pdf");
    info.setDescription("A test attachment");
    info.setApplicationTypes(Collections.singletonList(ApplicationType.EVENT));
    return info;
  }
}
