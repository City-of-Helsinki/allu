package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.testUtils.TestCommon;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class AttachmentDaoTest {

  @Autowired
  private AttachmentDao attachmentDao;

  @Autowired
  private ApplicationDao applicationDao;

  @Autowired
  TestCommon testCommon;

  private AttachmentInfo dummy;
  private Application application;
  private Application newApplication;

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();

    newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    application = applicationDao.insert(newApplication);
    // dummy will be used as a no-match info in some tests:
    dummy = newInfo();
    dummy.setName("NO MATCH");
  }

  /**
   * Test that attachment can be inserted
   *
   * @throws Exception
   */

  @Test
  public void testInsert() throws Exception {
    AttachmentInfo info = newInfo();
    byte[] data = generateTestData(3243);
    AttachmentInfo inserted = attachmentDao.insert(info, data);
    Assert.assertEquals(inserted.getSize().longValue(), data.length);
  }

  /**
   * Test that search by application ID works
   *
   * @throws Exception
   */

  @Test
  public void testFindByApplication() throws Exception {
    // Setup: store a few attachments for various applications:
    List<AttachmentInfo> attachmentInfos = storeInitialAttachments();
    // Add some extra attachments for test application
    AttachmentInfo info = attachmentInfos.get(10);
    for (int i = 20; i < 22; ++i) {
      info.setName(String.format("Attachment_%d.txt", i));
      attachmentDao.insert(info, generateTestData(5432));
    }
    // Now there should be attachments 10, 20, and 21 for test application
    List<AttachmentInfo> results = attachmentDao.findByApplication(info.getApplicationId());
    String expectedNames[] = { "Attachment_10.txt", "Attachment_20.txt", "Attachment_21.txt" };
    Assert.assertEquals(expectedNames.length,
        results.stream().mapToInt(a -> Arrays.asList(expectedNames).contains(a.getName()) ? 1 : 0).sum());
  }

  /**
   * Test that search by attachment ID works
   *
   * @throws Exception
   */

  @Test
  public void testFindById() throws Exception {
    // Setup: store a few attachments for various applications. Remember the
    // stored infos:
    List<AttachmentInfo> stored = storeInitialAttachments();
    // Test: find each stored info and verify that it matches the stored one.
    for (AttachmentInfo i : stored) {
      assertEquals(i, attachmentDao.findById(i.getId()).orElse(dummy));
    }
  }

  /**
   * Test that deleting works
   *
   * @throws Exception
   */

  @Test
  public void testDelete() throws Exception {
    // Setup: store a few attachments for various applications. Remember the
    // stored infos:
    List<AttachmentInfo> stored = storeInitialAttachments();
    // Test: delete first attachment and verify that it can't be retrieved.
    int id = stored.get(0).getId();
    attachmentDao.delete(id);
    Assert.assertFalse(attachmentDao.findById(id).isPresent());
    // Make sure the others still exist
    stored.remove(0);
    for (AttachmentInfo i : stored) {
      assertEquals(i, attachmentDao.findById(i.getId()).orElse(dummy));
    }
  }

  /**
   * Test that update works
   *
   * @throws Exception
   */
  @Test
  public void testUpdate() throws Exception {
    // Setup: store a few attachments for various applications. Remember the
    // stored infos:
    List<AttachmentInfo> stored = storeInitialAttachments();
    // Test: change first attachment's name:
    stored.get(0).setName("New name");
    attachmentDao.update(stored.get(0).getId(), stored.get(0));
    // Make sure all items still exist:
    for (AttachmentInfo i : stored) {
      assertEquals(i, attachmentDao.findById(i.getId()).orElse(dummy));
    }
  }

  /**
   * Test that getting data works
   *
   */
  @Test
  public void testGetData() {
    // Setup create attachment info and store data to it
    AttachmentInfo info = newInfo();
    byte[] testData = generateTestData(543210);
    Assert.assertEquals(543210, testData.length);
    info = attachmentDao.insert(info, testData);
    int goodId = info.getId();
    // store another info with different data:
    byte[] otherData = generateTestData(10);
    info = attachmentDao.insert(info, otherData);
    int otherId = info.getId();
    // Test: stored data should be readable and equal to original
    Optional<byte[]> readData = attachmentDao.getData(goodId);
    Assert.assertTrue(readData.isPresent());
    Assert.assertArrayEquals(testData, readData.get());
    // Test: reading data from other info should return other data
    readData = attachmentDao.getData(otherId);
    Assert.assertTrue(readData.isPresent());
    Assert.assertArrayEquals(otherData, readData.get());
    // Test: reading data from non-existent info should also return null:
    readData = attachmentDao.getData(otherId + goodId);
    Assert.assertFalse(readData.isPresent());
  }

  // Setup helper: store a bunch of attachment infos
  private List<AttachmentInfo> storeInitialAttachments() {
    AttachmentInfo info = newInfo();
    List<AttachmentInfo> stored = new ArrayList<>();
    for (int i = 0; i < 20; ++i) {
      info.setApplicationId(applicationDao.insert(newApplication).getId());
      info.setName(String.format("Attachment_%d.txt", i));
      info.setDescription(String.format("Attachment %d", i));
      stored.add(attachmentDao.insert(info, generateTestData(4321)));
    }
    return stored;
  }

  private AttachmentInfo newInfo() {
    AttachmentInfo info = new AttachmentInfo();
    info.setType(AttachmentType.ADDED_BY_CUSTOMER);
    info.setApplicationId(application.getId());
    info.setCreationTime(ZonedDateTime.now());
    info.setId(313);
    info.setName("Test_attachment.pdf");
    info.setDescription("Test attachment");
    return info;
  }

  private void assertEquals(AttachmentInfo expected, AttachmentInfo actual) {
    Assert.assertEquals(expected.getId(), actual.getId());
    Assert.assertEquals(expected.getApplicationId(), actual.getApplicationId());
    Assert.assertEquals(expected.getName(), actual.getName());
    Assert.assertEquals(expected.getDescription(), actual.getDescription());
    Assert.assertEquals(expected.getSize(), actual.getSize());
    Assert.assertEquals(expected.getCreationTime(), actual.getCreationTime());
  }

  private byte[] generateTestData(int size) {
    byte[] data = new byte[size];
    for (int i = 0; i < size; ++i) {
      data[i] = (byte) (i % 256);
    }
    return data;
  }
}
