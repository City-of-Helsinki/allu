package fi.hel.allu.model.dao;

import fi.hel.allu.common.types.ApplicationTagType;
import fi.hel.allu.common.types.ApplicationType;
import fi.hel.allu.model.ModelApplication;
import fi.hel.allu.model.domain.Application;
import fi.hel.allu.model.domain.ApplicationTag;
import fi.hel.allu.model.testUtils.TestCommon;

import org.junit.Assert;
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

  @Test
  public void testCreateApplicationIdString() {
    ApplicationSequenceDao applicationSequenceDaoMock = Mockito.mock(ApplicationSequenceDao.class);
    Mockito.when(applicationSequenceDaoMock.getNextValue(ApplicationSequenceDao.APPLICATION_TYPE_PREFIX.TP)).thenReturn(1600001L);
    ApplicationDao applicationDao = new ApplicationDao(null, applicationSequenceDaoMock);
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
    Application applOut = applicationDao.insert(application);

    assertEquals(application.getName(), applOut.getName());
    assertEquals(OVERRIDE_PRICE, applOut.getPriceOverride().intValue());
    assertEquals(OVERRIDE_REASON, applOut.getPriceOverrideReason());
    assertNotEquals(application.getCreationTime(), applOut.getCreationTime());
  }

  @Test
  public void testUpdateApplication() {
    Application application = testCommon.dummyOutdoorApplication("Test Application", "Test Handler");
    Application applOut = applicationDao.insert(application);
    applOut.setName("Updated application");
    applOut.setCreationTime(ZonedDateTime.parse("2015-12-03T10:15:30+02:00"));
    Application updated = applicationDao.update(applOut.getId(), applOut);

    assertEquals("Updated application", updated.getName());
    assertNotEquals(applOut.getCreationTime(), updated.getCreationTime());
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
