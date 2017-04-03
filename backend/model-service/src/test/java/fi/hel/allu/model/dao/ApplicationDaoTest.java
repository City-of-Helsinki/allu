package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ApplicationTagType;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.common.types.DistributionType;
import fi.hel.allu.common.types.StatusType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.domain.DistributionEntry;
import fi.hel.allu.model.testUtils.TestCommon;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ModelApplication.class)
@WebAppConfiguration
@Transactional
public class ApplicationDaoTest {

  @Autowired
  private TestCommon testCommon;
  @Autowired
  private ApplicationDao applicationDao;
  @Autowired
  private DistributionEntryDao distributionEntryDao;

  DistributionEntry testDistributionEntry;

  @Before
  public void init() {
    testDistributionEntry = new DistributionEntry();
    testDistributionEntry.setDistributionType(DistributionType.PAPER);
    testDistributionEntry.setEmail("foobar@foo.fi");
  }
/*
  describe("Applicant dao with applications", () -> {
    beforeEach(() -> {
      insertedApplicant = applicantDao.insert(testApplicant);
      insertedApplicationId = testCommon.insertApplication("dummy application", "foo handler");
    });
    it("should not find applications by applicant id", () -> {
      List<Integer> applicationIds = applicantDao.findRelatedApplications(insertedApplicant.getId());
      assertTrue(applicationIds.isEmpty());
    });
    it("should find applications by applicant id", () -> {
      Application application = applicationDao.findByIds(Collections.singletonList(insertedApplicationId)).get(0);
      application.setApplicantId(insertedApplicant.getId());
      applicationDao.update(insertedApplicationId, application);
      List<Integer> applicationIds = applicantDao.findRelatedApplications(insertedApplicant.getId());
      assertEquals(1, applicationIds.size());
      assertEquals(insertedApplicationId, (int) applicationIds.get(0));
    });
  });
*/
  @Test
  public void testFindApplicationsByApplicant() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application insertedApplication = applicationDao.insert(application);
    List<Integer> applicationIds = applicationDao.findByApplicant(insertedApplication.getApplicantId());
    assertEquals(1, applicationIds.size());
    assertEquals(insertedApplication.getId(), applicationIds.get(0));
  }

  @Test
  public void testCreateApplicationIdString() {
    ApplicationSequenceDao applicationSequenceDaoMock = Mockito.mock(ApplicationSequenceDao.class);
    Mockito.when(applicationSequenceDaoMock.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP)).thenReturn(1600001L);
    StructureMetaDao structureMetaDaoMock = Mockito.mock(StructureMetaDao.class);
    Mockito.when(structureMetaDaoMock.getLatestMetadataVersion()).thenReturn(1);
    ApplicationDao applicationDao =
        new ApplicationDao(null, applicationSequenceDaoMock, distributionEntryDao, structureMetaDaoMock);
    Assert.assertEquals("TP1600001", applicationDao.createApplicationId(ApplicationType.EVENT));
  }

  @Test
  public void insertApplication() {
    final int OVERRIDE_PRICE = 1234567;
    final String OVERRIDE_REASON = "Just felt like it";

    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    application.setPriceOverride(OVERRIDE_PRICE);
    application.setPriceOverrideReason(OVERRIDE_REASON);
    application.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    application.setDecisionDistributionList(Collections.singletonList(testDistributionEntry));
    Application applOut = applicationDao.insert(application);

    assertEquals(application.getName(), applOut.getName());
    assertEquals(OVERRIDE_PRICE, applOut.getPriceOverride().intValue());
    assertEquals(OVERRIDE_REASON, applOut.getPriceOverrideReason());
    assertNotEquals(application.getCreationTime(), applOut.getCreationTime());
    assertEquals(1, applOut.getDecisionDistributionList().size());
  }

  @Test
  public void testUpdateApplication() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application applOut = applicationDao.insert(application);
    applOut.setName("Updated application");
    applOut.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    applOut.setDecisionDistributionList(Collections.singletonList(testDistributionEntry));
    Application updated = applicationDao.update(applOut.getId(), applOut);

    assertEquals("Updated application", updated.getName());
    assertNotEquals(applOut.getCreationTime(), updated.getCreationTime());
    assertEquals(1, updated.getDecisionDistributionList().size());
  }

  @Test
  public void testUpdateStatus() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application applOut = applicationDao.insert(application);
    Application updated = applicationDao.updateStatus(applOut.getId(), StatusType.CANCELLED);
    assertEquals(StatusType.CANCELLED, updated.getStatus());
  }

  @Test
  public void testDecisionStatus() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application applOut = applicationDao.insert(application);
    Application updated = applicationDao.updateDecision(applOut.getId(), StatusType.REJECTED, application.getHandler());
    assertEquals(StatusType.REJECTED, updated.getStatus());
    assertEquals(application.getHandler(), updated.getDecisionMaker());
    assertNotNull(updated.getDecisionTime());
  }

  @Test
  public void testInsertApplicationTags() {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    newApplication.setApplicationTags(Collections.singletonList(createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)));
    Application application = applicationDao.insert(newApplication);
    assertNotNull(application.getApplicationTags());
    assertEquals(1, application.getApplicationTags().size());
    assertEquals(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED, application.getApplicationTags().get(0).getType());
  }

  @Test
  public void testUpdateApplicationTags() {
    Application newApplication = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    newApplication.setApplicationTags(Collections.singletonList(
        createApplicationTag(ApplicationTagType.ADDITIONAL_INFORMATION_REQUESTED)));
    Application application = applicationDao.insert(newApplication);
    application.setApplicationTags(Arrays.asList(
        createApplicationTag(ApplicationTagType.COMPENSATION_CLARIFICATION),
        createApplicationTag(ApplicationTagType.DEPOSIT_PAID)));
    application = applicationDao.update(application.getId(), application);
    assertNotNull(application.getApplicationTags());
    assertEquals(2, application.getApplicationTags().size());
    assertTrue(application.getApplicationTags().stream()
        .filter(tag -> tag.getType().equals(ApplicationTagType.COMPENSATION_CLARIFICATION)).findFirst().isPresent());
    assertTrue(application.getApplicationTags().stream()
        .filter(tag -> tag.getType().equals(ApplicationTagType.DEPOSIT_PAID)).findFirst().isPresent());
  }

  private ApplicationTag createApplicationTag(ApplicationTagType applicationTagType) {
    ApplicationTag applicationTag = new ApplicationTag();
    applicationTag.setAddedBy(1);
    applicationTag.setCreationTime(ZonedDateTime.now());
    applicationTag.setType(applicationTagType);
    return applicationTag;
  }
}
