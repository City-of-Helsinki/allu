package fi.hel.allu.model.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.testUtils.WebTestCommon;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for attachment controller APIs
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class AttachmentControllerTest {

  @Autowired
  WebTestCommon wtc;

  @Autowired
  ObjectMapper objectMapper;

  @Before
  public void setup() throws Exception {
    wtc.setup();
  }

  // Helper for inserting attachment info
  private AttachmentInfo insertAttachmentInfo(AttachmentInfo info, byte[] data) throws Exception {
    String infoJson = objectMapper.writeValueAsString(info);
    MockMultipartFile infoPart = new MockMultipartFile("info", "", "application/json", infoJson.getBytes());
    if (data == null) {
      data = new byte[100];
    }
    ResultActions resultActions = wtc.perform(fileUpload("/attachments").file(infoPart).file("data", data));
    resultActions.andExpect(status().isCreated());
    return wtc.parseObjectFromResult(resultActions, AttachmentInfo.class);
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
    AttachmentInfo stored = insertAttachmentInfo(info, null);
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
    AttachmentInfo stored = insertAttachmentInfo(newInfo(), null);
    // Test: Update the attachment info
    String infoUri = String.format("/attachments/%d", stored.getId());
    AttachmentInfo updatedInfo = newInfo();
    updatedInfo.setName("Muokattu hakemus");
    updatedInfo.setSize(99210L);
    ResultActions resultActions = wtc.perform(put(infoUri), updatedInfo).andExpect(status().isOk());
    AttachmentInfo updateResult = wtc.parseObjectFromResult(resultActions, AttachmentInfo.class);
    assertEquals(updatedInfo.getName(), updateResult.getName());
    assertEquals(updatedInfo.getSize(), updateResult.getSize());
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
    AttachmentInfo stored = insertAttachmentInfo(newInfo(), null);
    // Test: delete the attachment and verify that it doesn't exist anymore
    String infoUri = String.format("/attachments/%d", stored.getId());
    wtc.perform(delete(infoUri)).andExpect(status().isOk());
    wtc.perform(get(infoUri)).andExpect(status().isNotFound());
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
    AttachmentInfo stored = insertAttachmentInfo(newInfo(), content);
    // Verify that the attachment's content can now be read and is the same as
    // the stored one:
    String uri = String.format("/attachments/%d/data", stored.getId());
    ResultActions resultActions = wtc.perform(get(uri)).andExpect(status().isOk());
    byte[] readContent = resultActions.andReturn().getResponse().getContentAsByteArray();
    Assert.assertArrayEquals(content, readContent);
  }

  private AttachmentInfo newInfo() {
    AttachmentInfo info = new AttachmentInfo();
    info.setApplicationId(123);
    info.setCreationTime(ZonedDateTime.now());
    info.setId(313);
    info.setName("Test_attachment.pdf");
    info.setDescription("A test attachment");
    return info;
  }

  private void verifyEqual(AttachmentInfo expected, AttachmentInfo actual) {
    Assert.assertEquals(expected.getId(), actual.getId());
    Assert.assertEquals(expected.getApplicationId(), actual.getApplicationId());
    Assert.assertEquals(expected.getName(), actual.getName());
    Assert.assertEquals(expected.getDescription(), actual.getDescription());
    Assert.assertEquals(expected.getSize(), actual.getSize());
    Assert.assertEquals(expected.getCreationTime(), actual.getCreationTime());
  }

}
