package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.types.ApplicationType;
import fi.hel.allu.common.types.AttachmentType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.AttachmentInfo;
import fi.hel.allu.model.domain.DefaultAttachmentInfo;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
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
    AttachmentInfo inserted = attachmentDao.insert(application.getId(), info, data);
    Assert.assertNotNull(inserted.getAttachmentDataId());
  }

  @Test
  public void testDefaultInsert() throws Exception {
    DefaultAttachmentInfo dai = newDefaultInfo();
    byte[] data = generateTestData(3243);
    DefaultAttachmentInfo inserted = attachmentDao.insertDefault(dai, data);
    Assert.assertNotNull(inserted.getAttachmentDataId());
    Assert.assertEquals(1, inserted.getApplicationTypes().size());
    Assert.assertEquals(ApplicationType.EVENT, inserted.getApplicationTypes().get(0));
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
    int applicationId = ((TestAttachmentInfo) attachmentInfos.get(10)).applicationId;
    for (int i = 20; i < 22; ++i) {
      info.setName(String.format("Attachment_%d.txt", i));
      attachmentDao.insert(applicationId, info, generateTestData(5432));
    }
    // Now there should be attachments 10, 20, and 21 for test application
    List<AttachmentInfo> results = attachmentDao.findByApplication(applicationId);
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

  @Test
  public void testFindDefaultById() throws Exception {
    DefaultAttachmentInfo dai = newDefaultInfo();
    byte[] data = generateTestData(3243);
    DefaultAttachmentInfo inserted = attachmentDao.insertDefault(dai, data);
    Assert.assertTrue(attachmentDao.findDefaultById(inserted.getId()).isPresent());
    // deleted should not be available anymore
    attachmentDao.deleteDefault(inserted.getId());
    Assert.assertFalse(attachmentDao.findDefaultById(inserted.getId()).isPresent());
  }

  @Test
  public void testFindDefault() throws Exception {
    DefaultAttachmentInfo dai = newDefaultInfo();
    byte[] data = generateTestData(3243);
    attachmentDao.insertDefault(dai, data);
    attachmentDao.insertDefault(dai, data);
    DefaultAttachmentInfo inserted3 = attachmentDao.insertDefault(dai, data);
    attachmentDao.deleteDefault(inserted3.getId());
    // deleted should not be available anymore
    Assert.assertEquals(2, attachmentDao.findDefault().size());
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
    TestAttachmentInfo testAttachmentInfo = (TestAttachmentInfo) stored.get(0);
    attachmentDao.delete(testAttachmentInfo.applicationId, testAttachmentInfo.getId());
    Assert.assertFalse(attachmentDao.findById(testAttachmentInfo.getId()).isPresent());
    // Make sure the others still exist
    stored.remove(0);
    for (AttachmentInfo i : stored) {
      assertEquals(i, attachmentDao.findById(i.getId()).orElse(dummy));
    }
  }

  @Test
  public void testDefaultDelete() throws Exception {
    DefaultAttachmentInfo dai = newDefaultInfo();
    byte[] data = generateTestData(3243);
    DefaultAttachmentInfo inserted = attachmentDao.insertDefault(dai, data);
    attachmentDao.deleteDefault(inserted.getId());
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

  @Test
  public void testDefaultUpdate() throws Exception {
    DefaultAttachmentInfo dai = newDefaultInfo();
    byte[] data = generateTestData(3243);
    DefaultAttachmentInfo inserted = attachmentDao.insertDefault(dai, data);
    inserted.setFixedLocationAreaId(2);
    DefaultAttachmentInfo updated = attachmentDao.updateDefault(inserted.getId(), inserted);
    Assert.assertEquals(1, updated.getApplicationTypes().size());
    Assert.assertEquals(2, (int) updated.getFixedLocationAreaId());
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
    info = attachmentDao.insert(application.getId(), info, testData);
    int goodId = info.getId();
    // store another info with different data:
    byte[] otherData = generateTestData(10);
    info = attachmentDao.insert(application.getId(), info, otherData);
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

  @Test
  public void testGetAttachmentSize() {
    AttachmentInfo info = newInfo();
    byte[] testData = generateTestData(543210);
    Assert.assertEquals(543210, testData.length);
    info = attachmentDao.insert(application.getId(), info, testData);
    int attachmentId = info.getId();
    Assert.assertEquals(attachmentDao.getSizeByAttachmentId(attachmentId).get(), Long.valueOf(543210));
  }

  @Test
  public void testLinkToApplication() {
    Application linkApplication1 = testCommon.dummyOutdoorApplication("Test Application", "Test Handler1");
    Application linkApplication2 = testCommon.dummyOutdoorApplication("Test Application", "Test Handler2");
    Application application1 = applicationDao.insert(linkApplication1);
    Application application2 = applicationDao.insert(linkApplication2);
    DefaultAttachmentInfo dai = newDefaultInfo();
    byte[] data = generateTestData(3243);
    DefaultAttachmentInfo inserted1 = attachmentDao.insertDefault(dai, data);
    DefaultAttachmentInfo inserted2 = attachmentDao.insertDefault(dai, data);
    attachmentDao.linkApplicationToAttachment(application1.getId(), inserted1.getId());
    attachmentDao.linkApplicationToAttachment(application1.getId(), inserted2.getId());
    attachmentDao.linkApplicationToAttachment(application2.getId(), inserted1.getId());
    attachmentDao.linkApplicationToAttachment(application2.getId(), inserted2.getId());
    Assert.assertEquals(2, attachmentDao.findByApplication(application1.getId()).size());
    Assert.assertEquals(2, attachmentDao.findByApplication(application2.getId()).size());
    attachmentDao.removeLinkApplicationToAttachment(application1.getId(), inserted1.getId());
    Assert.assertEquals(1, attachmentDao.findByApplication(application1.getId()).size());
  }

  // Setup helper: store a bunch of attachment infos
  private List<AttachmentInfo> storeInitialAttachments() {
    AttachmentInfo info = newInfo();
    List<AttachmentInfo> stored = new ArrayList<>();
    for (int i = 0; i < 20; ++i) {
      info.setName(String.format("Attachment_%d.txt", i));
      info.setDescription(String.format("Attachment %d", i));
      newApplication.setApplicationId(null);
      int applicationId = applicationDao.insert(newApplication).getId();
      AttachmentInfo attachmentInfo = attachmentDao.insert(applicationId, info, generateTestData(4321));
      TestAttachmentInfo testAttachmentInfo = new TestAttachmentInfo(attachmentInfo, applicationId);
      stored.add(testAttachmentInfo);
      System.out.println("Added " + stored.get(stored.size() - 1).getId() + " / appid " + applicationId);
    }
    return stored;
  }

  private class TestAttachmentInfo extends AttachmentInfo {
    public Integer applicationId = null;

    public TestAttachmentInfo(
        AttachmentInfo info,
        Integer applicationId) {
      super(info.getId(), info.getUserId(), info.getType(),
          "mimeType", info.getName(),
          info.getDescription(), info.getAttachmentDataId(),
          info.getCreationTime(), info.isDecisionAttachment());
      this.applicationId = applicationId;
    }
  }

  private AttachmentInfo newInfo() {
    AttachmentInfo info = new AttachmentInfo();
    info.setType(AttachmentType.ADDED_BY_CUSTOMER);
    info.setCreationTime(ZonedDateTime.now());
    info.setId(313);
    info.setName("Test_attachment.pdf");
    info.setDescription("Test attachment");
    return info;
  }

  private DefaultAttachmentInfo newDefaultInfo() {
    DefaultAttachmentInfo info = new DefaultAttachmentInfo();
    info.setType(AttachmentType.ADDED_BY_CUSTOMER);
    info.setCreationTime(ZonedDateTime.now());
    info.setId(313);
    info.setName("Test_attachment.pdf");
    info.setDescription("Test attachment");
    info.setApplicationTypes(Collections.singletonList(ApplicationType.EVENT));
    info.setFixedLocationAreaId(1);
    return info;
  }

  private void assertEquals(AttachmentInfo expected, AttachmentInfo actual) {
    Assert.assertEquals(expected.getId(), actual.getId());
    Assert.assertEquals(expected.getName(), actual.getName());
    Assert.assertEquals(expected.getDescription(), actual.getDescription());
    Assert.assertEquals(expected.getAttachmentDataId(), actual.getAttachmentDataId());
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
