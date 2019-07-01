package fi.hel.allu.model.dao;

import fi.hel.allu.common.domain.TerminationInfo;
import fi.hel.allu.common.domain.types.StatusType;
import fi.hel.allu.common.exception.NoSuchEntityException;
import fi.hel.allu.common.util.TimeUtil;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class TerminationDaoTest {

  @Autowired
  TerminationDao terminationDao;

  @Autowired
  TestCommon testCommon;

  private Integer applicationId;
  private Integer userId;

  @Before
  public void setup() throws Exception {
    testCommon.deleteAllData();
    applicationId = testCommon.insertApplication("Testihakemus", "Käsittelijä");
    userId = testCommon.insertUser("testuser").getId();
  }

  @Test
  public void shouldInsertTerminationInfo() {
    TerminationInfo termination = createInfo(ZonedDateTime.now(), "Reason for termination");
    TerminationInfo created = terminationDao.insertTerminationInfo(applicationId, termination);
    assertEquals(applicationId, created.getApplicationId());
    assertNotNull(created.getCreationTime());
    assertEquals(termination.getReason(), created.getReason());
    assertEquals(termination.getExpirationTime(), TimeUtil.homeTime(created.getExpirationTime()));
  }

  @Test
  public void shouldGetExisting() {
    TerminationInfo termination = createInfo(ZonedDateTime.now(), "Reason for termination");
    terminationDao.insertTerminationInfo(applicationId, termination);
    TerminationInfo fetched = terminationDao.getTerminationInfo(applicationId);
    assertNotNull(fetched);
  }

  @Test
  public void shouldReturnNullOnNotExisting() {
    assertNull(terminationDao.getTerminationInfo(applicationId));
  }

  @Test
  public void shouldUpdateExisting() {
    TerminationInfo termination = createInfo(ZonedDateTime.now(), "Reason for termination");
    terminationDao.insertTerminationInfo(applicationId, termination);

    TerminationInfo update = createInfo(ZonedDateTime.now().plusDays(1), "Updated reason");
    terminationDao.updateTerminationInfo(applicationId, update);

    TerminationInfo updated = terminationDao.getTerminationInfo(applicationId);
    assertEquals(update.getExpirationTime(), TimeUtil.homeTime(updated.getExpirationTime()));
    assertEquals(update.getReason(), updated.getReason());
  }

  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowWhenUpdateNonExisting() {
    TerminationInfo update = createInfo(ZonedDateTime.now().plusDays(1), "Updated reason");
    terminationDao.updateTerminationInfo(applicationId, update);
  }

  @Test
  public void shouldUpdateDocumentOnExisting() {
    TerminationInfo termination = createInfo(ZonedDateTime.now(), "Reason for termination");
    terminationDao.insertTerminationInfo(applicationId, termination);

    String text = "Some document text here";
    byte[] document = text.getBytes();
    terminationDao.storeTerminationDocument(applicationId, document);

    byte[] storedDocument = terminationDao.getTerminationDocument(applicationId);
    assertEquals(text, new String(storedDocument, Charset.defaultCharset()));
  }

  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowWhenUpdateDocumentOnNonExisting() {
    String text = "Some document text here";
    byte[] document = text.getBytes();
    terminationDao.storeTerminationDocument(applicationId, document);
  }

  @Test(expected = NoSuchEntityException.class)
  public void shouldThrowWhenGetNonExistingDocument() {
    terminationDao.getTerminationDocument(applicationId);
  }

  @Test
  public void shouldGetTerminated() {
    Application app1 = testCommon.dummyBridgeBannerApplication("bridgeBanner1", "owner1");
    app1.setStatus(StatusType.TERMINATED);
    Integer id1 = testCommon.insertApplication(app1);
    terminationDao.insertTerminationInfo(id1, createInfo(ZonedDateTime.now().minusDays(5), "Reasons"));

    Application app2 = testCommon.dummyBridgeBannerApplication("bridgeBanner2", "owner2");
    app1.setStatus(StatusType.DECISIONMAKING);
    Integer id2 = testCommon.insertApplication(app2);
    terminationDao.insertTerminationInfo(id2, createInfo(ZonedDateTime.now().minusDays(5), "Reasons"));

    List<Integer> terminated = terminationDao.getTerminatedApplications();
    assertEquals(1, terminated.size());
    assertEquals(id1, terminated.get(0));
  }

  @Test
  public void shouldNotGetTerminationWithExpirationDateToday() {
    Application app1 = testCommon.dummyBridgeBannerApplication("bridgeBanner1", "owner");
    app1.setStatus(StatusType.TERMINATED);
    Integer id1 = testCommon.insertApplication(app1);
    terminationDao.insertTerminationInfo(id1, createInfo(ZonedDateTime.now(), "Reasons"));
    assertTrue(terminationDao.getTerminatedApplications().size() == 0);
  }

  @Test
  public void shouldGetTerminationWithExpirationDateInThePast() {
    Application app1 = testCommon.dummyBridgeBannerApplication("bridgeBanner1", "owner");
    app1.setStatus(StatusType.TERMINATED);
    Integer id1 = testCommon.insertApplication(app1);
    terminationDao.insertTerminationInfo(id1, createInfo(ZonedDateTime.now().minusDays(1), "Reasons"));
    assertTrue(terminationDao.getTerminatedApplications().size() == 1);
  }

  private TerminationInfo createInfo(ZonedDateTime expirationTime, String reason) {
    TerminationInfo terminationInfo = new TerminationInfo();
    terminationInfo.setExpirationTime(expirationTime);
    terminationInfo.setReason(reason);
    terminationInfo.setTerminator(userId);
    return terminationInfo;
  }
}
